package se.kth.infosys.smx.ladok3;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ladok3 producer.
 */
public class Ladok3Producer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(Ladok3Producer.class);
    private Ladok3Endpoint endpoint;

    public Ladok3Producer(Ladok3Endpoint endpoint) throws Exception {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());    
    }

}
