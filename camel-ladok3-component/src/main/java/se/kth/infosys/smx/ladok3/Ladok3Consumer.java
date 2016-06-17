package se.kth.infosys.smx.ladok3;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

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
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
        log.debug("loading keystore {} using pw {}", endpoint.getCert(), endpoint.getKey());
        keyStore.load(new FileInputStream(new File(endpoint.getCert())), endpoint.getKey().toCharArray());

        log.debug("Initializing key manager");
        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, endpoint.getKey().toCharArray());

        log.debug("Initializing trust manager");
        final TrustManagerFactory tmf = 
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        log.debug("Creating ssl context");
        final SSLContext serverContext = SSLContext.getInstance("TLS");
        serverContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom());

        final SSLEngine engine = serverContext.createSSLEngine();
        final SSLParameters sslParams = new SSLParameters();
        sslParams.setNeedClientAuth(true);
        engine.setSSLParameters(sslParams);

        final SSLSocketFactory socketFactory = serverContext.getSocketFactory();
        HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);
        log.debug("setup ssl done");
    }

    @Override
    protected int poll() throws Exception {
        URL feedUrl = new URL("https://" + endpoint.getHost() + "/handelser/feed/recent");
        XmlReader reader = new XmlReader(feedUrl);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(reader);
//        List<SyndEntry> entries = feed.getEntries();
        System.out.println(feed);

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
