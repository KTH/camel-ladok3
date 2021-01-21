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

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.MediaType;

/**
 * Real implementation of the Ladok utbildningsinformation service. It is using JAX RS
 * which means that errors will be thrown as unchecked runtime exceptions. See
 * JAX RS client documentation.
 */
public class UtbildningsinformationServiceImpl extends AbstractLadok3Service implements UtbildningsinformationService {
  private static final MediaType SERVICE_TYPE = new MediaType("application",
          "vnd.ladok-utbdildningsinformation+xml");
  private static final String SERVICE = "utbildningsinformation";

  /**
   * Constructor Web Service client end representing the Ladok utbildningsinformation endpoint.
   *
   * @param host     The hostname of the targeted Ladok environment, e.g. mit-ik.ladok.se
   * @param certFile The path to the certificate to use for authentication.
   * @param key      The key to certificate.
   * @throws Exception on errors.
   */
  public UtbildningsinformationServiceImpl(final String host, final String certFile, final String key)
          throws Exception {
    super(host, certFile, key, SERVICE);
  }

  /**
   * Constructor Web Service client end representing the Ladok utbildningsinformation endpoint.
   *
   * @param host    The hostname of the targeted Ladok environment, e.g. mit-ik.ladok.se
   * @param context the SSLContext containing necessary information.
   * @throws Exception on errors.
   */
  public UtbildningsinformationServiceImpl(final String host, final SSLContext context) throws Exception {
    super(host, context, SERVICE);
  }
}
