package se.kth.infosys.smx.ladok3.internal;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class Ladok3UnmarshallerFactory {
    private static final Map<String, Unmarshaller> unmarshallers = new HashMap<String, Unmarshaller>();

    /** Since the split of the Ladok model into api- and event-jars, there are ObjectFactories with identical
     * FQCNs. So, if you for example send in "se.ladok.schema.resultat" and have the ladok-api-jar before
     * the event-jar in the CP, the ObjectFactory from the api-jar will be used and no Events can be unmarshalled.
     */
    @Deprecated
    public static Unmarshaller unmarshaller(String context) throws JAXBException, ClassNotFoundException {
        if (! unmarshallers.containsKey(context)) {
            JAXBContext jC = JAXBContext.newInstance(context);
            unmarshallers.put(context, jC.createUnmarshaller());
        }
        return unmarshallers.get(context);
    }

    public static Unmarshaller unmarshaller(String context, Class<?> eventClass) throws JAXBException, ClassNotFoundException {
        if (! unmarshallers.containsKey(context)) {
            JAXBContext jC = JAXBContext.newInstance(eventClass);
            unmarshallers.put(context, jC.createUnmarshaller());
        }
        return unmarshallers.get(context);
    }
}
