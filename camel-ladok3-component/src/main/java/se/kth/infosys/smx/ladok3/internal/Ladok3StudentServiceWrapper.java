package se.kth.infosys.smx.ladok3.internal;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

import org.apache.camel.Exchange;
import org.apache.camel.util.ExchangeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.infosys.ladok3.Ladok3StudentInformationService;
import se.ladok.schemas.studentinformation.Student;

public class Ladok3StudentServiceWrapper implements Ladok3ServiceWrapper {
    private static final Logger log = LoggerFactory.getLogger(Ladok3StudentServiceWrapper.class);
    private static final Pattern URL_PATTERN = Pattern.compile("(/student(/(?<operation>personnummer|filtrera))+.*");
    private Ladok3StudentInformationService service;
    private String pathOperation;

    public Ladok3StudentServiceWrapper(URI uri, SSLContext context) throws Exception {
        this.service = new Ladok3StudentInformationService(uri.getHost(), context);
        Matcher matcher = URL_PATTERN.matcher(uri.getPath());
        if (matcher.matches()) {
            pathOperation = matcher.group("operation").toUpperCase();
        }
    }

    public void doExchange(Exchange exchange) throws Exception {
        switch (currentOperation(exchange)) {
        case "PERSONNUMMER":
            handleStudentPersonnummerRequest(exchange, service);
            break;
        default: // uid request
            handleStudentUidRequest(exchange, service);
        }
    }

    private void handleStudentPersonnummerRequest(final Exchange exchange, final Ladok3StudentInformationService service) throws Exception {
        String personnummer = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);
        if (personnummer == null || personnummer.isEmpty()) {
            Student student = exchange.getIn().getMandatoryBody(Student.class);
            personnummer = student.getPersonnummer();
        }

        log.debug("Getting Ladok data for student with pnr: {}", personnummer);
        Student fromLadok = service.studentPersonnummer(personnummer);
        exchange.getOut().setBody(fromLadok);
    }

    private void handleStudentUidRequest(final Exchange exchange, final Ladok3StudentInformationService service) throws Exception {
        String uid = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);
        if (uid == null || uid.isEmpty()) {
            Student student = exchange.getIn().getMandatoryBody(Student.class);
            uid = student.getPersonnummer();
        }

        log.debug("Getting Ladok data for student with uid: {}", uid);
        Student fromLadok = service.studentUID(uid);
        exchange.getOut().setBody(fromLadok);
    }

    private String currentOperation(Exchange exchange) throws Exception {
        if (pathOperation != null && ! pathOperation.isEmpty()) {
            return pathOperation;
        }
        return ExchangeHelper.getMandatoryHeader(exchange, Ladok3Message.Header.Operation, String.class);
    }
}
