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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import javax.ws.rs.ClientErrorException;

import org.junit.Before;
import org.junit.Test;

import se.ladok.schemas.Identiteter;
import se.ladok.schemas.dap.RelationLink;
import se.ladok.schemas.dap.ServiceIndex;
import se.ladok.schemas.kataloginformation.Anvandarbehorighetsstatus;
import se.ladok.schemas.kataloginformation.Anvandare;
import se.ladok.schemas.kataloginformation.AnvandareLista;
import se.ladok.schemas.kataloginformation.Anvandarinformation;
import se.ladok.schemas.kataloginformation.ObjectFactory;

public class Ladok3KataloginformationServiceTest {
    private static final String TEST_ANVANDARE_KTH_SE = "ladok3-rest-test@kth.se";
    private KataloginformationService katalogInformationService;
    private Properties properties = new Properties();
    private static final ObjectFactory objectFactory = new ObjectFactory();

    @Before
    public void setup() throws Exception {
        properties.load(ClassLoader.getSystemResourceAsStream("test.properties"));

        String host = properties.getProperty("ladok3.host");
        String certFile = properties.getProperty("ladok3.cert.file");
        String key = properties.getProperty("ladok3.cert.key");

        katalogInformationService = new KataloginformationServiceImpl(host, certFile, key);
    }

    @Test
    public void testServiceIndex() {
        ServiceIndex index = katalogInformationService.serviceIndex();
        List<RelationLink> links = index.getLink();

        assert(links.size() > 0);
    }

    @Test
    public void searchAnvandare() {
        String username = properties.getProperty("ladok3.test.Ladok3KataglogInformationServiceTest.searchAnvandare.username");
        String fornamn = properties.getProperty("ladok3.test.Ladok3KataglogInformationServiceTest.searchAnvandare.fornamn");
        String efternamn = properties.getProperty("ladok3.test.Ladok3KataglogInformationServiceTest.searchAnvandare.efternamn");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("anvandarnamn", username);

        AnvandareLista anvandare = katalogInformationService.anvandare(params);
        assertEquals(1, anvandare.getAnvandare().size());
        Anvandare user = anvandare.getAnvandare().get(0);

        assertEquals(fornamn, user.getFornamn().toUpperCase());
        assertEquals(efternamn, user.getEfternamn().toUpperCase());
        assertEquals(username, user.getAnvandarnamn());

        Anvandare res = katalogInformationService.anvandare(user.getUid());
        assertEquals(fornamn, res.getFornamn().toUpperCase());
        assertEquals(efternamn, res.getEfternamn().toUpperCase());
        assertEquals(username, user.getAnvandarnamn());
    }

    @Test
    public void anvandarTest() {
        try {
            Anvandare anvandare = objectFactory.createAnvandare();
            anvandare.setAnvandarnamn(TEST_ANVANDARE_KTH_SE);
            anvandare.setFornamn("Fornamn");
            anvandare.setEfternamn("Efternamn");
            Anvandare created = katalogInformationService.createAnvandare(anvandare);
            assertNotNull(created);
            assertFalse(created.getUid().isEmpty());
        } catch (ClientErrorException e) {
            assertEquals(409, e.getResponse().getStatus());
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("anvandarnamn", TEST_ANVANDARE_KTH_SE);
        AnvandareLista search = katalogInformationService.anvandare(params);
        assertEquals(1, search.getAnvandare().size());

        Anvandare update = katalogInformationService.anvandare(search.getAnvandare().get(0).getUid());
        update.setFornamn("NyttFornamn");
        Anvandare updated = katalogInformationService.updateAnvandare(update);
        assertEquals("NyttFornamn", updated.getFornamn());
        assertEquals(update.getUid(), updated.getUid());
        assertEquals(update.getAnvandarnamn(), updated.getAnvandarnamn());

        update.setFornamn("Fornamn");
        updated = katalogInformationService.updateAnvandare(update);

        try {
            Anvandarinformation information = objectFactory.createAnvandarinformation();
            information.setAnvandareUID(updated.getUid());
            information.setEpost(TEST_ANVANDARE_KTH_SE);
            information.setSms("123 123 123");
            Anvandarinformation createdInformation = katalogInformationService.createAnvandarinformation(information);
            assertNotNull(createdInformation);
            assertFalse(createdInformation.getUid().isEmpty());
        } catch (ClientErrorException e) {
            assertEquals(409, e.getResponse().getStatus());
        }

        String sms = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 10000));
        Anvandarinformation information = katalogInformationService.anvandarinformation(updated.getUid());
        information.setSms(sms);
        Anvandarinformation updatedInformation = katalogInformationService.updateAnvandarinformation(information);
        assertNotNull(updatedInformation);
        assertEquals(information.getUid(), updatedInformation.getUid());
        assertEquals(sms, updatedInformation.getSms());
    }

    @Test
    public void behorigheterTest() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("anvandarnamn", TEST_ANVANDARE_KTH_SE);
        AnvandareLista search = katalogInformationService.anvandare(params);
        assertEquals(1, search.getAnvandare().size());

        Anvandare anvandare = katalogInformationService.anvandare(search.getAnvandare().get(0).getUid());
        Identiteter identititer = katalogInformationService.anvandarbehorigheterBytstatus(
                anvandare, Anvandarbehorighetsstatus.INAKTIV);
        assertNotNull(identititer);
        assertFalse(identititer.getIdentitet().isEmpty());
        identititer = katalogInformationService.anvandarbehorigheterBytstatus(
                anvandare, Anvandarbehorighetsstatus.AKTIV);
        assertNotNull(identititer);
        assertFalse(identititer.getIdentitet().isEmpty());
    }
}
