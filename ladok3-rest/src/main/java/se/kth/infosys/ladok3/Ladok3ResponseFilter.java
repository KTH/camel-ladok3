package se.kth.infosys.ladok3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ladok3ResponseFilter implements ClientResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(Ladok3ResponseFilter.class);

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Response status: {} {}", responseContext.getStatus(), responseContext.getStatusInfo().toString());
            LOG.trace("Response headers: {}", responseContext.getHeaders());

            InputStream stream = responseContext.getEntityStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = stream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            stream.close();
            LOG.trace("Response body: {}", result.toString("UTF-8"));

            responseContext.setEntityStream(new ByteArrayInputStream(result.toByteArray()));
        }
    }

}
