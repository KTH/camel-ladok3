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
import javax.ws.rs.client.WebTarget;

import se.ladok.schemas.dap.ServiceIndex;
import se.ladok.schemas.kataloginformation.Anvandare;
import se.ladok.schemas.kataloginformation.AnvandareLista;

/**
 * A class representing the Ladok kataloginformation service. It is using JAX RS 
 * which means that errors will be thrown as unchecked runtime exceptions. See 
 * JAX RS client documentation.
 */
public class Ladok3KatalogInformationService extends LadokService {
    private static final String SERVICE_XML = "application/vnd.ladok-kataloginformation+xml";
    private static final String SERVICE = "kataloginformation";
    private final WebTarget kataloginformation;

    /**
     * Constructor Web Service client end representing the Ladok kataloginformation endpoint.
     * 
     * @param host The hostname of the targeted Ladok environment, e.g. mit-ik.ladok.se
     * @param certFile The path to the certificate to use for authentication.
     * @param key The key to certificate.
     * @throws Exception on errors.
     */
    public Ladok3KatalogInformationService(String host, String certFile, String key) throws Exception {
        super(host, certFile, key);
        this.kataloginformation = client.target(String.format("https://%s/%s", host, SERVICE));
    }

    /**
     * Constructor Web Service client end representing the Ladok studentinformation endpoint.
     * 
     * @param host The hostname of the targeted Ladok environment, e.g. mit-ik.ladok.se
     * @param context the SSLContext containing necessary information. 
     * @throws Exception on errors.
     */
    public Ladok3KatalogInformationService(String host, SSLContext context) throws Exception {
        super(context);
        this.kataloginformation = client.target(String.format("https://%s/%s", host, SERVICE));
    }

    /**
     * {@inheritDoc}
     */
    public ServiceIndex serviceIndex() {
        return kataloginformation.path("/service/index")
                .request()
                .accept(SERVICE_XML)
                .get(ServiceIndex.class);
    }

    /**
     * Retrieve a user (anvandare) given its UID.
     * @param uid The unique identifier for the user.
     * @return The user matching the UID
     */
    public Anvandare anvandareUID(String uid) {
        return kataloginformation.path("/anvandare/{uid}")
                .resolveTemplate("uid", uid)
                .request()
                .accept(SERVICE_XML)
                .get(Anvandare.class);
    }

    /**
     * Calls /anvandare with query parameters as specified in the params Map. 
     * See Ladok REST documentation for more information about parameters.
     *
     * {@code
     * Map<String, Object> params = new HashMap<String, Object>();
     * params.put("anvandarnamn", "fjo@kth.se");
     * AnvadarLista res =
     *     katatlogInformationService.anvandareFiltrerade(params);
     * 
     * Objects passed as values will be rendered into parameters using their 
     * toString() method.
     * }
     *
     * @param params A map between parameter strings and their object values.
     * @return The search result.
     */
    public AnvandareLista getAnvandare(Map<String, Object> params) {
        WebTarget request = kataloginformation.path("/anvandare");

        for (String param : params.keySet()) {
            request = request.queryParam(param, params.get(param));
        }

        return request
                .request()
                .accept(SERVICE_XML)
                .get(AnvandareLista.class);
    }

    /**
     * Calls /anvandare/filtrerade with query parameters as specified in the params Map. 
     * See Ladok REST documentation for more information about parameters.
     *
     * {@code
     * Map<String, Object> params = new HashMap<String, Object>();
     * params.put("anvandarnamn", "fjo@kth.se");
     * AnvadarLista res =
     *     katatlogInformationService.anvandareFiltrerade(params);
     * 
     * Objects passed as values will be rendered into parameters using their 
     * toString() method.
     * }
     *
     * @param params A map between parameter strings and their object values.
     * @return The search result.
     */
    public AnvandareLista anvandareFiltrerade(Map<String, Object> params) {
        WebTarget request = kataloginformation.path("/anvandare/filtrerade");

        for (String param : params.keySet()) {
            request = request.queryParam(param, params.get(param));
        }

        return request
                .request()
                .accept(SERVICE_XML)
                .get(AnvandareLista.class);
    }
}
