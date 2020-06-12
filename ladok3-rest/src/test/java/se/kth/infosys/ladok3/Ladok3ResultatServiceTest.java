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
import java.util.Map;
import java.util.Properties;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import se.ladok.schemas.resultat.StudieresultatForRapporteringPaAktivitetstillfalleSokVarden;
import se.ladok.schemas.resultat.StudieresultatOrderByEnum;
import se.ladok.schemas.resultat.StudieresultatTillstandVidRapporteringEnum;

@Ignore("Does not work without connection to Ladok")
public class Ladok3ResultatServiceTest {
  private ResultatServiceImpl resultatService;
  private Properties properties = new Properties();

  @Before
  public void setup() throws Exception {
    properties.load(ClassLoader.getSystemResourceAsStream("test.properties"));

    String host = properties.getProperty("ladok3.host");
    String certFile = properties.getProperty("ladok3.cert.file");
    String key = properties.getProperty("ladok3.cert.key");

    resultatService = new ResultatServiceImpl(host, certFile, key);
    }

  @Test
  public void testCreateSokresultatStudieResultatSokVarden() {
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("page", 1);
    params.put("limit", 400);
    params.put("filtrering", "utkast,obehandlade");
    params.put("orderby", "fornamn_asc,efternamn_asc");
    params.put("kurstillfallenuid", "86d2fa6a-893f-11ea-a682-14c6d22c41e7,86d2fa6a-893f-11ea-a682-14c6d22c34e7");
    
    StudieresultatForRapporteringPaAktivitetstillfalleSokVarden sokVarden = resultatService.createSokresultatStudieResultatSokVarden(params);

    assertNotNull(sokVarden);
    assertEquals(1, (int)sokVarden.getPage());
    assertEquals(2, sokVarden.getKurstillfallenUID().size());
    assertEquals(true, sokVarden.getFiltrering().contains(StudieresultatTillstandVidRapporteringEnum.UTKAST));
    assertEquals(2, sokVarden.getFiltrering().size());
    assertEquals(true, sokVarden.getOrderBy().contains(StudieresultatOrderByEnum.FORNAMN_ASC));

    }

}
