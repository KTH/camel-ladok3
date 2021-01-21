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
import java.net.CookieHandler;
import java.net.CookieManager;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * Abstract base class for Ladok REST services.
 */
abstract class AbstractLadok3Service implements Ladok3Service {
  private static final MediaType SERVICE_TYPE = new MediaType("application", "vnd.ladok+xml");

  /**
   * The constructed web target to use in sub classes.
   */
  protected final WebTarget target;

  static {
    if (CookieHandler.getDefault() == null) {
      CookieManager cookieManager = new CookieManager();
      CookieHandler.setDefault(cookieManager);
    }
  }

  /**
   * Initialize the service client with authentication certificates using a PKCS12
   * certificate file and key.
   *
   * @param host     The targeted Ladok environment, e.g mit-integration.ladok.se.
   * @param certFile The path to the certificate file.
   * @param key      The certificate file key phrase.
   * @param service  The Ladok3 service path, e.g. "studentinformation".
   * @throws Exception on errors.
   */
  protected AbstractLadok3Service(
          final String host,
          final String certFile,
          final String key,
          final String service) throws Exception {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    keyStore.load(new FileInputStream(new File(certFile)), key.toCharArray());

    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(keyStore, key.toCharArray());

    SSLContext context = SSLContext.getInstance("TLS");
    context.init(kmf.getKeyManagers(), null, null);

    target = clientFactory(context).target(String.format("https://%s/%s", host, service));
  }

  /**
   * Initialize the service client with authentication certificates using
   * a SSLContext configured by some other means in the application.
   *
   * @param host    The targeted Ladok environment, e.g mit-integration.ladok.se.
   * @param context the SSLContext containing necessary information.
   * @param service The Ladok3 service path, e.g. "studentinformation".
   * @throws Exception on errors.
   */
  protected AbstractLadok3Service(
          final String host,
          final SSLContext context,
          final String service) throws Exception {
    target = clientFactory(context).target(String.format("https://%s/%s", host, service));
  }

  /*
   * Private helper method.
   */
  private static Client clientFactory(final SSLContext context) {
    return ClientBuilder.newBuilder().sslContext(context)
            .build()
            .register(RequestFilter.class)
            .register(ResponseFilter.class);
  }
}
