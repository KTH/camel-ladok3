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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultPollingEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

/**
 * Represents a ladok3 endpoint.
 */
@UriEndpoint(scheme = "ladok3", title = "ladok3", syntax="ladok3://host?cert=file&key=password", consumerClass = Ladok3Consumer.class, label = "ladok3")
public class Ladok3Endpoint extends DefaultPollingEndpoint {
    @UriPath
    @Metadata(required = "true")
    private String host;

    @UriParam(name = "cert", description = "Path to file containing certificate in PKCS12 format")
    @Metadata(required = "true")
    private String cert;

    @UriParam(name = "key", description = "Private key for certificate")
    @Metadata(required = "true")
    private String key;

    @UriParam(label = "consumer", name = "lastEntry", defaultValue = "", description = "Entry id to start consuming from")
    private String lastEntry = "";

    @UriParam(label = "consumer", name = "lastFeed", defaultValue = "", description = "Feed to start consuming from")
    private String lastFeed = "";

    @UriParam(label = "consumer", name = "includeEvents", description = "List of event names to generate messages for.")
    private HashSet<String> includeEvents = new HashSet<String>();

    @UriParam(label = "consumer", name = "excludeEvents", description = "List of event names NOT to generate messages for.")
    private HashSet<String> excludeEvents = new HashSet<String>();

    @UriPath(label = "producer", description = "Ladok3 REST API path")
    private String api;

    private SSLContext context;

    public Ladok3Endpoint(String uri, Ladok3Component component) throws Exception {
        super(uri, component);
    }

    public Producer createProducer() throws Exception {
        return new Ladok3Producer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        Ladok3Consumer consumer = new Ladok3Consumer(this, processor);
        configureConsumer(consumer);
        return consumer;
    }
    
    public boolean isSingleton() {
        return false;
    }

    public InputStream get(final URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setReadTimeout(60000);
        connection.setConnectTimeout(10000);
        connection.setSSLSocketFactory(context.getSocketFactory());
        return connection.getInputStream();
    }

    /**
     * Path to certificate file.
     * @return the path
     */
    public String getCert() {
        return cert;
    }

    /**
     * Path to certificate file
     * @param cert the path to the certificate file.
     */
    public void setCert(String cert) {
        this.cert = cert;
    }

    /**
     * Private key for the certificate file.
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Private key for the certificate file.
     * @param key the key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * The Ladok3 host environment, api.mit-ik.ladok.se etc.
     * @return the host name
     */
    public String getHost() {
        return host;
    }

    /**
     * The ladok3 host environment, api.mit-ik.ladok.se etc.
     * @param host the fully qualified host name.
     */
    public void setHost(String host) {
        this.host = host;
    }

    public String getLastEntry() {
        return lastEntry;
    }

    public void setLastEntry(String lastEntry) {
        this.lastEntry = lastEntry;
    }

    public String getLastFeed() {
        return lastFeed;
    }

    public void setLastFeed(String lastFeed) {
        this.lastFeed = lastFeed;
    }

    public void setNextURL(URL lastURL) {
        this.lastFeed = lastURL.toString();
    }

    public URL getNextURL() throws MalformedURLException {
        return new URL(this.lastFeed);
    }

    public SSLContext getContext() {
        return context;
    }

    public void setContext(SSLContext context) {
        this.context = context;
    }

    public HashSet<String> getIncludeEvents() {
        return includeEvents;
    }

    public void setIncludeEvents(HashSet<String> events) {
        this.includeEvents = events;
    }

    public void setIncludeEvents(String events) {
        this.includeEvents = new HashSet<String>();
        for (String event : events.split(",")) {
            if (! event.isEmpty()) {
                this.includeEvents.add(event);
            }
        }
    }

    public HashSet<String> getExcludeEvents() {
        return excludeEvents;
    }

    public void setExcludeEvents(HashSet<String> events) {
        this.excludeEvents = events;
    }

    public void setExcludeEvents(String events) {
        this.excludeEvents = new HashSet<String>();
        for (String event : events.split(",")) {
            if (! event.isEmpty()) {
                this.excludeEvents.add(event);
            }
        }
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }
}
