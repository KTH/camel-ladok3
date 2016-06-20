package se.kth.infosys.smx.ladok3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

/**
 * The ladok3 consumer.
 */
public class Ladok3Consumer extends ScheduledPollConsumer {
    private final Ladok3Endpoint endpoint;

    public Ladok3Consumer(Ladok3Endpoint endpoint, Processor processor) throws Exception {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }

    @Override
    protected int poll() throws Exception {
        URL feedUrl = rewindFeed(new URL(String.format("https://%s/handelser/feed/recent", endpoint.getHost())));

        if (feedUrl == null) {
            log.debug("Ladok feed ID: {} is up to date, nothing to do.", endpoint.getFeedId());
            return 0;
        }

        log.debug("Start fetching events from: {}", feedUrl);

        Exchange exchange = endpoint.createExchange();

        // create a message body
        Date now = new Date();
        exchange.getIn().setBody("Hello World! The time is " + now);

        try {
            // send message to next processor in the route
            getProcessor().process(exchange);
            return 1; // number of messages polled
        } finally {
            // log exception if an exception occurred and was not handled
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }

    private SyndFeed getFeed(URL feedUrl) throws IOException, FeedException {
        log.debug("fetching feed: {}", feedUrl);
        XmlReader reader = new XmlReader(endpoint.get(feedUrl));
        SyndFeedInput input = new SyndFeedInput();
        return input.build(reader);
    }

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

    private URL rewindFeed(URL url) throws IOException, FeedException {
        SyndFeed feed = getFeed(url);

        if (feedId(feed) == endpoint.getFeedId()) {
            return getLink("next-archive", feed.getLinks());
        }

        URL prevArchive = getLink("prev-archive", feed.getLinks());
        if (prevArchive == null) {
            return getLink("via", feed.getLinks());
        }

        return rewindFeed(prevArchive);
    }
}