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

/**
 * Interface representing the Ladok kataloginformation service.
 */
public interface KataloginformationService extends Service {
    /**
     * Retrieve a user (anvandare) given its UID.
     * @param uid The unique identifier for the user.
     * @return The user matching the UID
     */
    public Anvandare anvandare(String uid);

    /**
     * Create a user.
     * 
     * @param anvandare The user object.
     * @return The created user.
     */
    public Anvandare createAnvandare(Anvandare anvandare);

    /**
     * Update a user.
     * 
     * @param anvandare The user object.
     * @return The updated user.
     */
    public Anvandare updateAnvandare(Anvandare anvandare);

    /**
     * Retrieve Anvandarinformation for a user (anvandare) given its UID.
     * 
     * @param uid The unique identifier for the user.
     * @return The user information matching the UID
     */
    public Anvandarinformation anvandarinformation(String uid);

    /**
     * Create Anvandarinformation for a user (anvandare).
     * 
     * @param anvandarinformation the user information object.
     * @return The resulting user information.
     */
    public Anvandarinformation createAnvandarinformation(Anvandarinformation anvandarinformation);

    /**
     * Update Anvandarinformation for a user (anvandare) given its UID.
     * 
     * @param anvandarinformation the user information object.
     * @return The resulting user information.
     */
    public Anvandarinformation updateAnvandarinformation(Anvandarinformation anvandarinformation);

    /**
     * Calls /anvandare with query parameters as specified in the params Map. 
     * See Ladok REST documentation for more information about parameters.
     *
     * {@code
     * Map<String, Object> params = new HashMap<String, Object>();
     * params.put("anvandarnamn", "fjo@kth.se");
     * AnvadarLista res =
     *     katatlogInformationService.anvandareFiltrerade(params);
     * 
     * Objects passed as values will be rendered into parameters using their 
     * toString() method.
     * }
     *
     * @param params A map between parameter strings and their object values.
     * @return The search result.
     */
    public AnvandareLista anvandare(Map<String, Object> params);

    /**
     * Calls /anvandare/filtrerade with query parameters as specified in the params Map. 
     * See Ladok REST documentation for more information about parameters.
     *
     * {@code
     * Map<String, Object> params = new HashMap<String, Object>();
     * params.put("anvandarnamn", "fjo@kth.se");
     * AnvadarLista res =
     *     katatlogInformationService.anvandareFiltrerade(params);
     * 
     * Objects passed as values will be rendered into parameters using their 
     * toString() method.
     * }
     *
     * @param params A map between parameter strings and their object values.
     * @return The search result.
     */
    public AnvandareLista anvandareFiltrerade(Map<String, Object> params);
}
