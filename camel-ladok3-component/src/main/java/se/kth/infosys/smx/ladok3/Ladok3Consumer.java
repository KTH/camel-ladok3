package se.kth.infosys.smx.ladok3;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
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

        log.info("Getting Ladok events, last read feed ID was: {}", endpoint.getFeedId());

        final URL feedUrl = rewindFeed(new URL(String.format("https://%s/handelser/feed/recent", endpoint.getHost())));

        if (feedUrl == null) {
            log.debug("Ladok feed ID: {} is up to date, nothing to do", endpoint.getFeedId());
            return messageCount;
        }

        final SyndFeed feed = getFeed(feedUrl);
        log.info("Getting Ladok events for feed ID {}, URL: {}", feedId(feed), feedUrl);

        sendControlMessage(Ladok3Message.MessageType.StartFeed, feedId(feed), ++messageCount);

        for (SyndEntry entry : feed.getEntries()) {
            final SyndContent content = entry.getContents().get(0);
            final String category = entry.getCategories().get(0).getName();

            if ("application/vnd.ladok+xml".equals(content.getType())) {
                if (CATEGORY_TO_CLASS_MAP.containsKey(category)) {
                    Source source = new StreamSource(new StringReader(content.getValue()));
                    JAXBElement<?> root = unmarshaller.unmarshal(source, Class.forName(CATEGORY_TO_CLASS_MAP.get(category)));
                    BaseEvent event = (BaseEvent) root.getValue();

                    doExchangeForEvent(event, feedId(feed), ++messageCount);
                } else {
                    log.error("Unknown Ladok type: {}", category);
                }
            }
        }

        sendControlMessage(Ladok3Message.MessageType.EndFeed, feedId(feed), ++messageCount);

        endpoint.setFeedId(feedId(feed));
        log.info("Done getting Ladok events for feed ID {}", endpoint.getFeedId());
        return messageCount;
    }

    private void doExchangeForEvent(BaseEvent event, long feedId, int n) throws Exception {
        final Exchange exchange = endpoint.createExchange();

        log.debug("Creating message for event: {} {}", event.getHandelseUID(), event.getClass().getName());

        final Message message = exchange.getIn();
        message.setHeader(Ladok3Message.Header.MessageType, Ladok3Message.MessageType.Event);
        message.setHeader(Ladok3Message.Header.FeedId, Long.toString(feedId));
        message.setHeader(Ladok3Message.Header.GroupID, Long.toString(feedId));
        message.setHeader(Ladok3Message.Header.GroupSeq, Integer.toString(n));
        message.setHeader(Ladok3Message.Header.EventType, event.getClass().getName());
        message.setBody(event);

        try {
            getProcessor().process(exchange);
        } finally {
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }

    protected void sendControlMessage(String messageType, long feedId, int n) throws Exception {
        Exchange exchange = endpoint.createExchange();
        try {
            Message message = exchange.getIn();
            message.setHeader(Ladok3Message.Header.MessageType, messageType);
            message.setHeader(Ladok3Message.Header.FeedId, Long.toString(feedId));
            message.setHeader(Ladok3Message.Header.GroupID, Long.toString(feedId));
            message.setHeader(Ladok3Message.Header.GroupSeq, Integer.toString(n));
            getProcessor().process(exchange);
        } finally {
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }

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
     * Given a feed URL, find the latest unread feed URL, or null if there is no unread feed.
     */
    private URL rewindFeed(URL url) throws IOException, FeedException {
        final SyndFeed feed = getFeed(url);

        if (feedId(feed) == endpoint.getFeedId()) {
            return getLink("next-archive", feed.getLinks());
        }

        final URL prevArchive = getLink("prev-archive", feed.getLinks());
        if (prevArchive == null) {
            return getLink("via", feed.getLinks());
        }

        return rewindFeed(prevArchive);
    }
}