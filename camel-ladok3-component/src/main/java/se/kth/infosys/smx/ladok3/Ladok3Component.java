package se.kth.infosys.smx.ladok3;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

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

        intializeSSL(endpoint);
        return endpoint;
    }

    private void intializeSSL(Ladok3Endpoint endpoint) throws Exception {
        try {
	        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
	        keyStore.load(new FileInputStream(new File(endpoint.getCert())), endpoint.getKey().toCharArray());
	
	        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	        kmf.init(keyStore, endpoint.getKey().toCharArray());
	
	        final SSLContext serverContext = SSLContext.getInstance("TLS");
	        serverContext.init(kmf.getKeyManagers(), null, null);
	
	        final SSLEngine engine = serverContext.createSSLEngine();
	        final SSLParameters sslParams = new SSLParameters();
	        sslParams.setNeedClientAuth(true);
	        engine.setSSLParameters(sslParams);
	
	        final SSLSocketFactory socketFactory = serverContext.getSocketFactory();
	        HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);
        } catch (Exception e) {
        	e.printStackTrace();
        	throw e;
        }
    }
    
}
