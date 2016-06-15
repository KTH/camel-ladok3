package se.kth.infosys.smx.ladok3;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ladok3 producer.
 */
public class ladok3Producer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(ladok3Producer.class);
    private ladok3Endpoint endpoint;

    public ladok3Producer(ladok3Endpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());    
    }

}
