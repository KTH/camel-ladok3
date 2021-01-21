package se.kth.infosys.ladok3;

import java.util.Iterator;
import java.util.Map;
import se.kth.infosys.ladok3.utdata.StudieaktivitetOchFinansiering;
import se.ladok.schemas.studiedeltagande.UtdataResultat;
import se.ladok.schemas.studiedeltagande.UtdataResultatrad;
import se.ladok.schemas.studiedeltagande.Utdatafraga;

public class StudieaktivitetUtdataResultat implements Iterable<StudieaktivitetOchFinansiering> {
  private StudieaktivitetOchFinansieringIterator iterator;

  public StudieaktivitetUtdataResultat(
          final StudiedeltagandeService studiedeltagandeService,
          final Map<String, Object> params) {
    this.iterator = new StudieaktivitetOchFinansieringIterator(studiedeltagandeService, params);
  }

  @Override
  public StudieaktivitetOchFinansieringIterator iterator() {
    return iterator;
  }

  public class StudieaktivitetOchFinansieringIterator implements Iterator<StudieaktivitetOchFinansiering> {
    private Iterator<UtdataResultatrad> iterator;

    public StudieaktivitetOchFinansieringIterator(
            final StudiedeltagandeService studiedeltagandeService,
            final Map<String, Object> params) {
      this.iterator = new UtdataResultatradIterator(studiedeltagandeService, params);
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public StudieaktivitetOchFinansiering next() {
      if (iterator.hasNext()) {
        return new StudieaktivitetOchFinansiering(iterator.next());
      }
      return null;
    }
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
      utdatafraga = service.createUtdatafraga(params);

      getNextPage();
    }

    protected void getNextPage() {
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
