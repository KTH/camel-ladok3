package se.kth.infosys.ladok3.internal;

import java.util.Iterator;
import java.util.Map;

import se.kth.infosys.ladok3.Ladok3StudentInformationService;
import se.ladok.schemas.studentinformation.Student;

public class Ladok3StudentFiltreraStudentIterator extends Ladok3StudentFiltreraIteratorBase implements Iterator<Student> {
    public Ladok3StudentFiltreraStudentIterator(Ladok3StudentInformationService ladok3StudentInformationService,
            Map<String, Object> params) {
        super(ladok3StudentInformationService, params);
    }

    @Override
    public Student next() {
        if (iterator.hasNext()) {
            return service.studentUID(iterator.next().getUid());
        } else if (serviceHasNext()) {
            getNextPage();
            return service.studentUID(iterator.next().getUid());
        }
        return null;
    }
}
