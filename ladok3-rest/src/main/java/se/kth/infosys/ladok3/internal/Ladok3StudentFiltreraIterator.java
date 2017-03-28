package se.kth.infosys.ladok3.internal;

import java.util.Iterator;
import java.util.Map;

import se.kth.infosys.ladok3.Ladok3StudentInformationService;
import se.ladok.schemas.studentinformation.StudentISokresultat;

public class Ladok3StudentFiltreraIterator extends Ladok3StudentFiltreraIteratorBase implements Iterator<StudentISokresultat> {
    public Ladok3StudentFiltreraIterator(Ladok3StudentInformationService ladok3StudentInformationService,
            Map<String, Object> params) {
        super(ladok3StudentInformationService, params);
    }

    @Override
    public StudentISokresultat next() {
        if (iterator.hasNext()) {
            return iterator.next();
        } else if (serviceHasNext()) {
            getNextPage();
            return next();
        }
        return null;
    }
}
