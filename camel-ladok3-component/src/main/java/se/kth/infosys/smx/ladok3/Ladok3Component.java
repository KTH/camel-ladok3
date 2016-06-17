package se.kth.infosys.smx.ladok3;

import java.net.URI;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.UriEndpointComponent;

/**
 * Represents the component that manages {@link Ladok3Endpoint}.
 */
public class Ladok3Component extends UriEndpointComponent {
    
    public Ladok3Component() {
        super(Ladok3Endpoint.class);
    }

    public Ladok3Component(CamelContext context) {
        super(context, Ladok3Endpoint.class);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Ladok3Endpoint endpoint = new Ladok3Endpoint(uri, this);
        setProperties(endpoint, parameters);

        URI formattedUri = new URI(uri);
        endpoint.setHost(formattedUri.getHost());
        return endpoint;
    }
}
