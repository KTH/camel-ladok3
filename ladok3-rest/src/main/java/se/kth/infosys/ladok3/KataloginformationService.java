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
import se.ladok.schemas.kataloginformation.Anvandare;
import se.ladok.schemas.kataloginformation.AnvandareLista;
import se.ladok.schemas.kataloginformation.Anvandarinformation;
import se.ladok.schemas.kataloginformation.OrganisationLista;


/**
 * Interface representing the Ladok kataloginformation service.
 */
public interface KataloginformationService extends Ladok3Service {
  /**
   * Retrieve a user (anvandare) given its UID.
   *
   * @param uid The unique identifier for the user.
   * @return The user matching the UID
   */
  Anvandare anvandare(final String uid);

  /**
   * Calls /anvandare with query parameters as specified in the params Map.
   * See Ladok REST documentation for more information about parameters.
   *
   * <p>NOTE: the list of Anvandare does not contain all information, to retrieve
   * all information about Anvandare, use anvandare(final String uid)
   *
   * <pre>
   * {@code
   * Map<String, Object> params = new HashMap<String, Object>();
   * params.put("anvandarnamn", "fjo@kth.se");
   * AnvadarLista res =
   *     katatlogInformationService.anvandareFiltrerade(params);
   * }
   * </pre>
   *
   * <p>Objects passed as values will be rendered into parameters using their
   * toString() method.
   *
   * @param params A map between parameter strings and their object values.
   * @return The search result.
   */
  AnvandareLista anvandare(final Map<String, Object> params);

  /**
   * Create a user.
   *
   * @param anvandare The user object.
   * @return The created user.
   */
  Anvandare createAnvandare(final Anvandare anvandare);

  /**
   * Update a user.
   *
   * @param anvandare The user object.
   * @return The updated user.
   */
  Anvandare updateAnvandare(final Anvandare anvandare);

  /**
   * Retrieve Anvandarinformation for a user (anvandare) given its UID.
   *
   * @param uid The unique identifier for the user.
   * @return The user information matching the UID
   */
  Anvandarinformation anvandarinformation(final String uid);

  /**
   * Create Anvandarinformation for a user (anvandare).
   *
   * @param anvandarinformation the user information object.
   * @return The resulting user information.
   */
  Anvandarinformation createAnvandarinformation(final Anvandarinformation anvandarinformation);

  /**
   * Update Anvandarinformation for a user (anvandare).
   *
   * @param anvandarinformation the user information object.
   * @return The resulting user information.
   */
  Anvandarinformation updateAnvandarinformation(final Anvandarinformation anvandarinformation);

  /**
   * Calls /anvandare/filtrerade with query parameters as specified in the params Map.
   * See Ladok REST documentation for more information about parameters.
   *
   * <pre>
   * {@code
   * Map<String, Object> params = new HashMap<String, Object>();
   * params.put("anvandarnamn", "fjo@kth.se");
   * AnvadarLista res =
   *     katatlogInformationService.anvandareFiltrerade(params);
   * }
   * </pre>
   *
   * <p>Objects passed as values will be rendered into parameters using their
   * toString() method.
   *
   * @param params A map between parameter strings and their object values.
   * @return The search result.
   */
  AnvandareLista anvandareFiltrerade(final Map<String, Object> params);

  OrganisationLista organisationLista();

}
