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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import se.kth.infosys.ladok3.utdata.StudieaktivitetOchFinansiering;
import se.ladok.schemas.studiedeltagande.IngaendeKurspaketeringstillfalleLista;
import se.ladok.schemas.studiedeltagande.TillfallesdeltagandeLista;
import se.ladok.schemas.studiedeltagande.Utdatafraga;

@Ignore("Does not work without connection to Ladok")
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

    @Test
    public void testUtdataStudieaktivitetOchFinansieringIterator() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("datumperiod", "2018-01-16_2018-06-04");
        params.put("limit", 5);

        Iterator<StudieaktivitetOchFinansiering> iterator = studiedeltagandeService.utdataStudieaktivitetOchFinansieringIteraterable(params).iterator();

        assertNotNull(iterator);
        assertTrue(iterator.hasNext());

        for (int i = 0; i < 7; i++) {
            assertNotNull(iterator.next());
        }
    }
    @Test
    public void testCreateUtdatafraga() {
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("datumperiod", "2018-01-16_2018-06-04");
      params.put("limit", 5);
      Utdatafraga fraga = studiedeltagandeService.createUtdatafraga(params);

      assertNotNull(fraga);
      assertEquals(5, fraga.getSidstorlek());
    }

    @Test
     public void testKurstillfallesdeltagandeStudent() {
        String uid = properties.getProperty("ladok3.test.Ladok3StudiedeltagandeServiceTest.kurstillfallesdeltagandeStudent.uid");

        TillfallesdeltagandeLista pabarjadUtbildning = studiedeltagandeService.pabarjadutbildningKurspaketeringStudent(uid);
        assertNotNull(pabarjadUtbildning.getTillfallesdeltaganden());
        assertNotNull(pabarjadUtbildning.getTillfallesdeltaganden().getTillfallesdeltagande());
        assertNotEquals(0, pabarjadUtbildning.getTillfallesdeltaganden().getTillfallesdeltagande().size());
    }
}
