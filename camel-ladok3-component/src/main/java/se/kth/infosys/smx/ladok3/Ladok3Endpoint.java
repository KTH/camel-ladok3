package se.kth.infosys.smx.ladok3;

import javax.net.ssl.SSLSocketFactory;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

/**
 * Represents a ladok3 endpoint.
 */
@UriEndpoint(scheme = "ladok3", title = "ladok3", syntax="ladok3://host?cert=file&key=password", consumerClass = Ladok3Consumer.class, label = "ladok3")
public class Ladok3Endpoint extends DefaultEndpoint {
    @UriPath @Metadata(required = "true")
    private String host;
    @UriParam(name = "cert", defaultValue = "Path to file containing certificate in PKCS12 format") @Metadata(required = "true")
    private String cert;
    @UriParam(name = "key", defaultValue = "private key for certificate") @Metadata(required = "true")
    private String key;

    private SSLSocketFactory socketFactory;
    
    public Ladok3Endpoint(String uri, Ladok3Component component) throws Exception {
        super(uri, component);
    }
    
    public Producer createProducer() throws Exception {
        return new Ladok3Producer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new Ladok3Consumer(this, processor);
    }

    public boolean isSingleton() {
        return false;
    }

    /**
     * Path to certificate file.
     * @return the path
     */
    public String getCert() {
        return cert;
    }

    /**
     * Path to certificate file
     * @param cert the path to the certificate file.
     */
    public void setCert(String cert) {
        this.cert = cert;
    }

    /**
     * Private key for the certificate file.
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Private key for the certificate file.
     * @param key the key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * The Ladok3 host environment, mit-ik.ladok.se etc.
     * @return the host name
     */
    public String getHost() {
        return host;
    }

    /**
     * The ladok3 host environmnet, mit-ik.ladok.se etc.
     * @param host the fully qualified host name.
     */
    public void setHost(String host) {
        this.host = host;
    }

	public SSLSocketFactory getSocketFactory() {
		return socketFactory;
	}

	public void setSocketFactory(SSLSocketFactory socketFactory) {
		this.socketFactory = socketFactory;
	}
}
