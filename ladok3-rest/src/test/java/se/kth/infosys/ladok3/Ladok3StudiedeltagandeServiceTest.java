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

import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import se.ladok.schemas.dap.RelationLink;
import se.ladok.schemas.dap.ServiceIndex;
import se.ladok.schemas.studiedeltagande.IngaendeKurspaketeringstillfalleLista;
import se.ladok.schemas.studiedeltagande.TillfallesdeltagandeLista;

public class Ladok3StudiedeltagandeServiceTest {
    private StudiedeltagandeServiceImpl studiedeltagandeService;
    private Properties properties = new Properties();

    @Before
    public void setup() throws Exception {
        properties.load(ClassLoader.getSystemResourceAsStream("test.properties"));

        String host = properties.getProperty("ladok3.host");
        String certFile = properties.getProperty("ladok3.cert.file");
        String key = properties.getProperty("ladok3.cert.key");

        studiedeltagandeService = new StudiedeltagandeServiceImpl(host, certFile, key);
    }

    @Test
    public void testServiceIndex() {
        ServiceIndex index = studiedeltagandeService.serviceIndex();
        List<RelationLink> links = index.getLink();

        assert(links.size() > 0);
    }

    @Test
    @Ignore
    public void testPabarjadutbildningKurspaketeringStudent() {
        String uid = properties.getProperty("ladok3.test.Ladok3StudiedeltagandeServiceTest.pabarjadutbildningKurspaketeringStudent.uid");

        TillfallesdeltagandeLista tillfallen = studiedeltagandeService.pabarjadutbildningKurspaketeringStudent(uid);
        assert(tillfallen.getTillfallesdeltaganden().getTillfallesdeltagande().size() > 0);
    }

    @Test
    @Ignore
    public void testStudiestrukturStudent() {
        String uid = properties.getProperty("ladok3.test.Ladok3StudiedeltagandeServiceTest.pabarjadutbildningKurspaketeringStudent.uid");

        IngaendeKurspaketeringstillfalleLista tillfallen = studiedeltagandeService.studiestrukturStudent(uid);
        assert(tillfallen.getStudiestrukturer().getStudiestruktur().size() > 0);
    }
}