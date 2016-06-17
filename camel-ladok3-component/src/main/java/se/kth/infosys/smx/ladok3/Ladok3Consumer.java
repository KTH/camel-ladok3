package se.kth.infosys.smx.ladok3;

import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
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
    	URL feedUrl = new URL("https://" + endpoint.getHost() + "/handelser/feed/recent");

    	HttpsURLConnection connection = (HttpsURLConnection) feedUrl.openConnection();
        connection.setSSLSocketFactory(endpoint.getSocketFactory());
        XmlReader reader = new XmlReader(connection.getInputStream());

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(reader);
        List<SyndEntry> entries = feed.getEntries();

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
}
