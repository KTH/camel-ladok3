package se.kth.infosys.smx.ladok3;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

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

        final URI formattedUri = new URI(uri);
        endpoint.setHost(formattedUri.getHost());

        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(new File(endpoint.getCert())), endpoint.getKey().toCharArray());

        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, endpoint.getKey().toCharArray());

        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(kmf.getKeyManagers(), null, null);
        
        endpoint.setSocketFactory(context.getSocketFactory());
        return endpoint;
    }
}
