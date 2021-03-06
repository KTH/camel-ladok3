# camel-ladok3-component

Camel Endpoint Component to Connect to Ladok3 packaged in a way suitable for
use in a Karaf feature for e.g. Apache ServiceMix.  The feature consists of
this component and the ladok3-model and ladok3-rest supporting libraries.

The consumer part of the component consumes the Atom feed with events from Ladok3.
It is pretty much feature complete.

The producer part invokes calls to the REST API. The main purpose would be to 
enrich events recieved with the consumer with additional data from the Ladok3 
REST API, but could also be used for other request/response purposes. 
It is, like the ladok3-rest library it is built on, so far more a proof of
concept than really useful so far, implementing just a few API calls.

## Configuration of the component

The component now follows more common patterns for configuration in camel where 
the basic configuration has to be made on the component bean. An example camel-spring or
osgi in xml:

```
  <bean id="ladok3" class="se.kth.infosys.smx.ladok3.Ladok3Component">
    <property name="cert" value="${ladok3.cert.file}"/>
    <property name="key" value="${ladok3.cert.key}"/>
    <property name="host" value="${ladok3.host}"/>
  </bean>
```

Where the options are:

| Property | Description |
|----------|-------------|
| cert     | Path to a file containing a certificate in PKCS12 format |
| key      | Password for the certificate file |
| host     | The ladok3 host environment, e.g., mit-integration.ladok.se. |

## The consumer

The consumer uses a URI of the form: `ladok3://?[lastEntry=x][&lastFeed=y][&events=u,v,x]`

It is based on the Camel polling consumer and supports its standard configuration
options, see http://camel.apache.org/polling-consumer.html

### Parameters

| Parameter | Description |
|-----------|-------------|
| lastEntry | The last event ID, an opaque string recieved from Ladok3 |
| lastFeed  | The last feed ID, an opaque string recieved from Ladok3 |
| includeEvents    | A comma separated list of event types in the feed to generate messages for |
| excludeEvents    | A comma separated list of event types in the feed NOT to generate messages for |

Note, exclude is stronger than include. If an event is both included and excluded it will be excluded.

Assuming a property place holder in Karaf an example of a configuration could be:

```
  <cm:property-placeholder persistent-id="se.kth.infosys.smx.ladok3" update-strategy="reload" />

  <camelContext xmlns="http://camel.apache.org/schema/blueprint">
    <route id="read-atom-feed">
      <from uri="ladok3://?lastEntry={{last_id}}&amp;lastFeed={{last_feed}}" />
      <marshal>
        <jacksonxml />
      </marshal>
      <to uri="log:se.kth.infosys.smx.ladok3?level=INFO" />
    </route>
  </camelContext>
```

A simple manner to keep track of your position in the stream between runs is to load these 
from a properties file in the camel context which is updated based on the headers in the exchanges.
This file would have to be created by some other means before the first run.

Note that this file would take quite a bit of I/O if you are concerned with performance.

```
  <camelContext xmlns="http://camel.apache.org/schema/blueprint">
    <propertyPlaceholder id="ladok_version" location="file:/tmp/ladok3.version" />

    <route id="ladok3-atom-feed-to-service-bus-route">
      <from uri="ladok3://?lastEntry={{last_id}}&amp;lastFeed={{last_feed}}" />
      ...
      <setBody>
        <simple>
last_feed=${in.header.ladok3AtomFeed}
last_id=${in.header.ladok3AtomEntryId}
        </simple>
      </setBody>
      <to uri="file:/tmp?fileName=ladok3.version" />
    </route>
  </camelContext>
```

### Message headers used by the Consumer

| Header | Type | Description |
|--------|------|-------------|
| ladok3MessageSequenceNumber | long | The sequence number of the message in the stream. Can be used for down stream re-sequencing. |
| ladok3MessageType | String | Either of ladok3FeedStart, ladok3FeedDone or ladok3Event |
| ladok3AtomFeed | String | The URL of the Ladok3 feed where the event is found |
| ladok3AtomEntryId | String | The Atom entry ID of the Atom feed entry the event is found in |
| ladok3IsLastFeed | Boolean | Whether the feed is the currently last feed of all feeds |
| ladok3EventType | String | Class name of the event, only for type ladok3Event |
| ladok3EventId | String | The Ladok3 UID of the event, only for type ladok3Event |

Typically messages are sent in batches for each feed read from Ladok3.

1. Message with header `ladok3MessageType: ladok3FeedStart` and null body.
1. A number of messages with header `ladok3MessageType: ladok3Event` and body of type se.ladok.schemas.
1. Message with header `ladok3MessageType: ladok3FeedDone` and null body.

All messages gets a sequence number, including control messages, in order to support
down stream [re-sequencing](http://camel.apache.org/resequencer.html), e.g.

```
<resequence>
  <stream-config capacity="10000" timeout="10000" />
  <header>ladok3MessageSequenceNumber</header>
  <to uri="mock:result" />
</resequence>
```

## The producer

Unlike the consumer, the producer is not feature complete. The goal is to provide enough relevant functionality
for message enrichment of the Atom feed data and data retrieval necessary for our integration purposes on 
a requirement driven basis, and not to cover all of the Ladok3 REST API.

The producer uses a URI of the form: `ladok3:[/<service>][/<operation>]`

The `service` parameter corresponds to the service URL for the corresponding Ladok3 service. Currently "student" is
the only service implemented, and even then only a few calls of it.

The `service` and `operation` parameters can be left out and replaced with message headers `ladok3Service` and
`ladok3Operation` which are mandatory unless the information is provided in the URL path. Information provided
in the URL path has precedence over message headers.

The body of the resulting message is generally the information returned by Ladok3, in the form of an POJO object
of the `se.ladok.schemas` object tree.

### Use case

A *very* fictional use case, but you get the idea:

```
  <camelContext>
    <route>
      <!-- Reading from Atom feed -->
      <from uri="ladok3:" />
      <!-- Filter out student events. -->
      <filter>
        <simple>${in.body.class} == "StudentEvent"</simple>
        <!-- Prepare for getting student information. -->
        <setHeader headerName="ladok3Key">
          <simple>${in.body.studentUID}</simple>
        </setHeader>
        <!-- Retrieve student information -->
        <enrich strategyRef="aggregationStrategy">
          <constant>ladok3:/student</constant>
        </enrich>
        <!-- Retrieve student kontaktinformation -->
        <enrich strategyRef="aggregationStrategy">
          <constant>ladok3:/student/kontaktinformation</constant>
        </enrich>
        <to uri="log:ladok3-log" />
      </filter>
    </route>
  </camelContext>
```

### /student/filtrera

One of the methods implemented is the possibility to list students in Ladok3. A Map<String, Object> params can
be provided for filtering. No filter will retrieve all students in chunks of 400.

The result is an Iterable<StudentISokresultat> which takes care of any pagination of results behind the scenes.
Hence, it works well with streaming mode in the Camel splitter and similar situations and results are fetched
when required.

The ladok3-rest api also supports returning an Iterable<Student> which additionally will fetch the Student object
corresponding to a search result, but it is unclear if this is really required in the Camel producer (it can be
done by other means).

### Expanding the producer

Add basic JAX-RS calls to [../ladok3-rest](../ladok3-rest) if necessary, see `Ladok3StudentInformationService`.
Add calls referencing ladok3-rest to
`se.kth.infosys.smx.ladok3.internal.Ladok3StudentInformationServiceWrapper`.

If adding a new Ladok3 service not previously used, create a new wrapper similar to the
`Ladok3StudentInformationServiceWrapper` and add a registration of it to the
`Ladok3Producer` constructor.

### Writing integration tests for routes which uses this component

The camel-ladok3-component cannot easily be mocked, but must be replaced in the route using a fake-processor.
The end result is then mocked ussing Mockito. An example of code using this approach:

```java
package se.kth.integral.ths.fakeprocessor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Iterator;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import se.kth.infosys.ladok3.StudieaktivitetUtdataResultat;
import se.kth.infosys.ladok3.utdata.StudieaktivitetOchFinansiering;
import se.ladok.schemas.studiedeltagande.UtdataResultatrad;
import se.ladok.schemas.studiedeltagande.UtdataResultatrad.Metadata;
import se.ladok.schemas.studiedeltagande.UtdataResultatrad.Metadata.Entry;

public class FakeLadok3StudieaktivitetOchFinansieringProcessor implements Processor {

  @Override
  public void process(Exchange exchange) throws Exception {
  
    Metadata metadata = new Metadata();

    Entry entry = new Entry();
    entry.setKey(0);
    entry.setValue("01ed6247-e3f2-4daa-9450-8d73d735f491");

    metadata.getEntry().add(entry);

    UtdataResultatrad utdata = new UtdataResultatrad();
    utdata.setMetadata(metadata);
    utdata.getVarden().add(0, "19710321-1234");
    utdata.getVarden().add(1, "Jönsson, Fredrik");
    utdata.getVarden().add(2, "ELSYSETS");
    utdata.getVarden().add(3, "Elektro- och systemteknik");
    utdata.getVarden().add(4, "240,0 hp");
    utdata.getVarden().add(5, "EB");
    utdata.getVarden().add(6, "EES/skolkansli, forskarämnen");
    utdata.getVarden().add(7, "KONV-03309");
    utdata.getVarden().add(8, "2014-10-01 — ");
    utdata.getVarden().add(9, "2020-07-01 — 2020-12-31");
    utdata.getVarden().add(10, "80");
    utdata.getVarden().add(11, "100");
    utdata.getVarden().add(12, "ÖVR");
    utdata.getVarden().add(13, "Yrkesverksamhet utan anknytning till utbildning på forskarnivå, eller studiemedel, eller studiefinansiering saknas");

    StudieaktivitetOchFinansiering fakeStudieaktivitet = new StudieaktivitetOchFinansiering(utdata);

    /**
     * Mocks the invocation of the camel-ladok3-component and it's query to Ladok. 
     */
    Iterator<StudieaktivitetOchFinansiering> iterator = mock(
        StudieaktivitetUtdataResultat.StudieaktivitetOchFinansieringIterator.class);
    
    when(iterator.hasNext()).thenReturn(Boolean.TRUE);
    when(iterator.next()).thenReturn(fakeStudieaktivitet);
    
  
    exchange.getIn().setBody(iterator);
  }
}
```
