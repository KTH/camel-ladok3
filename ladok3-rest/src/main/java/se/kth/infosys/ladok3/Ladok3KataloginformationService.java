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

import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import se.ladok.schemas.kataloginformation.Anvandare;
import se.ladok.schemas.kataloginformation.AnvandareLista;
import se.ladok.schemas.kataloginformation.Anvandarinformation;

/**
 * Real implementation of the Ladok kataloginformation service. It is using JAX RS 
 * which means that errors will be thrown as unchecked runtime exceptions. See 
 * JAX RS client documentation.
 */
public class Ladok3KataloginformationService extends Ladok3Service implements KataloginformationService {
    private static final MediaType SERVICE_TYPE = new MediaType("application", "vnd.ladok-kataloginformation+xml");
    private static final String SERVICE = "kataloginformation";

    /**
     * Constructor Web Service client end representing the Ladok kataloginformation endpoint.
     * 
     * @param host The hostname of the targeted Ladok environment, e.g. mit-ik.ladok.se
     * @param certFile The path to the certificate to use for authentication.
     * @param key The key to certificate.
     * @throws Exception on errors.
     */
    public Ladok3KataloginformationService(final String host, final String certFile, final String key) throws Exception {
        super(host, certFile, key, SERVICE);
    }

    /**
     * Constructor Web Service client end representing the Ladok studentinformation endpoint.
     * 
     * @param host The hostname of the targeted Ladok environment, e.g. mit-ik.ladok.se
     * @param context the SSLContext containing necessary information. 
     * @throws Exception on errors.
     */
    public Ladok3KataloginformationService(final String host, final SSLContext context) throws Exception {
        super(host, context, SERVICE);
    }

    /**
     * {@inheritDoc}
     */
    public Anvandare anvandare(final String uid) {
        return target.path("/anvandare/{uid}")
                .resolveTemplate("uid", uid)
                .request()
                .accept(SERVICE_TYPE)
                .get(Anvandare.class);
    }

    /**
     * {@inheritDoc}
     */
    public Anvandare createAnvandare(final Anvandare anvandare) {
        return target.path("/anvandare")
                .request()
                .accept(SERVICE_TYPE)
                .post(Entity.entity(anvandare, SERVICE_TYPE), Anvandare.class);
    }

    /**
     * {@inheritDoc}
     */
    public Anvandare updateAnvandare(final Anvandare anvandare) {
        return target.path("/anvandare/{uid}")
                .resolveTemplate("uid", anvandare.getUid())
                .request()
                .accept(SERVICE_TYPE)
                .put(Entity.entity(anvandare, SERVICE_TYPE), Anvandare.class);
    }

    /**
     * {@inheritDoc}
     */
    public Anvandarinformation anvandarinformation(final String uid) {
        return target.path("/anvandare/{uid}/anvandarinformation")
                .resolveTemplate("uid", uid)
                .request()
                .accept(SERVICE_TYPE)
                .get(Anvandarinformation.class);
    }

    /**
     * {@inheritDoc}
     */
    public Anvandarinformation createAnvandarinformation(final Anvandarinformation anvandarinformation) {
        return target.path("/anvandare/{uid}/anvandarinformation")
                .resolveTemplate("uid", anvandarinformation.getAnvandareUID())
                .request()
                .accept(SERVICE_TYPE)
                .post(Entity.entity(anvandarinformation, SERVICE_TYPE), Anvandarinformation.class);
    }

    /**
     * {@inheritDoc}
     */
    public Anvandarinformation updateAnvandarinformation(final Anvandarinformation anvandarinformation) {
        return target.path("/anvandare/{uid}/anvandarinformation")
                .resolveTemplate("uid", anvandarinformation.getAnvandareUID())
                .request()
                .accept(SERVICE_TYPE)
                .put(Entity.entity(anvandarinformation, SERVICE_TYPE), Anvandarinformation.class);
    }

    /**
     * {@inheritDoc}
     */
    public AnvandareLista anvandare(final Map<String, Object> params) {
        WebTarget request = target.path("/anvandare");

        for (String param : params.keySet()) {
            request = request.queryParam(param, params.get(param));
        }

        return request
                .request()
                .accept(SERVICE_TYPE)
                .get(AnvandareLista.class);
    }

    /**
     * {@inheritDoc}
     */
    public AnvandareLista anvandareFiltrerade(final Map<String, Object> params) {
        WebTarget request = target.path("/anvandare/filtrerade");

        for (String param : params.keySet()) {
            request = request.queryParam(param, params.get(param));
        }

        return request
                .request()
                .accept(SERVICE_TYPE)
                .get(AnvandareLista.class);
    }
}
