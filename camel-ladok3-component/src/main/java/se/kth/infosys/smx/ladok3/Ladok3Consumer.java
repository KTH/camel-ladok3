package se.kth.infosys.smx.ladok3;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import se.kth.infosys.smx.ladok3.internal.Ladok3Message;
import se.ladok.schemas.events.BaseEvent;

/**
 * The ladok3 consumer.
 */
public class Ladok3Consumer extends ScheduledPollConsumer {
    private final Ladok3Endpoint endpoint;
    private final Unmarshaller unmarshaller = JAXBContext.newInstance("se.ladok.schemas").createUnmarshaller();

    public Ladok3Consumer(Ladok3Endpoint endpoint, Processor processor) throws Exception {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }

    @Override
    protected int poll() throws Exception {
        int messageCount = 0;

        log.info("Getting Ladok events, last read ID was: {}", endpoint.getLastEntry());

        SyndFeed feed = getLastUnreadFeed(new URL(String.format("https://%s/handelser/feed/recent", endpoint.getHost())));

        do {
            log.info("Getting Ladok events for feed ID {}", feedId(feed));

            List<SyndEntry> entries = unreadEntries(feed);
            for (SyndEntry entry : entries) {
                final SyndContent content = entry.getContents().get(0);

                if ("application/vnd.ladok+xml".equals(content.getType())) {

                    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                    builderFactory.setNamespaceAware(true);
                    DocumentBuilder builder = builderFactory.newDocumentBuilder();
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(content.getValue()));
                    Document document = builder.parse(is);
                    Node rootElement = document.getFirstChild();

                    final String category = "se.ladok.schemas." 
                            + rootElement.getNamespaceURI().substring("http://schemas.ladok.se/".length())
                            + "."
                            + rootElement.getLocalName();

                    JAXBElement<?> root = unmarshaller.unmarshal(rootElement, Class.forName(category));
                    BaseEvent event = (BaseEvent) root.getValue();

                    doExchangeForEvent(event, feedId(feed), entry.getUri());
                    messageCount++;
                    endpoint.setLastEntry(entry.getUri());
                }
            }
            if (isLast(feed)) {
                log.info("Done getting Ladok events.");
                return messageCount;
            }
            feed = getFeed(getLink("next-archive", feed.getLinks()));
        } while (true);
    }

    private void doExchangeForEvent(BaseEvent event, long feedId, String entryId) throws Exception {
        final Exchange exchange = endpoint.createExchange();

        log.debug("Creating message for event: {} {}", event.getHandelseUID(), event.getClass().getName());

        final Message message = exchange.getIn();
        message.setHeader(Ladok3Message.Header.FeedId, Long.toString(feedId));
        message.setHeader(Ladok3Message.Header.EntryId, entryId);
        message.setHeader(Ladok3Message.Header.EventType, event.getClass().getName());
        message.setHeader(Ladok3Message.Header.EventId, event.getHandelseUID());
        message.setBody(event);

        try {
            getProcessor().process(exchange);
        } finally {
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }

    /*
     * Get feed from URL.
     */
    private SyndFeed getFeed(URL feedUrl) throws IOException, FeedException {
        log.debug("fetching feed: {}", feedUrl);
        final XmlReader reader = new XmlReader(endpoint.get(feedUrl));
        final SyndFeedInput input = new SyndFeedInput();
        return input.build(reader);
    }

    /*
     * Return URL for link with given "rel" label.
     */
    private URL getLink(String rel, List<SyndLink> links) throws MalformedURLException {
        for (SyndLink link : links) {
            if (link.getRel().equals(rel)) {
                return new URL(link.getHref());
            }
        }
        return null;
    }

    /*
     * Return the Ladok3 feed ID of the feed as a number.
     */
    private long feedId(SyndFeed feed) {
        return Long.parseLong(feed.getUri().trim().substring(7)); // "getUri() -> urn:id:123"
    }

    /*
     * True if this is the last available feed.
     */
    private boolean isLast(SyndFeed feed) throws MalformedURLException {
        return getLink("next-archive", feed.getLinks()) == null;
    }

    /*
     * Return true if the feed contains an entry with given ID.
     */
    private boolean entriesContainsEntry(List<SyndEntry> entries, String entryId) {
        for (final SyndEntry entry : entries) {
            if (entry.getUri().equals(entryId)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Get unread entries in feed in oldest to latest order.
     */
    private List<SyndEntry> unreadEntries(SyndFeed feed) {
        List<SyndEntry> entries = feed.getEntries();
        if (entries.isEmpty()) {
            return entries;
        }

        Collections.reverse(entries);
        if (entriesContainsEntry(feed.getEntries(), endpoint.getLastEntry())) {
            SyndEntry entry; 
            do {
                entry = entries.remove(0);
            } while(! entry.getUri().equals(endpoint.getLastEntry()));
        }
        return entries;
    }

    /*
     * Given a feed URL, get the latest feed not yet completed.
     */
    private SyndFeed getLastUnreadFeed(URL url) throws IOException, FeedException {
        final SyndFeed feed = getFeed(url);

        if (! entriesContainsEntry(feed.getEntries(), endpoint.getLastEntry())) {
            final URL prevArchive = getLink("prev-archive", feed.getLinks());
            if (prevArchive == null) {
                return feed;
            }
            return getLastUnreadFeed(prevArchive);
        }

        return feed;
    }
}