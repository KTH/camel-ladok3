package se.kth.infosys.smx.ladok3.internal;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

import org.apache.camel.Exchange;
import org.apache.camel.util.ExchangeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.infosys.ladok3.Ladok3StudentinformationService;
import se.kth.infosys.ladok3.StudentinformationService;
import se.kth.infosys.smx.ladok3.Ladok3Message;
import se.ladok.schemas.studentinformation.Kontaktuppgifter;
import se.ladok.schemas.studentinformation.Student;

public class Ladok3StudentInformationServiceWrapper implements Ladok3ServiceWrapper {
    private static final Logger log = LoggerFactory.getLogger(Ladok3StudentInformationServiceWrapper.class);
    private static final Pattern URL_PATTERN = Pattern.compile("^/student(/(?<operation>personnummer|kontaktinformation|filtrera))+.*");
    private StudentinformationService service;
    private String pathOperation;

    public Ladok3StudentInformationServiceWrapper(URI uri, SSLContext context) throws Exception {
        this.service = new Ladok3StudentinformationService(uri.getHost(), context);
        Matcher matcher = URL_PATTERN.matcher(uri.getPath());
        if (matcher.matches()) {
            pathOperation = matcher.group("operation").toLowerCase();
        }
    }

    public void doExchange(Exchange exchange) throws Exception {
        switch (currentOperation(exchange)) {
        case "personnummer":
            handleStudentPersonnummerRequest(exchange, service);
            break;
        case "kontaktinformation":
            handleStudentKontaktinformationRequest(exchange, service);
            break;
        case "filtrera":
            handleStudentFiltreraRequest(exchange, service);
            break;
        default: // uid request
            handleStudentUidRequest(exchange, service);
        }
    }

    private void handleStudentFiltreraRequest(Exchange exchange, StudentinformationService service2) {
        List<Student> fromLadok = new LinkedList<Student>();

        @SuppressWarnings("unchecked")
        HashMap<String, Object> params = exchange.getIn().getHeader(
                Ladok3Message.Header.Params, new HashMap<String, Object>(), HashMap.class);

        log.debug("Getting Ladok data for student filtrera request with params: {}", params);
        Iterator<Student> studentIterator = service.studentFiltreraStudentIterator(params);
        while (studentIterator.hasNext()) {
            fromLadok.add(studentIterator.next());
        }
        exchange.getOut().setBody(fromLadok);
    }

    private void handleStudentPersonnummerRequest(final Exchange exchange, final StudentinformationService service) throws Exception {
        String personnummer = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);
        if (personnummer == null || personnummer.isEmpty()) {
            Student student = exchange.getIn().getMandatoryBody(Student.class);
            personnummer = student.getPersonnummer();
        }

        log.debug("Getting Ladok data for student with pnr: {}", personnummer);
        Student fromLadok = service.studentPersonnummer(personnummer);
        exchange.getOut().setBody(fromLadok);
    }

    private void handleStudentKontaktinformationRequest(final Exchange exchange, final StudentinformationService service) throws Exception {
        String uid = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);
        if (uid == null || uid.isEmpty()) {
            Student student = exchange.getIn().getMandatoryBody(Student.class);
            uid = student.getUid();
        }

        log.debug("Getting kontaktinformation for student with uid: {}", uid);
        Kontaktuppgifter fromLadok = service.studentKontaktuppgifter(uid);
        exchange.getOut().setBody(fromLadok);
    }

    private void handleStudentUidRequest(final Exchange exchange, final StudentinformationService service) throws Exception {
        String uid = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);
        if (uid == null || uid.isEmpty()) {
            Student student = exchange.getIn().getMandatoryBody(Student.class);
            uid = student.getPersonnummer();
        }

        log.debug("Getting Ladok data for student with uid: {}", uid);
        Student fromLadok = service.student(uid);
        exchange.getOut().setBody(fromLadok);
    }

    private String currentOperation(final Exchange exchange) throws Exception {
        if (pathOperation != null && ! pathOperation.isEmpty()) {
            return pathOperation;
        }
        return ExchangeHelper.getMandatoryHeader(exchange, Ladok3Message.Header.Operation, String.class);
    }
}
