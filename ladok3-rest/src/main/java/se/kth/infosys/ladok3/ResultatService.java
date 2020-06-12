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

import se.ladok.schemas.resultat.Aktivitetstillfalle;
import se.ladok.schemas.resultat.AktivitetstillfalleForStudentLista;
import se.ladok.schemas.resultat.SokresultatStudieresultatResultat;
import se.ladok.schemas.resultat.StudieresultatForRapporteringPaAktivitetstillfalleSokVarden;

/**
 * Interface representing the Ladok Resultat service
 */
public interface ResultatService extends Ladok3Service {
  
  /**
   * Retrieve an Aktivitetstillfälle 
   * @param aktivitetstillfalleUID
   * @return the Aktivitetsfillfälle 
   */
  public Aktivitetstillfalle aktivitetstillfalle(final String aktivitetstillfalleUID);

  /**
   * Retrieve aktivitetstillfallen for a student
   * @param uid
   * @return a list of aktivitestillfallen for a student
   */
  public AktivitetstillfalleForStudentLista aktivitetstillfalleForStudentLista(final String uid);
  
  /**
   * Retrieve studieresultat for reporting of results on an Aktivitetstillfalle
   */
  public SokresultatStudieresultatResultat sokresultatStudieresultatResultat(final String aktivitetstillfalleUID, final Map<String, Object> sokVarden); 

  /**
   * Creates a representation for SokresultatStudieresultatResultat 
   */
  public StudieresultatForRapporteringPaAktivitetstillfalleSokVarden createSokresultatStudieResultatSokVarden(Map<String, Object> params); 

}

