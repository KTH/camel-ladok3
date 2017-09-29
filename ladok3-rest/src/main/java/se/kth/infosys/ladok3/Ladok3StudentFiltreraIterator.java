package se.kth.infosys.ladok3;

import java.util.Iterator;
import java.util.Map;

import se.ladok.schemas.studentinformation.SokresultatStudentinformationRepresentation;
import se.ladok.schemas.studentinformation.StudentISokresultat;

class Ladok3StudentFiltreraIterator implements Iterator<StudentISokresultat> {
    protected StudentinformationServiceImpl service;
    protected SokresultatStudentinformationRepresentation result;
    protected Iterator<StudentISokresultat> iterator;

    private Map<String, Object> params;
    private int page = 0;
    private int limit = 400;

    public Ladok3StudentFiltreraIterator(
            final StudentinformationServiceImpl ladok3StudentInformationService,
            final Map<String, Object> params) {
        this.service = ladok3StudentInformationService;
        this.params = params;

        if (params.get("limit") != null) {
            if (params.get("limit") instanceof Integer) {
                this.limit = (Integer) params.get("limit");
            } else {
                this.limit = Integer.parseInt((String) params.get("limit"));
            }
        }

        getNextPage();
    }

    protected void getNextPage() {
        params.put("limit", limit);
        params.put("page", ++page);
        result = service.studentFiltrera(params);
        iterator = result.getResultat().iterator();
    }

   @Override 
    public boolean hasNext() {
        return iterator.hasNext() || serviceHasNext();
    }

    protected boolean serviceHasNext() {
        return result.getResultat().size() == limit;
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
