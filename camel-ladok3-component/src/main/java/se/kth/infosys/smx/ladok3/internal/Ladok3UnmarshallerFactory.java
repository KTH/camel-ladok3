package se.kth.infosys.smx.ladok3.internal;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class Ladok3UnmarshallerFactory {
  private static final Map<String, Unmarshaller> unmarshallers = new HashMap<>();

  public static Unmarshaller unmarshaller(String context, Class<?> eventClass) throws JAXBException {
    if (!unmarshallers.containsKey(context)) {
      JAXBContext jaxbContext = JAXBContext.newInstance(eventClass);
      unmarshallers.put(context, jaxbContext.createUnmarshaller());
    }
    return unmarshallers.get(context);
  }
}
