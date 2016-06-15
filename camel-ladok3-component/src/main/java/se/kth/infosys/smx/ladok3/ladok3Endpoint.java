package se.kth.infosys.smx.ladok3;

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
@UriEndpoint(scheme = "ladok3", title = "ladok3", syntax="ladok3:name", consumerClass = ladok3Consumer.class, label = "ladok3")
public class ladok3Endpoint extends DefaultEndpoint {
    @UriPath @Metadata(required = "true")
    private String name;
    @UriParam(defaultValue = "10")
    private int option = 10;

    public ladok3Endpoint() {
    }

    public ladok3Endpoint(String uri, ladok3Component component) {
        super(uri, component);
    }

    public ladok3Endpoint(String endpointUri) {
        super(endpointUri);
    }

    public Producer createProducer() throws Exception {
        return new ladok3Producer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new ladok3Consumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }

    /**
     * Some description of this option, and what it does
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Some description of this option, and what it does
     */
    public void setOption(int option) {
        this.option = option;
    }

    public int getOption() {
        return option;
    }
}
