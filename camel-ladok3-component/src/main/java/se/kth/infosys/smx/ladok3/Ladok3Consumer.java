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
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import se.kth.infosys.smx.ladok3.internal.Ladok3Message;
import se.ladok.schemas.events.BaseEvent;

/**
 * The ladok3 consumer.
 */
public class Ladok3Consumer extends ScheduledPollConsumer {
    private final Ladok3Endpoint endpoint;
    private final Unmarshaller unmarshaller = JAXBContext.newInstance("se.ladok.schemas").createUnmarshaller();

    // This is a horrible hack. The ATOM feed should be fixed so that we don't have to
    // keep a map between the ATOM entry category and the XSD class representation.
    // fjo 2016-06-21
    private static final Map<String, String> CATEGORY_TO_CLASS_MAP = new HashMap<String, String>();

    public Ladok3Consumer(Ladok3Endpoint endpoint, Processor processor) throws Exception {
        super(endpoint, processor);
        this.endpoint = endpoint;

        CATEGORY_TO_CLASS_MAP.put("se.ladok.utbildningsinformation.interfaces.events.utbildningstillfalle.KurstillfälleTillStatusEvent", "se.ladok.schemas.utbildningsinformation.KurstillfalleTillStatusEvent");
        CATEGORY_TO_CLASS_MAP.put("se.ladok.utbildningsinformation.interfaces.events.utbildning.KurspaketeringTillStatusEvent", "se.ladok.schemas.utbildningsinformation.KurspaketeringTillStatusEvent");
        CATEGORY_TO_CLASS_MAP.put("se.ladok.utbildningsinformation.interfaces.events.struktur.StrukturEvent", "se.ladok.schemas.utbildningsinformation.StrukturEvent");
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
                    final String category = entry.getCategories().get(0).getName();

                    if (CATEGORY_TO_CLASS_MAP.containsKey(category)) {
                        Source source = new StreamSource(new StringReader(content.getValue()));
                        JAXBElement<?> root = unmarshaller.unmarshal(source, Class.forName(CATEGORY_TO_CLASS_MAP.get(category)));
                        BaseEvent event = (BaseEvent) root.getValue();

                        doExchangeForEvent(event, feedId(feed), entry.getUri());
                        messageCount++;
                    } else {
                        log.error("Unknown Ladok type: {}", category);
                    }
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
        for (SyndEntry entry : entries) {
            if (entry.getUri().equals(entryId)) {
                return true;
            }
        }
        return false;
    }

    private List<SyndEntry> unreadEntries(SyndFeed feed) {
        List<SyndEntry> entries = feed.getEntries();
        Collections.reverse(entries);
        if (entriesContainsEntry(feed.getEntries(), endpoint.getLastEntry()) && ! entries.isEmpty()) {
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