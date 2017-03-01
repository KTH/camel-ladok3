package se.kth.infosys.ladok3;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ladok3RequestFilter implements ClientRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(Ladok3RequestFilter.class);

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Target: {}", requestContext.getUri().toString());
            LOG.trace("Method: {}", requestContext.getMethod());
            LOG.trace("Headers: {}", requestContext.getStringHeaders());
            if (requestContext.getEntity() != null) {
                LOG.trace(requestContext.getEntity().toString());
            }
        }
    }
}
