package se.kth.infosys.smx.ladok3.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.infosys.ladok3.StudentinformationService;
import se.kth.infosys.ladok3.StudentinformationServiceImpl;
import se.kth.infosys.smx.ladok3.Ladok3Message;
import se.ladok.schemas.studentinformation.Kontaktuppgifter;
import se.ladok.schemas.studentinformation.Student;
import se.ladok.schemas.studentinformation.StudentISokresultat;
import se.ladok.schemas.studentinformation.Studenthistorikposter;

public class Ladok3StudentInformationServiceWrapper implements Ladok3ServiceWrapper {
  private static final Logger log = LoggerFactory.getLogger(Ladok3StudentInformationServiceWrapper.class);
  private static final Pattern URL_PATTERN = Pattern
          .compile("^/student(/(?<operation>personnummer|kontaktinformation|filtrera|historik))+.*");
  private final StudentinformationService service;
  private String pathOperation;

  public Ladok3StudentInformationServiceWrapper(String host, String path, SSLContext context) throws Exception {
    this.service = new StudentinformationServiceImpl(host, context);
    Matcher matcher = URL_PATTERN.matcher(path);
    if (matcher.matches()) {
      pathOperation = matcher.group("operation").toLowerCase();
    }
  }

  public void doExchange(Exchange exchange) throws Exception {
    switch (currentOperation(exchange)) {
      case "personnummer":
        handleStudentPersonnummerRequest(exchange);
        break;
      case "kontaktinformation":
        handleStudentKontaktinformationRequest(exchange);
        break;
      case "filtrera":
        handleStudentFiltreraRequest(exchange);
        break;
      case "historik":
        handleStudentHistorikRequest(exchange);
        break;
      default: // uid request
        handleStudentUidRequest(exchange);
    }
  }

  private void handleStudentHistorikRequest(Exchange exchange) throws Exception {
    String uid = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);
    if (uid == null || uid.isEmpty()) {
      Student student = exchange.getIn().getMandatoryBody(Student.class);
      uid = student.getUid();
    }

    log.debug("Getting history for student with uid: {}", uid);
    Studenthistorikposter fromLadok = service.studentHistorik(uid);
    exchange.getIn().setBody(fromLadok);
  }

  private void handleStudentFiltreraRequest(Exchange exchange) {
    @SuppressWarnings("unchecked")
    HashMap<String, Object> params = exchange.getIn().getHeader(
            Ladok3Message.Header.Params, new HashMap<String, Object>(), HashMap.class);

    log.debug("Getting Ladok data for student filtrera request with params: {}", params);
    Iterator<StudentISokresultat> fromLadok = service.studentFiltreraIterable(params).iterator();
    exchange.getIn().setBody(fromLadok);
  }

  private void handleStudentPersonnummerRequest(final Exchange exchange) throws Exception {
    String personnummer = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);
    if (personnummer == null || personnummer.isEmpty()) {
      Student student = exchange.getIn().getMandatoryBody(Student.class);
      personnummer = student.getPersonnummer();
    }

    log.debug("Getting data for student with pnr: {}", personnummer);
    Student fromLadok = service.studentPersonnummer(personnummer);
    exchange.getIn().setBody(fromLadok);
  }

  private void handleStudentKontaktinformationRequest(final Exchange exchange) throws Exception {
    String uid = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);
    if (uid == null || uid.isEmpty()) {
      Student student = exchange.getIn().getMandatoryBody(Student.class);
      uid = student.getUid();
    }

    log.debug(String.format("Getting kontaktinformation for student with uid: %s", uid));
    Kontaktuppgifter fromLadok = service.studentKontaktuppgifter(uid);
    exchange.getIn().setBody(fromLadok);
  }

  private void handleStudentUidRequest(final Exchange exchange) throws Exception {
    String uid = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);
    if (uid == null || uid.isEmpty()) {
      Student student = exchange.getIn().getMandatoryBody(Student.class);
      uid = student.getPersonnummer();
    }

    log.debug(String.format("Getting data for student with uid: %s", uid));
    Student fromLadok = service.student(uid);
    exchange.getIn().setBody(fromLadok);
  }

  private String currentOperation(final Exchange exchange) throws Exception {
    if (pathOperation != null && !pathOperation.isEmpty()) {
      return pathOperation;
    }

    String headerOperation = exchange.getIn().getHeader(Ladok3Message.Header.Operation, String.class);
    if (headerOperation != null && !headerOperation.isEmpty()) {
      return headerOperation;
    }

    return "uid";
  }
}
