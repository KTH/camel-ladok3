package se.kth.infosys.smx.ladok3.utils;

import java.util.ArrayList;
import org.apache.camel.Exchange;
import org.apache.camel.AggregationStrategy;

/**
 * A very simple aggregation strategy which only combines body objects
 * into a list.
 */
public class Ladok3ListAggregationStrategy implements AggregationStrategy {
  /**
   * {@inheritDoc}
   */
  public Exchange aggregate(final Exchange echange1, final Exchange exchange2) {
    ArrayList<Object> list = new ArrayList<Object>();

    for (Exchange e : new Exchange[]{echange1, exchange2}) {
      Object body = e.getIn().getBody();
      if (body != null) {
        list.add(body);
      }
    }
    exchange2.getIn().setBody(list);
    return exchange2;
  }
}
