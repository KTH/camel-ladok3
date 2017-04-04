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

import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import se.kth.infosys.ladok3.internal.Ladok3StudentFiltreraIterator;
import se.kth.infosys.ladok3.internal.Ladok3StudentFiltreraStudentIterator;
import se.kth.infosys.ladok3.internal.Ladok3Service;
import se.ladok.schemas.studentinformation.Kontaktuppgifter;
import se.ladok.schemas.studentinformation.SokresultatStudentinformationRepresentation;
import se.ladok.schemas.studentinformation.Student;
import se.ladok.schemas.studentinformation.StudentISokresultat;

/**
 * Real implementation of the Ladok studentinformation service. It is using JAX RS 
 * which means that errors will be thrown as unchecked runtime exceptions. See 
 * JAX RS client documentation.
 */
public class Ladok3StudentinformationService extends Ladok3Service implements StudentinformationService {
    private static final MediaType SERVICE_TYPE = new MediaType("application", "vnd.ladok-studentinformation+xml");
    private static final String SERVICE = "studentinformation";

    /**
     * Constructor Web Service client end representing the Ladok studentinformation endpoint.
     * 
     * @param host The hostname of the targeted Ladok environment, e.g. mit-ik.ladok.se
     * @param certFile The path to the certificate to use for authentication.
     * @param key The key to certificate.
     * @throws Exception on errors.
     */
    public Ladok3StudentinformationService(String host, String certFile, String key) throws Exception {
        super(host, certFile, key, SERVICE);
    }

    /**
     * Constructor Web Service client end representing the Ladok studentinformation endpoint.
     * 
     * @param host The hostname of the targeted Ladok environment, e.g. mit-ik.ladok.se
     * @param context the SSLContext containing necessary information. 
     * @throws Exception on errors.
     */
    public Ladok3StudentinformationService(String host, SSLContext context) throws Exception {
        super(host, context, SERVICE);
    }

    /**
     * {@inheritDoc}
     */
    public Student studentPersonnummer(String personnummer) {
        return target.path("/student/personnummer/{personnummer}")
                .resolveTemplate("personnummer", personnummer)
                .request()
                .accept(SERVICE_TYPE)
                .get(Student.class);
    }

    /**
     * {@inheritDoc}
     */
    public Student student(String uid) {
        return target.path("/student/{uid}")
                .resolveTemplate("uid", uid)
                .request()
                .accept(SERVICE_TYPE)
                .get(Student.class);
    }

    /**
     * {@inheritDoc}
     */
    public Kontaktuppgifter studentKontaktuppgifter(String uid) {
        return target.path("/student/{uid}/kontaktuppgifter")
                .resolveTemplate("uid", uid)
                .request()
                .accept(SERVICE_TYPE)
                .get(Kontaktuppgifter.class);
    }

    /**
     * {@inheritDoc}
     */
    public SokresultatStudentinformationRepresentation studentFiltrera(Map<String, Object> params) {
        WebTarget request = target.path("/student/filtrera");

        params.putIfAbsent("limit", 400);
        params.putIfAbsent("page", 1);

        for (String param : params.keySet()) {
            request = request.queryParam(param, params.get(param));
        }

        return request
                .request()
                .accept(SERVICE_TYPE)
                .get(SokresultatStudentinformationRepresentation.class);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<StudentISokresultat> studentFiltreraIterator(Map<String, Object> params) {
        return new Ladok3StudentFiltreraIterator(this, params);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Student> studentFiltreraStudentIterator(Map<String, Object> params) {
        return new Ladok3StudentFiltreraStudentIterator(this, params);
    }
}
