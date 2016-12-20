package se.kth.infosys.ladok3;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ladok3ResponseFilter implements ClientResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(Ladok3ResponseFilter.class);

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        LOG.debug("Response status: {} {}", responseContext.getStatus(), responseContext.getStatusInfo().toString());
        LOG.debug("Response headers: {}", responseContext.getHeaders());
    }

}
