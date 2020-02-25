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
import org.apache.camel.support.DefaultProducer;
import org.apache.camel.support.ExchangeHelper;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.infosys.smx.ladok3.Ladok3Message;
import se.kth.infosys.smx.ladok3.internal.Ladok3KataloginformationServiceWrapper;
import se.kth.infosys.smx.ladok3.internal.Ladok3ServiceWrapper;
import se.kth.infosys.smx.ladok3.internal.Ladok3StudentInformationServiceWrapper;
import se.kth.infosys.smx.ladok3.internal.Ladok3StudiedeltagandeServiceWrapper;

/**
 * The ladok3 producer.
 */
public class Ladok3Producer extends DefaultProducer {
  @SuppressWarnings("unused")
  private static final Logger log = LoggerFactory.getLogger(Ladok3Producer.class);

  // First segment of URL (or ladok3Service header) should match the list 
  // of "supported" services in the HashMap apis.

  private static final Pattern API_PATTERN = Pattern.compile("(^/(?<api>[a-zA-Z]*))+.*");
  private static final HashMap<String, Ladok3ServiceWrapper> services = new HashMap<String, Ladok3ServiceWrapper>();

  public Ladok3Producer(Ladok3Endpoint endpoint) throws Exception {
    super(endpoint);
    String path = new URI(endpoint.getEndpointUri()).getPath();

    Matcher matcher = API_PATTERN.matcher(path);
    if (matcher.matches()) {
      endpoint.setApi(matcher.group("api").toLowerCase());
    }

    services.put("student",
        new Ladok3StudentInformationServiceWrapper(endpoint.getHost(), path, endpoint.getContext()));
    services.put("studiedeltagande",
        new Ladok3StudiedeltagandeServiceWrapper(endpoint.getHost(), path, endpoint.getContext()));
    services.put("kataloginformation",
        new Ladok3KataloginformationServiceWrapper(endpoint.getHost(), path, endpoint.getContext()));
  }

  public void process(Exchange exchange) throws Exception {
    String api = getEndpoint().getApi();
    if (api == null) {
      api = ExchangeHelper.getMandatoryHeader(exchange, Ladok3Message.Header.Service, String.class);
    }

    Ladok3ServiceWrapper service = services.get(api);
    if (service == null) {
      throw new UnsupportedOperationException("Ladok3 service: " + api + " not supported");
    }
    service.doExchange(exchange);
  }

  @Override
  public Ladok3Endpoint getEndpoint() {
    return (Ladok3Endpoint) super.getEndpoint();
  }
}
