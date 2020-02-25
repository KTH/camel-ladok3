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

import java.io.File;
import java.io.FileInputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;
import org.apache.camel.spi.Metadata;

/**
 * Represents the component that manages {@link Ladok3Endpoint}.
 */
public class Ladok3Component extends DefaultComponent {
    @Metadata(required = true)
    private String cert;

    @Metadata(required = true)
    private String key;

    @Metadata(required = true)
    private String host;

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        if (CookieHandler.getDefault() == null) {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
        }

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(new File(cert)), key.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, key.toCharArray());

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(kmf.getKeyManagers(), null, null);

        Ladok3Endpoint endpoint = new Ladok3Endpoint(uri, this, getHost(), context);
        setProperties(endpoint, parameters);

        return endpoint;
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
     * Set Ladok3 host environment, api.mit-ik.ladok.se etc.
     * @param host Ladok3 host environment.
     */
    public void setHost(String host) {
        this.host = host;
    }
}
