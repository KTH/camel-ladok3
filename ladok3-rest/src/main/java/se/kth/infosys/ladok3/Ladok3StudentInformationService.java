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

import se.ladok.schemas.studentinformation.SokresultatStudentinformationRepresentation;
import se.ladok.schemas.studentinformation.Student;

public class Ladok3StudentInformationService extends LadokService {
    private static final String STUDENTINFORMATION_XML = "application/vnd.ladok-studentinformation+xml";
    private final WebTarget studentinformation;

    public Ladok3StudentInformationService(String host, String certFile, String key) throws Exception {
        super(host, certFile, key);
        this.studentinformation = client.target(String.format("https://%s/studentinformation", host));
    }

    public Student getStudentByPersonnummer(String personnummer) {
        return studentinformation.path("/student/personnummer/{personnummer}")
                .resolveTemplate("personnummer", personnummer)
                .request()
                .accept(STUDENTINFORMATION_XML)
                .get(Student.class);
    }

    public SokresultatStudentinformationRepresentation searchStudents(Map<String, Object> params) {
        WebTarget request = studentinformation.path("/student/filtrera");

        for (String param : params.keySet()) {
            request = request.queryParam(param, params.get(param));
        }

        return request
                .queryParam("limit", 400)
                .queryParam("page", 1)
                .request()
                .accept(STUDENTINFORMATION_XML)
                .get(SokresultatStudentinformationRepresentation.class);
    }
}
