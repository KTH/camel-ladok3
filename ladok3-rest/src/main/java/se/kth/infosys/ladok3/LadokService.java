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
package se.kth.infosys.ladok3;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import se.ladok.schemas.dap.ServiceIndex;

/**
 * Abstract base class for Ladok REST services.
 */
public abstract class LadokService {
    protected final Client client;

    /**
     * Initialize the service client with authentication certificates.
     * @param host The targeted Ladok environment.
     * @param certFile The path to the certificate file.
     * @param key The certificate file key phrase.
     * @throws Exception on errors.
     */
    protected LadokService(String host, String certFile, String key) throws Exception {
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(new File(certFile)), key.toCharArray());

        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, key.toCharArray());

        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(kmf.getKeyManagers(), null, null);

        client = clientFactory(context);
    }

    /**
     * Initialize the service client with authentication certificates.
     * @param context the SSLContext containing necessary information.
     * @throws Exception on errors.
     */
    protected LadokService(SSLContext context) throws Exception {
        client = clientFactory(context);
    }

    /*
     * Private helper method.
     */
    private static Client clientFactory(SSLContext context) {
        return ClientBuilder.newBuilder().sslContext(context)
            .build()
            .register(Ladok3RequestFilter.class)
            .register(Ladok3ResponseFilter.class);
    }

    /**
     * Get the service index for the service.
     * 
     * NOTE: This could probably have been made generic in the base class, but
     * the "ACCEPT" types are different for each service. The idea to keep an abstract
     * definition here is to use it in order to provide a generic structure to make
     * lookups into this information. However, while the Ladok project says we should
     * use this index, why we should and for what purpose really escapes me, so I'm 
     * stalling it for now. - fjo 20161018
     * 
     * @return The service index
     */
    public abstract ServiceIndex serviceIndex();
}
