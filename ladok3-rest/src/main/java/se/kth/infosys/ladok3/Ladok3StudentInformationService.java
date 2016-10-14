package se.kth.infosys.ladok3;

import javax.ws.rs.client.WebTarget;

import se.ladok.schemas.studentinformation.Student;

public class Ladok3StudentInformationService extends LadokService {
    private static final String STUDENTINFORMATION_XML = "application/vnd.ladok-studentinformation+xml";
    private final WebTarget studentinformation;

    public Ladok3StudentInformationService(String host, String certFile, String key) throws Exception {
        super(host, certFile, key);
        this.studentinformation = client.target(String.format("https://%s/studentinformation", host));
    }

    public Student getStudentByPersonnummer(String personnummer) {
        return studentinformation.path("/student/personnummer/{personnummer}")
                .resolveTemplate("personnummer", personnummer)
                .request()
                .accept(STUDENTINFORMATION_XML)
                .get(Student.class);
    }
}
