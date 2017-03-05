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
package se.kth.infosys.smx.ladok3;

import java.net.URI;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ExchangeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.infosys.ladok3.Ladok3StudentInformationService;
import se.kth.infosys.ladok3.LadokService;
import se.kth.infosys.smx.ladok3.internal.Ladok3Message;
import se.kth.infosys.smx.ladok3.internal.StudentServiceHelper;

/**
 * The ladok3 producer.
 */
public class Ladok3Producer extends DefaultProducer {
    private static final Logger log = LoggerFactory.getLogger(Ladok3Producer.class);

    private static final Pattern PRODUCER_PATTERN = Pattern.compile("(/(?<service>student))+(/(?<operation>personnummer|filtrera))+");

    public static final HashMap<String, LadokService> services = new HashMap<>();

    public Ladok3Producer(Ladok3Endpoint endpoint) throws Exception {
        super(endpoint);
        final URI uri = new URI(endpoint.getEndpointUri());

        Matcher matcher = PRODUCER_PATTERN.matcher(uri.getPath());
        if (matcher.matches()) {
            endpoint.setApi(matcher.group("service").toUpperCase());
            endpoint.setOperation(matcher.group("operation").toUpperCase());
        }

        services.put("student", new Ladok3StudentInformationService(
                uri.getHost(),
                endpoint.getContext()));
    }


    public void process(Exchange exchange) throws Exception {
        String api = getEndpoint().getApi();
        if (api == null) {
            api = ExchangeHelper.getMandatoryHeader(exchange, Ladok3Message.Header.Service, String.class);
        }

        switch (api) {
        case "STUDENT":
            handleStudentRequest(exchange);
            break;
        default:
            throw new UnsupportedOperationException("Ladok3 service: " + api + " not supported");
        }
    }


    private void handleStudentRequest(Exchange exchange) throws Exception {
        String operation = getEndpoint().getOperation();
        if (operation == null) {
            operation = ExchangeHelper.getMandatoryHeader(exchange, Ladok3Message.Header.Operation, String.class);
        }

        final Ladok3StudentInformationService service = (Ladok3StudentInformationService) services.get("student");

        switch (operation) {
        case "PERSONNUMMER":
            StudentServiceHelper.handleStudentPersonnummerRequest(exchange, service);
            break;
        default:
            StudentServiceHelper.handleStudentUidRequest(exchange, service);
        }
    }


    @Override
    public Ladok3Endpoint getEndpoint() {
        return (Ladok3Endpoint) super.getEndpoint();
    }
}
