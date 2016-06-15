package se.kth.infosys.smx.ladok3;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.UriEndpointComponent;

/**
 * Represents the component that manages {@link ladok3Endpoint}.
 */
public class ladok3Component extends UriEndpointComponent {
    
    public ladok3Component() {
        super(ladok3Endpoint.class);
    }

    public ladok3Component(CamelContext context) {
        super(context, ladok3Endpoint.class);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new ladok3Endpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
