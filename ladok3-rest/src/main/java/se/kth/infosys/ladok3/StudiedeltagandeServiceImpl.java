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

import se.ladok.schemas.studiedeltagande.IngaendeKurspaketeringstillfalleLista;
import se.ladok.schemas.studiedeltagande.TillfallesdeltagandeLista;
import se.ladok.schemas.studiedeltagande.UtdataResultat;
import se.ladok.schemas.studiedeltagande.UtdataResultatrad;
import se.ladok.schemas.studiedeltagande.Utdatafraga;
import se.ladok.schemas.studiedeltagande.Utdatatyp;

/**
 * Real implementation of the Ladok studiedeltagande  service. It is using JAX RS
 * which means that errors will be thrown as unchecked runtime exceptions. See
 * JAX RS client documentation.
 */
public class StudiedeltagandeServiceImpl extends AbstractLadok3Service implements StudiedeltagandeService {
    private static final MediaType SERVICE_TYPE = new MediaType("application", "vnd.ladok-studiedeltagande+xml");
    private static final String SERVICE = "studiedeltagande";

    /**
     * Constructor Web Service client end representing the Ladok studiedeltagande  endpoint.
     *
     * @param host The hostname of the targeted Ladok environment, e.g. mit-ik.ladok.se
     * @param certFile The path to the certificate to use for authentication.
     * @param key The key to certificate.
     * @throws Exception on errors.
     */
    public StudiedeltagandeServiceImpl(final String host, final String certFile, final String key) throws Exception {
        super(host, certFile, key, SERVICE);
    }

    /**
     * Constructor Web Service client end representing the Ladok studiedeltagande  endpoint.
     *
     * @param host The hostname of the targeted Ladok environment, e.g. mit-ik.ladok.se
     * @param context the SSLContext containing necessary information.
     * @throws Exception on errors.
     */
    public StudiedeltagandeServiceImpl(final String host, final SSLContext context) throws Exception {
        super(host, context, SERVICE);
    }

    /**
     * {@inheritDoc}
     */
    public TillfallesdeltagandeLista pabarjadutbildningKurspaketeringStudent(String uid) {
        return target.path("/paborjadutbilding/kurspaketering/student/{studentuid}")
                .resolveTemplate("studentuid", uid)
                .request()
                .accept(SERVICE_TYPE)
                .get(TillfallesdeltagandeLista.class);
    }

    /**
     * {@inheritDoc}
     */
    public IngaendeKurspaketeringstillfalleLista studiestrukturStudent(String uid) {
        return target.path("/studiestruktur/student/{studentuid}")
                .resolveTemplate("studentuid", uid)
                .request()
                .accept(SERVICE_TYPE)
                .get(IngaendeKurspaketeringstillfalleLista.class);
    }

    /**
     * {@inheritDoc}
     */
    public UtdataResultat utdataStudieaktivitetOchFinansiering(final Utdatafraga utdatafraga) {
        WebTarget request = target.path("/utdata/" + Utdatatyp.STUDIEDELTAGANDE_UTDATA_STUDIEAKTIVITET_OCH_FINANSIERING.value());

        Utdatafraga fraga = utdatafraga;
        if(fraga == null) {
          fraga = new Utdatafraga();
          fraga.setSida(1);
          fraga.setSidstorlek(400);
        }

        return request
                .request()
                .put(Entity.entity(fraga, SERVICE_TYPE), UtdataResultat.class);

    }

    /**
     * {@inheritDoc}
     */
    public Iterable<UtdataResultatrad> utdataStudieaktivitetOchFinansieringIteraterable(Map<String, Object> params) {
        return new StudieaktivitetUtdataResultat(this, params);
    }

    /**
     * {@inheritDoc}
     */
    public TillfallesdeltagandeLista kurstillfallesdeltagandeStudent(String uid) {
        return target.path("/tillfallesdeltagande/kurstillfallesdeltagande/student/{studentuid}")
                .resolveTemplate("studentuid", uid)
                .request()
                .accept(SERVICE_TYPE)
                .get(TillfallesdeltagandeLista.class);
    }
}
