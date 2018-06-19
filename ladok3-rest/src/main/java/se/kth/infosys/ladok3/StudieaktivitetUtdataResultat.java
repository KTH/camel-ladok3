package se.kth.infosys.ladok3;

import java.util.Iterator;
import java.util.Map;

import se.ladok.schemas.studiedeltagande.SokresultatStudieAktivitetOchFinansiering;
import se.ladok.schemas.studiedeltagande.StudieaktivitetUtdata;

public class StudieaktivitetUtdataResultat implements Iterable<StudieaktivitetUtdata> {
    private StudieaktivitetUtdataIterator iterator;

    public StudieaktivitetUtdataResultat(
            final StudiedeltagandeService studiedeltagandeService,
            final Map<String, Object> params) {
        this.iterator = new StudieaktivitetUtdataIterator(studiedeltagandeService, params);
    }

    @Override
    public Iterator<StudieaktivitetUtdata> iterator() {
        return iterator;
    }

    public class StudieaktivitetUtdataIterator implements Iterator<StudieaktivitetUtdata> {
        private StudiedeltagandeService service;
        private Iterator<StudieaktivitetUtdata> iterator;
        private SokresultatStudieAktivitetOchFinansiering result;

        private Map<String, Object> params;
        private int page = 0;
        private int limit = 400;

        public StudieaktivitetUtdataIterator(
                final StudiedeltagandeService studiedeltagandeService,
                final Map<String, Object> params) {
            this.service = studiedeltagandeService;
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
            result = service.utdataStudieaktivitetOchFinansiering(params);
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
        public StudieaktivitetUtdata next() {
            if (iterator.hasNext()) {
                return iterator.next();
            } else if (serviceHasNext()) {
                getNextPage();
                return next();
            }
            return null;
        }
    }
}
