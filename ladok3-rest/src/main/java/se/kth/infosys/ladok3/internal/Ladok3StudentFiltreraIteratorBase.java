package se.kth.infosys.ladok3.internal;

import java.util.Iterator;
import java.util.Map;

import se.kth.infosys.ladok3.Ladok3StudentInformationService;
import se.ladok.schemas.studentinformation.SokresultatStudentinformationRepresentation;
import se.ladok.schemas.studentinformation.StudentISokresultat;

public abstract class Ladok3StudentFiltreraIteratorBase {
    private static final int DEFAULT_LIMIT = 400;

    protected Ladok3StudentInformationService service;
    protected SokresultatStudentinformationRepresentation result;
    protected Iterator<StudentISokresultat> iterator;

    private Map<String, Object> params;
    private int page = 0;
    private int limit = DEFAULT_LIMIT;

    public Ladok3StudentFiltreraIteratorBase(
            Ladok3StudentInformationService ladok3StudentInformationService,
            Map<String, Object> params) {
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

    public boolean hasNext() {
        return iterator.hasNext() || serviceHasNext();
    }

    protected boolean serviceHasNext() {
        return result.getResultat().size() == limit;
    }
}
