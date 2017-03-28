package se.kth.infosys.ladok3.internal;

import java.util.Iterator;
import java.util.Map;

import se.kth.infosys.ladok3.Ladok3StudentInformationService;
import se.ladok.schemas.studentinformation.Student;
import se.ladok.schemas.studentinformation.StudentISokresultat;

public class Ladok3StudentFiltreraStudentIterator implements Iterator<Student> {
    private Ladok3StudentInformationService service;
    private Iterator<StudentISokresultat> iterator;

    public Ladok3StudentFiltreraStudentIterator(Ladok3StudentInformationService ladok3StudentInformationService,
            Map<String, Object> params) {
        this.service = ladok3StudentInformationService;
        this.iterator = service.studentFiltreraIterator(params);
    }

    @Override
    public Student next() {
        if (iterator.hasNext()) {
            return service.studentUID(iterator.next().getUid());
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
