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
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import se.ladok.schemas.resultat.Aktivitetstillfalle;
import se.ladok.schemas.resultat.AktivitetstillfalleForStudentLista;
import se.ladok.schemas.resultat.SokresultatStudieresultatResultat;
import se.ladok.schemas.resultat.StudieresultatAnmaldaEnum;
import se.ladok.schemas.resultat.StudieresultatForRapporteringPaAktivitetstillfalleSokVarden;
import se.ladok.schemas.resultat.StudieresultatOrderByEnum;
import se.ladok.schemas.resultat.StudieresultatTillstandVidRapporteringEnum;

public class ResultatServiceImpl extends AbstractLadok3Service implements ResultatService {
  private static final MediaType SERVICE_TYPE = new MediaType("application", "vnd.ladok-resultat+xml");
  private static final String SERVICE = "resultat";

  /**
   * Constructor Web Service client end representing the Ladok resultat endpoint.
   *
   * @param host     The hostname of the targeted Ladok environment, e.g.
   *                 api.integrationstest.ladok.se
   * @param certFile The path to the certificate to use for authentication.
   * @param key      The key to certificate.
   * @throws Exception on errors.
   */
  public ResultatServiceImpl(final String host, final String certFile, final String key) throws Exception {
    super(host, certFile, key, SERVICE);
  }

  /**
   * Constructor Web Service client end representing the Ladok resultat endpoint.
   *
   * @param host    The hostname of the targeted Ladok environment, e.g.
   *                api.integrationstest.ladok.se
   * @param context the SSLContext containing necessary information.
   * @throws Exception on errors.
   */
  public ResultatServiceImpl(final String host, final SSLContext context) throws Exception {
    super(host, context, SERVICE);
  }

  /**
   * {@inheritDoc}
   */
  public Aktivitetstillfalle aktivitetstillfalle(String aktivitetstillfalleUID) {
    return target.path("/aktivitetstillfalle/{aktivitetstillfalleUID}")
        .resolveTemplate("aktivitetstillfalleUID", aktivitetstillfalleUID).request().accept(SERVICE_TYPE)
        .get(Aktivitetstillfalle.class);
  }

  /**
   * {@inheritDoc}
   */
  public AktivitetstillfalleForStudentLista aktivitetstillfalleForStudentLista(String uid) {
    return target.path("/aktivitetstillfalle/student/{studentUID}").resolveTemplate("studentUID", uid).request()
        .accept(SERVICE_TYPE).get(AktivitetstillfalleForStudentLista.class);
  }

   /**
   * {@inheritDoc}
   */
  public SokresultatStudieresultatResultat sokresultatStudieresultatResultat(String aktivitetstillfalleUID,
      Map<String, Object> sokVarden) {

    WebTarget req = target.path("/studieresultat/rapportera/aktivitetstillfalle/{aktivitetstillfalleUID}/sok")
                    .resolveTemplate("aktivitetstillfalleUID", aktivitetstillfalleUID);

    StudieresultatForRapporteringPaAktivitetstillfalleSokVarden varden = createSokresultatStudieResultatSokVarden(sokVarden);

    return req.request()
            .put(Entity.entity(varden, SERVICE_TYPE), SokresultatStudieresultatResultat.class);

  }

  /**
   * Creates a SokresultatStudieResultatSokVarden object used to filter the query to Ladok
   * based on the params sent in the request.
   * @param params as Map<String, Object>. Will default to Limit 400, Page 1 and ALLA on "anmaldaFiltrering"
   * if nothing else is supplied.
   * @return the StudieresultatForRapporteringPaAktivitetstillfalleSokVarden object
   */
  public StudieresultatForRapporteringPaAktivitetstillfalleSokVarden createSokresultatStudieResultatSokVarden(
      Map<String, Object> params) {

        StudieresultatForRapporteringPaAktivitetstillfalleSokVarden sokVarden = new StudieresultatForRapporteringPaAktivitetstillfalleSokVarden();

        params.putIfAbsent("limit", 400);
        params.putIfAbsent("page", 1);
        params.putIfAbsent("anmaldafiltrering", StudieresultatAnmaldaEnum.ALLA.value());

    int page = 1;
    if(params.get("page") != null) {
      if(params.get("page") instanceof Integer) {
        page = (Integer) params.get("page");
      } else {
        page = Integer.parseInt((String) params.get("page"));
      }
      sokVarden.setPage(page);
    }

    if(params.get("limit") != null) {
      int limit = 400;
      if(params.get("limit") instanceof Integer) {
        limit = (Integer) params.get("limit");
      } else {
        limit = Integer.parseInt((String) params.get("limit"));
      }
      sokVarden.setLimit(limit);
    }

    if(params.get("anmaldafiltrering") != null) {
      sokVarden.setAnmaldafiltrering(StudieresultatAnmaldaEnum.fromValue(
        (String)params.get("anmaldafiltrering")));
    }

    if(params.get("filtrering") != null) {
      String[] filters = ((String) params.get("filtrering")).split(",");

      for(String filter : filters) {
        sokVarden.getFiltrering().add(
          StudieresultatTillstandVidRapporteringEnum.fromValue(filter.toUpperCase())
        );
      }

    }

    if(params.get("orderby") != null) {
      String[] orderBy = ((String) params.get("orderby")).split(",");

      for(String order : orderBy ) {
        sokVarden.getOrderBy().add(
          StudieresultatOrderByEnum.fromValue(order.toUpperCase())
        );
      }
    }

    if(params.get("gruppuid") != null) {
      sokVarden.setGruppUID((String)params.get("gruppuid"));
    }

    if(params.get("kurstillfallenuid") != null) {
      String[] uids = ((String) params.get("kurstillfallenuid")).split(",");

      for(String uid : uids) {
        sokVarden.getKurstillfallenUID().add(uid);
      }
    }

    return sokVarden;
  }


}
