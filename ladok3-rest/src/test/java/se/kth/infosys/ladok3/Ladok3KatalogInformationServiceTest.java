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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.ClientErrorException;

import org.junit.Before;
import org.junit.Test;

import se.ladok.schemas.dap.RelationLink;
import se.ladok.schemas.dap.ServiceIndex;
import se.ladok.schemas.kataloginformation.Anvandare;
import se.ladok.schemas.kataloginformation.AnvandareLista;
import se.ladok.schemas.kataloginformation.Anvandarinformation;
import se.ladok.schemas.kataloginformation.ObjectFactory;

public class Ladok3KatalogInformationServiceTest {
    private Ladok3KatalogInformationService katalogInformationService;
    private Properties properties = new Properties();
    private static final ObjectFactory objectFactory = new ObjectFactory();

    @Before
    public void setup() throws Exception {
        properties.load(ClassLoader.getSystemResourceAsStream("test.properties"));

        String host = properties.getProperty("ladok3.host");
        String certFile = properties.getProperty("ladok3.cert.file");
        String key = properties.getProperty("ladok3.cert.key");

        katalogInformationService = new Ladok3KatalogInformationService(host, certFile, key);
    }

    @Test
    public void testServiceIndex() {
        ServiceIndex index = katalogInformationService.serviceIndex();
        List<RelationLink> links = index.getLink();

        for (RelationLink link : links) {
            System.out.println(link.getRel());
            System.out.println(link.getUri());
        }
        assert(links.size() > 0);
    }

    @Test
    public void searchAnvandare() {
        String username = properties.getProperty("ladok3.test.Ladok3KataglogInformationServiceTest.searchAnvandare.username");
        String fornamn = properties.getProperty("ladok3.test.Ladok3KataglogInformationServiceTest.searchAnvandare.fornamn");
        String efternamn = properties.getProperty("ladok3.test.Ladok3KataglogInformationServiceTest.searchAnvandare.efternamn");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("anvandarnamn", username);

        AnvandareLista anvandare = katalogInformationService.getAnvandare(params);
        assertEquals(1, anvandare.getAnvandare().size());
        Anvandare user = anvandare.getAnvandare().get(0);

        assertEquals(fornamn, user.getFornamn().toUpperCase());
        assertEquals(efternamn, user.getEfternamn().toUpperCase());
        assertEquals(username, user.getAnvandarnamn());

        Anvandare res = katalogInformationService.anvandareUID(user.getUid());
        assertEquals(fornamn, res.getFornamn().toUpperCase());
        assertEquals(efternamn, res.getEfternamn().toUpperCase());
        assertEquals(username, user.getAnvandarnamn());
    }

    @Test
    public void anvandarInformationTest() {
        String username = properties.getProperty("ladok3.test.Ladok3KataglogInformationServiceTest.searchAnvandare.username");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("anvandarnamn", username);

        AnvandareLista anvandare = katalogInformationService.getAnvandare(params);
        assertEquals(1, anvandare.getAnvandare().size());
        Anvandare user = anvandare.getAnvandare().get(0);

        Anvandarinformation information = katalogInformationService.anvandarInformation(user.getUid());
        assertNotNull(information);

        try {
            information.setSms(objectFactory.createAnvandarinformationSms("123 123 123"));
            information.setUid(user.getUid());
            information = katalogInformationService.createAnvandarInformation(user.getUid(), information);
            assertNotNull(information);
            assertEquals("123 123 123", information.getSms().getValue());
        } catch (ClientErrorException e) {
            assertEquals(409, e.getResponse().getStatus());
        }

        information.setSms(objectFactory.createAnvandarinformationSms("321 321 321"));
        information = katalogInformationService.updateAnvandarInformation(user.getUid(), information);
        assertNotNull(information);
        assertEquals("321 321 321", information.getSms().getValue());
    }
}
