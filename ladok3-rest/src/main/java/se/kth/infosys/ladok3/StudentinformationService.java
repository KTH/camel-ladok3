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

import java.util.Iterator;
import java.util.Map;

import se.ladok.schemas.studentinformation.Kontaktuppgifter;
import se.ladok.schemas.studentinformation.SokresultatStudentinformationRepresentation;
import se.ladok.schemas.studentinformation.Student;
import se.ladok.schemas.studentinformation.StudentISokresultat;

/**
 * Interface representing the Ladok studentinformation service.
 */
public interface StudentinformationService extends Service {
    /**
     * Retrieve a student given a personnummer.
     * @param personnummer identifying the student.
     * @return The student matching the personnummer
     */
    public Student studentPersonnummer(String personnummer);

    /**
     * Retrieve a student given its UID.
     * @param uid The unique identifier for the student.
     * @return The student matching the UID
     */
    public Student student(String uid);

    /**
     * Retrieve contact information for a student given its UID.
     * @param uid The unique identifier for the student.
     * @return The contact information matching the UID
     */
    public Kontaktuppgifter studentKontaktuppgifter(String uid);

    /**
     * Calls /student/filtrera with query parameters as specified in the params Map. 
     * See Ladok REST documentation for more information about parameters. Only 
     * difference is that this method will default to "limit=400" and "page=1"
     * unless something else is specified. E.g:
     *
     * <pre>
     * {@code
     * Map<String, Object> params = new HashMap<String, Object>();
     * params.put("personnummer", "19870412031234");
     * SokresultatStudentinformationRepresentation res =
     *     studentInformationService.studentFiltrera(params);
     * }
     * </pre>
     * 
     * Objects passed as values will be rendered into parameters using their 
     * toString() method.
     *
     * @param params A map between parameter strings and their object values.
     * @return The search result.
     */
    public SokresultatStudentinformationRepresentation studentFiltrera(Map<String, Object> params);

    /**
     * Higher abstraction of {@link #studentFiltrera} method which returns 
     * an iterator of StudentISokresultat hiding all paging related issues.
     * 
     * @param params A map between parameter strings and their object values.
     * @return an iterator for all search results matching params.
     */
    public Iterator<StudentISokresultat> studentFiltreraIterator(Map<String, Object> params);

    /**
     * Higher abstraction of {@link #studentFiltrera} method which returns 
     * an iterator of Student hiding all paging related and call to student information
     * service issues.
     * 
     * @param params A map between parameter strings and their object values.
     * @return an iterator for all search results matching params.
     */
    public Iterator<Student> studentFiltreraStudentIterator(Map<String, Object> params);
}
