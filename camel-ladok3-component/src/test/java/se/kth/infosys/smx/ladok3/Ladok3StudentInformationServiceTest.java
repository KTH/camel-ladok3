package se.kth.infosys.smx.ladok3;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import se.kth.infosys.smx.ladok3.internal.Ladok3StudentInformationService;
import se.ladok.schemas.studentinformation.Student;

public class Ladok3StudentInformationServiceTest {
    private Ladok3StudentInformationService studentInformationService;
    private Properties properties = new Properties();

    @Before
    public void setup() throws Exception {
        properties.load(ClassLoader.getSystemResourceAsStream("test.properties"));

        String host = properties.getProperty("ladok3.host");
        String certFile = properties.getProperty("ladok3.cert.file");
        String key = properties.getProperty("ladok3.cert.key");

        studentInformationService = new Ladok3StudentInformationService(host, certFile, key);
    }

    @Test
    public void getStudentByPersonnummer() {
        String personnummer = properties.getProperty("ladok3.test.Ladok3StudentInformationServiceTest.getStudentByPersonnummer.personnummer");
        String fornamn = properties.getProperty("ladok3.test.Ladok3StudentInformationServiceTest.getStudentByPersonnummer.fornamn");
        String efternamn = properties.getProperty("ladok3.test.Ladok3StudentInformationServiceTest.getStudentByPersonnummer.efternamn");

        Student student = studentInformationService.getStudentByPersonnummer(personnummer);

        assertEquals(personnummer, student.getPersonnummer());
        assertEquals(fornamn, student.getFornamn());
        assertEquals(efternamn, student.getEfternamn());
    }
}
