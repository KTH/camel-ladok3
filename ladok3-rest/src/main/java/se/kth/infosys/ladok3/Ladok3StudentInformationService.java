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
package se.kth.infosys.ladok3;

import java.util.Map;

import javax.ws.rs.client.WebTarget;

import se.ladok.schemas.dap.ServiceIndex;
import se.ladok.schemas.studentinformation.SokresultatStudentinformationRepresentation;
import se.ladok.schemas.studentinformation.Student;

/**
 * A class representing the Ladok studentinformation service.
 */
public class Ladok3StudentInformationService extends LadokService {
    private static final String STUDENTINFORMATION_XML = "application/vnd.ladok-studentinformation+xml";
    private final WebTarget studentinformation;

    /**
     * Constructor Web Service client end representing the Ladok studentinformation endpoint.
     * 
     * @param host The hostname of the targeted Ladok environment, i.e. mit-ik.ladok.se
     * @param certFile The path to the certificate to use for authentication.
     * @param key The key to certificate.
     * @throws Exception on errors.
     */
    public Ladok3StudentInformationService(String host, String certFile, String key) throws Exception {
        super(host, certFile, key);
        this.studentinformation = client.target(String.format("https://%s/studentinformation", host));
    }

    /**
     * Retrieve the service index for the studentinformation service.
     * @return the service index.
     */
    public ServiceIndex serviceIndex() {
        return studentinformation.path("/service/index")
                .request()
                .accept(STUDENTINFORMATION_XML)
                .get(ServiceIndex.class);
    }

    /**
     * Retrieve a student given a personnummer.
     * @param personnummer
     * @return The student matching the personnummer
     */
    public Student studentPersonnummer(String personnummer) {
        return studentinformation.path("/student/personnummer/{personnummer}")
                .resolveTemplate("personnummer", personnummer)
                .request()
                .accept(STUDENTINFORMATION_XML)
                .get(Student.class);
    }

    /**
     * Calls /student/filtrera with query parameters as specified in the params Map. 
     * See Ladok REST documentation for more information about parameters. Only 
     * difference is that this method will default to "limit=400" and "page=1"
     * unless something else is specified. E.g:
     *
     * <code>
     * Map<String, Object> params = new HashMap<String, Object>();
     * params.put("personnummer", "19870412031234");
     * SokresultatStudentinformationRepresentation res =
     *     studentInformationService.studentFiltrera(params);
     * </code>
     *
     * @param params
     * @return the search result.
     */
    public SokresultatStudentinformationRepresentation studentFiltrera(Map<String, Object> params) {
        WebTarget request = studentinformation.path("/student/filtrera");

        params.putIfAbsent("limit", 400);
        params.putIfAbsent("page", 1);

        for (String param : params.keySet()) {
            request = request.queryParam(param, params.get(param));
        }

        return request
                .request()
                .accept(STUDENTINFORMATION_XML)
                .get(SokresultatStudentinformationRepresentation.class);
    }
}
