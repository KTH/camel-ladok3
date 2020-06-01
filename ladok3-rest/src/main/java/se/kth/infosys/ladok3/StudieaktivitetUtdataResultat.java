package se.kth.infosys.ladok3;

import java.util.Iterator;
import java.util.Map;

import se.ladok.schemas.studiedeltagande.UtdataResultat;
import se.ladok.schemas.studiedeltagande.UtdataResultatrad;
import se.ladok.schemas.studiedeltagande.Utdatafraga;

public class StudieaktivitetUtdataResultat implements Iterable<UtdataResultatrad> {
    private UtdataResultatradIterator iterator;

    public StudieaktivitetUtdataResultat(
            final StudiedeltagandeService studiedeltagandeService,
            final Map<String, Object> params) {
        this.iterator = new UtdataResultatradIterator(studiedeltagandeService, params);
    }

    @Override
    public Iterator<UtdataResultatrad> iterator() {
        return iterator;
    }

    public class UtdataResultatradIterator implements Iterator<UtdataResultatrad> {
        private StudiedeltagandeService service;
        private Iterator<UtdataResultatrad> iterator;
        private UtdataResultat result;

        //private Map<String, Object> params;
        private int page = 0;
        private int limit = 400;
        private Utdatafraga utdatafraga;

        public UtdataResultatradIterator(
                final StudiedeltagandeService studiedeltagandeService,
                final Map<String, Object> params) {
            this.service = studiedeltagandeService;
            utdatafraga = new Utdatafraga();

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
            utdatafraga.setSidstorlek(limit);
            utdatafraga.setSida(++page);
            result = service.utdataStudieaktivitetOchFinansiering(utdatafraga);
            iterator = result.getResultatrader().iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext() || serviceHasNext();
        }

        protected boolean serviceHasNext() {
            return result.getResultatrader().size() == limit;
        }


        @Override
        public UtdataResultatrad next() {
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
