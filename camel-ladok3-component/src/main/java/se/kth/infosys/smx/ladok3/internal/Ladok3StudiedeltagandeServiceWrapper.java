package se.kth.infosys.smx.ladok3.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;
import org.apache.camel.CamelExchangeException;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.infosys.ladok3.StudiedeltagandeService;
import se.kth.infosys.ladok3.StudiedeltagandeServiceImpl;
import se.kth.infosys.ladok3.utdata.StudieaktivitetOchFinansiering;
import se.kth.infosys.smx.ladok3.Ladok3Message;
import se.ladok.schemas.studentinformation.Student;
import se.ladok.schemas.studiedeltagande.IngaendeKurspaketeringstillfalleLista;
import se.ladok.schemas.studiedeltagande.PeriodLista;
import se.ladok.schemas.studiedeltagande.TillfallesdeltagandeLista;

public class Ladok3StudiedeltagandeServiceWrapper implements Ladok3ServiceWrapper {
  private static final Logger log = LoggerFactory.getLogger(Ladok3StudiedeltagandeServiceWrapper.class);
  private static final Pattern URL_PATTERN = Pattern.compile(
          "^/studiedeltagande(/(?<operation>"
                  + "tillfallesdeltagande/kurstillfallesdeltagande/student|"
                  + "utdata/studieaktivitetochfinansiering|"
                  + "pabarjadutbildning/kurspaketering/student|"
                  + "studiestruktur/student|"
                  + "period"
                  + "))+.*");
  private StudiedeltagandeService service;
  private String pathOperation;

  public Ladok3StudiedeltagandeServiceWrapper(String host, String path, SSLContext context)
          throws Exception {
    this.service = new StudiedeltagandeServiceImpl(host, context);
    Matcher matcher = URL_PATTERN.matcher(path);
    if (matcher.matches()) {
      pathOperation = matcher.group("operation").toLowerCase();
    }
  }

  public void doExchange(Exchange exchange) throws Exception {
    final String operation = currentOperation(exchange);
    switch (operation) {
      case "tillfallesdeltagande/kurstillfallesdeltagande/student":
        handleKurstillfallesdeltagandeStudent(exchange);
        break;
      case "utdata/studieaktivitetochfinansiering":
        handleStudieaktivitetOchFinansiering(exchange);
        break;
      case "pabarjadutbildning/kurspaketering/student":
        handlePabarjadutbildningKurspaketeringStudent(exchange);
        break;
      case "studiestruktur/student":
        handleStudiestrukturStudent(exchange);
        break;
      case "period":
        getStudiedeltagandePerioder(exchange);
        break;
      default:
        throw new CamelExchangeException("Unupported operation: %s" + operation, exchange);
    }
  }

  private void handleKurstillfallesdeltagandeStudent(Exchange exchange) throws Exception {
    String uid = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);
    if (uid == null || uid.isEmpty()) {
      Student student = exchange.getIn().getMandatoryBody(Student.class);
      uid = student.getUid();
    }

    log.debug("Getting kurstillfallesdeltaganden for student with uid: {}", uid);
    TillfallesdeltagandeLista fromLadok = service.kurstillfallesdeltagandeStudent(uid);
    exchange.getIn().setBody(fromLadok);
  }

  private void handleStudieaktivitetOchFinansiering(Exchange exchange) {
    @SuppressWarnings("unchecked")
    HashMap<String, Object> params = exchange.getIn().getHeader(
            Ladok3Message.Header.Params, new HashMap<String, Object>(), HashMap.class);

    log.debug("Getting Ladok data for studieaktivetet och finansiering request with params: {}",
            params);
    Iterator<StudieaktivitetOchFinansiering> fromLadok =
            service.utdataStudieaktivitetOchFinansieringIteraterable(params).iterator();
    exchange.getIn().setBody(fromLadok);
  }

  private void handleStudiestrukturStudent(Exchange exchange) throws Exception {
    String uid = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);
    if (uid == null || uid.isEmpty()) {
      Student student = exchange.getIn().getMandatoryBody(Student.class);
      uid = student.getUid();
    }

    log.debug("Getting studiestruktur for student with uid: {}", uid);
    IngaendeKurspaketeringstillfalleLista fromLadok = service.studiestrukturStudent(uid);
    exchange.getIn().setBody(fromLadok);
  }

  private void handlePabarjadutbildningKurspaketeringStudent(Exchange exchange) throws Exception {
    String uid = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);
    if (uid == null || uid.isEmpty()) {
      Student student = exchange.getIn().getMandatoryBody(Student.class);
      uid = student.getUid();
    }

    log.debug("Getting tillfallesdeltagandelista for student with uid: {}", uid);
    TillfallesdeltagandeLista fromLadok = service.pabarjadutbildningKurspaketeringStudent(uid);
    exchange.getIn().setBody(fromLadok);
  }

  private String currentOperation(final Exchange exchange) throws Exception {
    if (pathOperation != null && !pathOperation.isEmpty()) {
      return pathOperation;
    }

    return exchange.getIn().getHeader(Ladok3Message.Header.Operation, String.class);
  }

  private void getStudiedeltagandePerioder(Exchange exchange) throws Exception {
    PeriodLista fromLadok = service.studiedeltagandePeriod();
    exchange.getIn().setBody(fromLadok);

  }
}
