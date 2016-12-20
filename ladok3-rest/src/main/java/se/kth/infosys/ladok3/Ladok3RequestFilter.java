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
        LOG.debug("Target: {}", requestContext.getUri().toString());
        LOG.debug("Method: {}", requestContext.getMethod());
        LOG.debug("Headers: {}", requestContext.getStringHeaders());
        if (requestContext.getEntity() != null) {
            LOG.debug(requestContext.getEntity().toString());
        }
    }
}
