package se.kth.infosys.smx.ladok3;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import se.ladok.schemas.events.BaseEvent;

/**
 * The ladok3 consumer.
 */
public class Ladok3Consumer extends ScheduledPollConsumer {
    private final Ladok3Endpoint endpoint;
    private final Unmarshaller unmarshaller = JAXBContext.newInstance("se.ladok.schemas").createUnmarshaller();
    private static final Map<String, String> CLASSES = new HashMap<String, String>();

    public Ladok3Consumer(Ladok3Endpoint endpoint, Processor processor) throws Exception {
        super(endpoint, processor);
        this.endpoint = endpoint;

        CLASSES.put("se.ladok.utbildningsinformation.interfaces.events.utbildningstillfalle.KurstillfälleTillStatusEvent", "se.ladok.schemas.utbildningsinformation.KurstillfalleTillStatusEvent");
        CLASSES.put("se.ladok.utbildningsinformation.interfaces.events.utbildning.KurspaketeringTillStatusEvent", "se.ladok.schemas.utbildningsinformation.KurspaketeringTillStatusEvent");
        CLASSES.put("se.ladok.utbildningsinformation.interfaces.events.struktur.StrukturEvent", "se.ladok.schemas.utbildningsinformation.StrukturEvent");
    }

    @Override
    protected int poll() throws Exception {
        final URL feedUrl = rewindFeed(new URL(String.format("https://%s/handelser/feed/recent", endpoint.getHost())));
        int i = 0;

        if (feedUrl == null) {
            log.debug("Ladok feed ID: {} is up to date, nothing to do.", endpoint.getFeedId());
            return i;
        }

        log.debug("Start fetching events from: {}", feedUrl);
        final SyndFeed feed = getFeed(feedUrl);
        for (SyndEntry entry : feed.getEntries()) {
            final SyndContent content = entry.getContents().get(0);
            final String category = entry.getCategories().get(0).getName();

            if ("application/vnd.ladok+xml".equals(content.getType())) {
                if (CLASSES.containsKey(category)) {
                    Source source = new StreamSource(new StringReader(content.getValue()));
                    JAXBElement<?> root = unmarshaller.unmarshal(source, Class.forName(CLASSES.get(category)));
                    BaseEvent event = (BaseEvent) root.getValue();
                    log.debug("Got event: {} {}", event.getHandelseUID(), event.getClass().getName());

//                    if (event instanceof KurstillfalleTillStatusEvent) {
//                        KurstillfalleTillStatusEvent realEvent = (KurstillfalleTillStatusEvent) event;
//                    }
//                    if (event instanceof KurspaketeringTillStatusEvent) {
//                        KurspaketeringTillStatusEvent realEvent = (KurspaketeringTillStatusEvent) event;
//                    }
                    doExchangeForEvent(event);
                    i++;
                } else {
                    log.error("Unknown Ladok type: {}", category);
                }
            }
        }
        return i;
    }

    private void doExchangeForEvent(BaseEvent event) throws Exception {
        final Exchange exchange = endpoint.createExchange();

        exchange.getIn().setBody(event);

        try {
            // send message to next processor in the route
            getProcessor().process(exchange);
        } finally {
            // log exception if an exception occurred and was not handled
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

    private int feedId(SyndFeed feed) {
        return Integer.parseInt(feed.getUri().trim().substring(7)); // "getUri() -> urn:id:123"
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
            return getLink("self", feed.getLinks());
        }

        return rewindFeed(prevArchive);
    }
}