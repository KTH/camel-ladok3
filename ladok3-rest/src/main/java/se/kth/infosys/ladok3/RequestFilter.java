/*
 * MIT License
 *
 * Copyright (c) 2017 Kungliga Tekniska högskolan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package se.kth.infosys.ladok3;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a client request filter that is set up to trace calls in {@link AbstractLadok3Service}
 * using SLF4J logging. To trace calls in your application, setup SLF4J logging
 * appropriately and crank up the log level for se.kth.infosys.ladok3 to trace level.
 */
class RequestFilter implements ClientRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(RequestFilter.class);

    /**
     * {@inheritDoc}
     */
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
