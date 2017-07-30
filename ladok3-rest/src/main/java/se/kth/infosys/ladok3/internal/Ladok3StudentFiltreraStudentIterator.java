package se.kth.infosys.ladok3.internal;

import java.util.Iterator;
import java.util.Map;

import se.kth.infosys.ladok3.Ladok3StudentinformationService;
import se.ladok.schemas.studentinformation.Student;
import se.ladok.schemas.studentinformation.StudentISokresultat;

public class Ladok3StudentFiltreraStudentIterator implements Iterator<Student> {
    private Ladok3StudentinformationService service;
    private Iterator<StudentISokresultat> iterator;

    public Ladok3StudentFiltreraStudentIterator(
            final Ladok3StudentinformationService ladok3StudentInformationService,
            final Map<String, Object> params) {
        this.service = ladok3StudentInformationService;
        this.iterator = service.studentFiltreraIterator(params);
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
