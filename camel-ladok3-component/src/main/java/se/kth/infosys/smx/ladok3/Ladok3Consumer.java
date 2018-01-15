/*
 * MIT License
 *
 * Copyright (c) 2017 Kungliga Tekniska högskolan
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
import java.util.Date;
import java.util.List;

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

import se.kth.infosys.smx.ladok3.internal.Ladok3UnmarshallerFactory;
import se.kth.infosys.smx.ladok3.utils.StockholmLocalDateTimeFormatter;
import se.ladok.schemas.events.BaseEvent;

/**
 * The ladok3 consumer. Will read Ladok3 events from the ATOM feed and create
 * exchanges with the event in the body in a Java POJO representation based
 * on the XSD:s published by Ladok. The exchange includes some headers with
 * event type, feed and event ids. The consumer has options to control where
 * in the feed archive to start reading events and the headers can be used to
 * persist information about the position in the feed to handle restarts.
 */
public class Ladok3Consumer extends ScheduledPollConsumer {
    private static final String SCHEMAS_BASE_PACKAGE = "se.ladok.schemas";
    private static final String SCHEMA_BASE_URL = "http://schemas.ladok.se/";
    private static final String FIRST_FEED_FORMAT = "https://%s/handelser/feed/first";
    private static final String LAST_FEED_FORMAT = "https://%s/handelser/feed/recent";

    private final Ladok3Endpoint endpoint;
    private final DocumentBuilder builder;
    private long sequenceNumber = 0;

    public Ladok3Consumer(Ladok3Endpoint endpoint, Processor processor) throws Exception {
        super(endpoint, processor);
        this.endpoint = endpoint;

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builder = builderFactory.newDocumentBuilder();
    }

    /*
     * Will fetch and handle one Ladok3 feed each poll until we are the last feed.
     * Handles any unread events for the last feed for each poll.
     * 
     * @see org.apache.camel.impl.ScheduledPollConsumer#poll()
     */
    @Override
    protected int poll() throws Exception {
        int messageCount = 0;
        int atomItemIndex = 0;

        Ladok3Feed feed = getLastUnreadFeed();

        endpoint.setNextURL(feed.getURL());

        List<SyndEntry> unreadEntries = feed.unreadEntries();

        if (unreadEntries.isEmpty()) {
            if (!feed.isLast()) {
                endpoint.setNextURL(feed.getLink(Ladok3Feed.NEXT));
            }
            return 0;
        }

        doControlExchange(feed, true);
        messageCount++;

        for (SyndEntry entry : unreadEntries) {
            SyndContent content = entry.getContents().get(0);

            if ("application/vnd.ladok+xml".equals(content.getType())) {
                Document document = builder.parse(new InputSource(new StringReader(content.getValue())));
                Node rootElement = document.getFirstChild();

                if (shouldHandleEvent(rootElement)) {
                    Class<?> eventClass = Class.forName(ladokEventClass(rootElement));
                    Unmarshaller unmarshaller = Ladok3UnmarshallerFactory.unmarshaller(eventClass.getPackage().getName());
                    JAXBElement<?> root = unmarshaller.unmarshal(rootElement, eventClass);
                    BaseEvent event = (BaseEvent) root.getValue();

                    doExchangeForEvent(event, entry.getUri(), feed, entry.getUpdatedDate(), atomItemIndex);
                    messageCount++;
                    atomItemIndex++;
                }
            }
            endpoint.setLastEntry(entry.getUri());
        }

        doControlExchange(feed, false);
        messageCount++;

        log.debug("Consumed Ladok ATOM feed {} up to id {}", feed.getURL(), endpoint.getLastEntry());

        if (feed.isLast()) {
            endpoint.setNextURL(feed.getURL());
        } else {
            endpoint.setNextURL(feed.getLink(Ladok3Feed.NEXT));
        }

        return messageCount;
    }

    private boolean shouldHandleEvent(Node rootElement) {
        return (endpoint.getIncludeEvents().isEmpty() || endpoint.getIncludeEvents().contains(rootElement.getLocalName()))
                && !endpoint.getExcludeEvents().contains(rootElement.getLocalName());
    }

    /*
     * Derive Ladok3 event class name from namespace the same way xcj does,
     * but hard coded for ladok3 use case.
     */
    private String ladokEventClass(final Node rootElement) {
        return String.format("%s.%s.%s",
                SCHEMAS_BASE_PACKAGE,
                rootElement.getNamespaceURI().substring(SCHEMA_BASE_URL.length()).replace("/", "."),
                rootElement.getLocalName());
    }

    /*
     * Generate start of feed exchange
     */
    private void doControlExchange(final Ladok3Feed feed, final boolean start) throws Exception {
        Exchange exchange = endpoint.createExchange();

        Message message = exchange.getIn();
        message.setHeader(Ladok3Message.Header.SequenceNumber, sequenceNumber++);
        message.setHeader(Ladok3Message.Header.Feed, feed.getURL().toString());
        message.setHeader(Ladok3Message.Header.EntryId, endpoint.getLastEntry());
        message.setHeader(Ladok3Message.Header.IsLastFeed, feed.isLast());
        if (start) {
            message.setHeader(Ladok3Message.Header.MessageType, Ladok3Message.MessageType.Start);
        } else {
            message.setHeader(Ladok3Message.Header.MessageType, Ladok3Message.MessageType.Done);
        }

        try {
            getProcessor().process(exchange);
        } finally {
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }

    /*
     * Generate exchange for Ladok3 event and dispatch to next processor.
     */
    private void doExchangeForEvent(BaseEvent event, String entryId, Ladok3Feed feed, Date entryUpdated, int atomItemIndex) throws Exception {
        Exchange exchange = endpoint.createExchange();

        Message message = exchange.getIn();
        message.setHeader(Ladok3Message.Header.SequenceNumber, sequenceNumber++);
        message.setHeader(Ladok3Message.Header.MessageType, Ladok3Message.MessageType.Event);
        message.setHeader(Ladok3Message.Header.EntryId, entryId);
        message.setHeader(Ladok3Message.Header.EntryUpdated, StockholmLocalDateTimeFormatter.formatAsStockolmLocalDateTime(entryUpdated));
        message.setHeader(Ladok3Message.Header.Feed, feed.getURL().toString());
        message.setHeader(Ladok3Message.Header.IsLastFeed, feed.isLast());
        message.setHeader(Ladok3Message.Header.EventType, event.getClass().getName());
        message.setHeader(Ladok3Message.Header.EventId, event.getHandelseUID());
        message.setHeader(Ladok3Message.Header.EntryItemIndex,  atomItemIndex);
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
     * Get the latest feed not yet completed. Will return the first URL if no 
     * events have been read, last known URL if any, or search for the feed from
     * the most recent to the one containing the last known event. The latter may 
     * take a *lot* of time if the consumer is not run regularly.
     */
    private Ladok3Feed getLastUnreadFeed() throws IOException, FeedException {
        if ("".equals(endpoint.getLastFeed()) && "".equals(endpoint.getLastEntry())) {
            // No info about where to begin, start from beginning.
            return new Ladok3Feed(new URL(String.format(FIRST_FEED_FORMAT, endpoint.getHost())));
        }
        if (!"".equals(endpoint.getLastFeed())) {
            // We have a previous feed, return it.
            return new Ladok3Feed(endpoint.getNextURL());
        }

        // We have a previous entry but no feed, try to find entry,
        // starting at the end working towards beginning.
        Ladok3Feed feed = new Ladok3Feed(new URL(String.format(LAST_FEED_FORMAT, endpoint.getHost())));

        for (;;) {
            if (feed.containsEntry(endpoint.getLastEntry())) {
                return feed;
            }

            URL prevArchive = feed.getLink(Ladok3Feed.PREV);
            if (prevArchive == null) {
                throw new FeedException("At end of the archive without finding Ladok3 event ID: '"
                        + endpoint.getLastEntry() + "'");
            }
            feed = new Ladok3Feed(prevArchive);
        }
    }

    /*
     * Inner class to wrap the SyndFeed with it's corresponding URL and convenience methods.
     */
    private final class Ladok3Feed {
        private final SyndFeed feed;
        private final URL url;

        private static final String NEXT = "next-archive";
        private static final String PREV = "prev-archive";

        /*
         * Create the feed from the URL.
         */
        public Ladok3Feed(final URL url) throws IOException, IllegalArgumentException, FeedException {
            this.url = url;

            log.debug("fetching feed: {}", url);
            XmlReader reader = new XmlReader(endpoint.get(url));
            SyndFeedInput input = new SyndFeedInput();
            feed = input.build(reader);
            reader.close();
        }

        /*
         * Return true if the feed contains an entry with given ID.
         */
        public boolean containsEntry(final String entryId) {
            for (SyndEntry entry : feed.getEntries()) {
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
            return getLink(NEXT) == null;
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
        public URL getLink(final String rel) throws MalformedURLException {
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
            List<SyndEntry> entries = feed.getEntries();
            List<SyndEntry> unmatchedEntries = new ArrayList<SyndEntry>(entries.size());

            Collections.reverse(entries);

            while (!entries.isEmpty()) {
                SyndEntry entry = entries.remove(0);
                if (entry.getUri().equals(endpoint.getLastEntry())) {
                    return entries;
                }
                unmatchedEntries.add(entry);
            }

            return unmatchedEntries;
        }
    }
}