package se.kth.infosys.smx.ladok3.internal;

import org.apache.camel.Exchange;

public interface Ladok3ServiceWrapper {
    public abstract void doExchange(Exchange exchange) throws Exception;
}
