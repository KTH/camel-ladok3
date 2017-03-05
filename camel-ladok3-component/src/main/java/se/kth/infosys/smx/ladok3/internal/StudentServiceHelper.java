package se.kth.infosys.smx.ladok3.internal;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.infosys.ladok3.Ladok3StudentInformationService;
import se.ladok.schemas.studentinformation.Student;

public class StudentServiceHelper {
    private static final Logger log = LoggerFactory.getLogger(StudentServiceHelper.class);

    public static void handleStudentPersonnummerRequest(final Exchange exchange, final Ladok3StudentInformationService service) throws Exception {
        String personnummer = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);
        if (personnummer == null || personnummer.isEmpty()) {
            Student student = exchange.getIn().getMandatoryBody(Student.class);
            personnummer = student.getPersonnummer();
        }

        log.debug("Getting Ladok data for student with pnr: {}", personnummer);
        Student fromLadok = service.studentPersonnummer(personnummer);
        exchange.getOut().setBody(fromLadok);
    }

    public static void handleStudentUidRequest(final Exchange exchange, final Ladok3StudentInformationService service) throws Exception {
        String uid = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);
        if (uid == null || uid.isEmpty()) {
            Student student = exchange.getIn().getMandatoryBody(Student.class);
            uid = student.getPersonnummer();
        }

        log.debug("Getting Ladok data for student with uid: {}", uid);
        Student fromLadok = service.studentUID(uid);
        exchange.getOut().setBody(fromLadok);
    }
}
