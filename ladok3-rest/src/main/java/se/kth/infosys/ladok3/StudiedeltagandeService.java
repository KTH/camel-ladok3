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

import se.kth.infosys.ladok3.utdata.StudieaktivitetOchFinansiering;
import se.ladok.schemas.studiedeltagande.IngaendeKurspaketeringstillfalleLista;
import se.ladok.schemas.studiedeltagande.TillfallesdeltagandeLista;
import se.ladok.schemas.studiedeltagande.PeriodLista;
import se.ladok.schemas.studiedeltagande.UtdataResultat;
import se.ladok.schemas.studiedeltagande.Utdatafraga;

/**
 * Interface representing the Ladok studiedeltagande service.
 */
public interface StudiedeltagandeService extends Ladok3Service {
    /**
     * Lista alla påbörjade kurspaketeringar för en student.
     *
     * @param uid student UID
     * @return påbörjade kurspaketeringar.
     */
    public TillfallesdeltagandeLista pabarjadutbildningKurspaketeringStudent(final String uid);

    /**
     * Hämta studiestrukturer för student, en per rotkurspaketeringstillfälle.
     *
     * @param uid student UID
     * @return studiestrukturer för student.
     */
    public IngaendeKurspaketeringstillfalleLista studiestrukturStudent(final String uid);

    /**
     * Sök studieaktivitet och finansiering.
     *
     * Anropar /studiedeltagande/utdata/studieaktivitetochfinansiering med frågeparametrar
     * enligt params. Se Ladok REST-dokumentation för mer information om parametrar.
     *
     * <pre>
     * {@code
     * Map<String, Object> params = new HashMap<String, Object>();
     * params.put("datumperiod", "2018-01-14_2018-06-04");
     * SokresultatStudieAktivitetOchFinansiering res =
     *     studiedeltagandeService.utdataStudieaktivitetOchFinansiering(params);
     * }
     * </pre>
     *
     * @param utdatafraga A <code>Utdatafraga</code>.
     * @return The search result.
     */
    public UtdataResultat utdataStudieaktivitetOchFinansiering(final Utdatafraga utdatafraga);
    public Utdatafraga createUtdatafraga(Map<String, Object> params);

    /**
     * Higher abstraction of {@link #utdataStudieaktivitetOchFinansiering} method which returns
     * an iterable of StudieaktivitetUtdata hiding all paging related issues.
     *
     * @param params A map between parameter strings and their object values.
     * @return an iterable for all search results matching params.
     */
    public Iterable<StudieaktivitetOchFinansiering> utdataStudieaktivitetOchFinansieringIteraterable(final Map<String, Object> params);

    /**
     * Hämta alla kurstillfällesdeltaganden för en student.
     *
     * @param uid student UID
     * @return kurstillfällesdeltagande för student.
     */
    public TillfallesdeltagandeLista kurstillfallesdeltagandeStudent(String uid);

    /**
     * Hämta alla perioder för studiedeltagande.
     *
     * @return perioder för studiedeltagande
     */
    public PeriodLista studiedeltagandePeriod();
}
