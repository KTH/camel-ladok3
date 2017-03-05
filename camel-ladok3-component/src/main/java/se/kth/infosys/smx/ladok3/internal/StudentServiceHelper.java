package se.kth.infosys.smx.ladok3.internal;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.infosys.ladok3.Ladok3StudentInformationService;
import se.ladok.schemas.studentinformation.Student;

public class StudentServiceHelper {
    private static final Logger log = LoggerFactory.getLogger(StudentServiceHelper.class);

    public static void handleStudentPersonnummerRequest(Exchange exchange, Ladok3StudentInformationService service) throws Exception {
        Student student = exchange.getIn().getMandatoryBody(Student.class);

        log.debug("Getting Ladok data for student with pnr: {}", student.getPersonnummer());
        Student fromLadok = service.studentPersonnummer(student.getPersonnummer());
        exchange.getOut().setBody(fromLadok);
    }

    public static void handleStudentUidRequest(Exchange exchange, Ladok3StudentInformationService service) throws Exception {
        Student student = exchange.getIn().getMandatoryBody(Student.class);

        log.debug("Getting Ladok data for student with uid: {}", student.getUid());
        Student fromLadok = service.studentUID(student.getUid());
        exchange.getOut().setBody(fromLadok);
    }
}
