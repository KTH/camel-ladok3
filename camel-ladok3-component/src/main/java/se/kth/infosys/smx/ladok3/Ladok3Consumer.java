/*
 * MIT License
 *
 * Copyright (c) 2016 Kungliga Tekniska högskolan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package se.kth.infosys.smx.ladok3;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

import se.kth.infosys.smx.ladok3.internal.Ladok3Message;
import se.ladok.schemas.events.BaseEvent;

/**
 * The ladok3 consumer.
 */
public class Ladok3Consumer extends ScheduledPollConsumer {
    private static final String SCHEMAS_BASE_PACKAGE = "se.ladok.schemas";
    private static final String SCHEMA_BASE_URL = "http://schemas.ladok.se/";
    private static final String FIRST_FEED_FORMAT = "https://%s/handelser/feed/first";
    private static final String LAST_FEED_FORMAT = "https://%s/handelser/feed/recent";

    private final Ladok3Endpoint endpoint;
    private final Unmarshaller unmarshaller;
    private final DocumentBuilder builder;

    public Ladok3Consumer(Ladok3Endpoint endpoint, Processor processor) throws Exception {
        super(endpoint, processor);
        this.endpoint = endpoint;

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builder = builderFactory.newDocumentBuilder();

        unmarshaller = JAXBContext.newInstance(SCHEMAS_BASE_PACKAGE).createUnmarshaller();
    }

    @Override
    protected int poll() throws Exception {
        int messageCount = 0;
        Ladok3Feed feed;

        if ("".equals(endpoint.getLastFeed())) {
            feed = getLastUnreadFeed();
            endpoint.setLastFeedURL(feed.getURL());
        } else {
            feed = new Ladok3Feed(endpoint.getLastFeedURL());
        }

        for (SyndEntry entry : feed.unreadEntries()) {
            final SyndContent content = entry.getContents().get(0);

            if ("application/vnd.ladok+xml".equals(content.getType())) {
                final Document document = builder.parse(new InputSource(new StringReader(content.getValue())));
                final Node rootElement = document.getFirstChild();

                final JAXBElement<?> root = unmarshaller.unmarshal(rootElement, Class.forName(ladokEventClass(rootElement)));
                final BaseEvent event = (BaseEvent) root.getValue();

                doExchangeForEvent(event, entry.getUri(), feed);
                messageCount++;
            }
            endpoint.setLastEntry(entry.getUri());
        }

        log.info("Consumed Ladok ATOM feed {} up to id {}", 
                feed.getURL(),
                endpoint.getLastEntry());

        if (feed.isLast()) {
            endpoint.setLastFeedURL(feed.getURL());
        } else {
            endpoint.setLastFeedURL(feed.getLink("next-archive"));
        }

        return messageCount;
    }

    /*
     * Derive ladok3 event class name from namespace the same way xcj does,
     * but hard coded for ladok3 use case.
     */
    private String ladokEventClass(final Node rootElement) {
        final String eventClass = 
                SCHEMAS_BASE_PACKAGE
                + "."
                + rootElement.getNamespaceURI().substring(SCHEMA_BASE_URL.length()).replace("/", ".")
                + "."
                + rootElement.getLocalName();
        return eventClass;
    }

    /*
     * Generate exchange for Ladok event and dispatch to next processor.
     */
    private void doExchangeForEvent(BaseEvent event, String entryId, Ladok3Feed feed) throws Exception {
        final Exchange exchange = endpoint.createExchange();

        log.debug("Creating message for event: {} {}", event.getHandelseUID(), event.getClass().getName());

        final Message message = exchange.getIn();
        message.setHeader(Ladok3Message.Header.EntryId, entryId);
        message.setHeader(Ladok3Message.Header.Feed, feed.getURL().toString());
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
     * Get the latest feed not yet completed.
     */
    private Ladok3Feed getLastUnreadFeed() throws IOException, FeedException {
        if ("".equals(endpoint.getLastEntry())) {
            return new Ladok3Feed(new URL(String.format(FIRST_FEED_FORMAT, endpoint.getHost())));
        }

        Ladok3Feed feed = new Ladok3Feed(new URL(String.format(LAST_FEED_FORMAT, endpoint.getHost())));

        for (;;) {
            if (feed.containsEntry(endpoint.getLastEntry())) {
                return feed;
            }

            URL prevArchive = feed.getLink("prev-archive");
            if (prevArchive == null) {
                throw new FeedException("At end of the archive without finding Ladok3 event ID: '"
                        + endpoint.getLastEntry() + "'");
            }
            feed = new Ladok3Feed(prevArchive);
        }
    }

    /*
     * Inner class to wrap the SyndFeed with it's corresponding URL and convenvience methods.
     */
    private final class Ladok3Feed {
        private final SyndFeed feed;
        private final URL url;

        /*
         * Create the feed from the URL.
         */
        public Ladok3Feed(final URL url) throws IOException, IllegalArgumentException, FeedException {
            this.url = url;

            log.debug("fetching feed: {}", url);
            final XmlReader reader = new XmlReader(endpoint.get(url));
            final SyndFeedInput input = new SyndFeedInput();
            feed = input.build(reader);
        }

        /*
         * Return true if the feed contains an entry with given ID.
         */
        public boolean containsEntry(String entryId) {
            for (final SyndEntry entry : feed.getEntries()) {
                if (entry.getUri().equals(entryId)) {
                    return true;
                }
            }
            return false;
        }

        /*
         * True if feed is currently the last available.
         */
        private boolean isLast() throws MalformedURLException {
            return getLink("next-archive") == null;
        }

        /*
         * Return the URL for this feed.
         */
        public URL getURL() {
            return url;
        }

        /*
         * Get the link with specified rel label "next-archive", "prev-archive", etc, 
         * or null if the link does not exist.
         */
        public URL getLink(String rel) throws MalformedURLException {
            for (SyndLink link : feed.getLinks()) {
                if (link.getRel().equals(rel)) {
                    return new URL(link.getHref());
                }
            }
            return null;
        }

        /*
         * Get unread entries in feed in oldest to latest order.
         */
        private List<SyndEntry> unreadEntries() {
            final List<SyndEntry> entries = feed.getEntries();
            final List<SyndEntry> unmatchedEntries = new ArrayList<SyndEntry>(entries.size());

            Collections.reverse(entries);

            while (! entries.isEmpty()) {
                final SyndEntry entry = entries.remove(0);
                if (entry.getUri().equals(endpoint.getLastEntry())) {
                    return entries;
                }
                unmatchedEntries.add(entry);
            }

            return unmatchedEntries;
        }
    }
}