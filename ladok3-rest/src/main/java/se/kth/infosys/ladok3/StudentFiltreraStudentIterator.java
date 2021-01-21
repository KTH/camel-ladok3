package se.kth.infosys.ladok3;
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

import java.util.Iterator;
import java.util.Map;
import se.ladok.schemas.studentinformation.Student;
import se.ladok.schemas.studentinformation.StudentISokresultat;

class StudentFiltreraStudentIterator implements Iterator<Student> {
  private StudentinformationServiceImpl service;
  private Iterator<StudentISokresultat> iterator;

  public StudentFiltreraStudentIterator(
          final StudentinformationServiceImpl ladok3StudentInformationService,
          final Map<String, Object> params) {
    this.service = ladok3StudentInformationService;
    this.iterator = service.studentFiltreraIterable(params).iterator();
  }

  @Override
  public Student next() {
    if (iterator.hasNext()) {
      return service.student(iterator.next().getUid());
    }
    return null;
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }
}
