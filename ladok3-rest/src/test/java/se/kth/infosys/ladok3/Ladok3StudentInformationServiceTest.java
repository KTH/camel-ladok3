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

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import se.ladok.schemas.dap.RelationLink;
import se.ladok.schemas.dap.ServiceIndex;
import se.ladok.schemas.studentinformation.SokresultatStudentinformationRepresentation;
import se.ladok.schemas.studentinformation.Student;
import se.ladok.schemas.studentinformation.StudentISokresultat;

public class Ladok3StudentInformationServiceTest {
    private Ladok3StudentInformationService studentInformationService;
    private Properties properties = new Properties();

    @Before
    public void setup() throws Exception {
        properties.load(ClassLoader.getSystemResourceAsStream("test.properties"));

        String host = properties.getProperty("ladok3.host");
        String certFile = properties.getProperty("ladok3.cert.file");
        String key = properties.getProperty("ladok3.cert.key");

        studentInformationService = new Ladok3StudentInformationService(host, certFile, key);
    }

    @Test
    public void testServiceIndex() {
        ServiceIndex index = studentInformationService.serviceIndex();
        List<RelationLink> links = index.getLink();

        for (RelationLink link : links) {
            System.out.println(link.getRel());
            System.out.println(link.getUri());
        }
        assert(links.size() > 0);
    }

    @Test
    public void searchStudents() {
        String personnummer = properties.getProperty("ladok3.test.Ladok3StudentInformationServiceTest.getStudentByPersonnummer.personnummer");
        String fornamn = properties.getProperty("ladok3.test.Ladok3StudentInformationServiceTest.getStudentByPersonnummer.fornamn");
        String efternamn = properties.getProperty("ladok3.test.Ladok3StudentInformationServiceTest.getStudentByPersonnummer.efternamn");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("personnummer", personnummer);

        SokresultatStudentinformationRepresentation res = studentInformationService.studentFiltrera(params);
        assertEquals(1, res.getTotaltAntalPoster());

        StudentISokresultat student = res.getResultat().get(0);
        assertEquals(fornamn, student.getFornamn());
        assertEquals(efternamn, student.getEfternamn());
        assertEquals(personnummer, student.getPersonnummer());
    }

    @Test
    public void getStudentByPersonnummer() {
        String personnummer = properties.getProperty("ladok3.test.Ladok3StudentInformationServiceTest.getStudentByPersonnummer.personnummer");
        String fornamn = properties.getProperty("ladok3.test.Ladok3StudentInformationServiceTest.getStudentByPersonnummer.fornamn");
        String efternamn = properties.getProperty("ladok3.test.Ladok3StudentInformationServiceTest.getStudentByPersonnummer.efternamn");

        Student student = studentInformationService.studentPersonnummer(personnummer);

        assertEquals(personnummer, student.getPersonnummer());
        assertEquals(fornamn, student.getFornamn());
        assertEquals(efternamn, student.getEfternamn());
    }
}