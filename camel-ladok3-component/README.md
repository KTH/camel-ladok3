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

## The consumer

The consumer uses a URI of the form: `ladok3://host.ladok.se?cert=path-to-cert&key=cert-passphrase[&lastEntry=x][&lastFeed=y][&events=u,v,x]`

### Parameters

| Parameter | Description |
|-----------|-------------|
| cert      | Path to a file containing a certificate in PKCS12 format |
| key       | Password for the certificate file |
| lastEntry | The last event ID, an opaque string recieved from Ladok3 |
| lastFeed  | The last feed ID, an opaque string recieved from Ladok3 |
| events    | A comma separated list of event types in the feed to generate messages for |

Assuming a property place holder in Karaf an example of a configuration could be:

```
  <cm:property-placeholder persistent-id="se.kth.infosys.smx.ladok3" update-strategy="reload" />

  <camelContext xmlns="http://camel.apache.org/schema/blueprint">
    <route id="read-atom-feed">
      <from uri="ladok3://{{host}}?cert={{cert}}&amp;key={{key}}&amp;lastEntry={{last_id}}&amp;lastFeed={{last_feed}}" />
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
      <from uri="ladok3://{{host}}?cert={{cert}}&amp;key={{key}}&amp;lastEntry={{last_id}}&amp;lastFeed={{last_feed}}" />
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
| ladok3EventType | String | Class name of the event |
| ladok3AtomFeed | String | The URL of the Ladok3 feed where the event is found |
| ladok3AtomEntryId | String | The Atom entry ID of the Atom feed entry the event is found in |
| ladok3IsLastFeed | Boolean | Whether the feed is the currently last feed of all feeds |

In addition the JMS message id is set to the string `ladok3-atom:<ladok3AtomEntryId>`
in order to be useful with deduplication techniques.

## The producer

Unlike the consumer, the producer is not feature complete and more of a proof of concept. The goal
is to provide enough relevant funtionality for message enrichment of the Atom feed data where
necesseary and not to cover all of the Ladok3 REST API.

The producer uses a URI of the form: `ladok3://host.ladok.se[/<service>][/<operation>]?cert=path-to-cert&key=cert-passphrase`

The `service` parameter corresponds to the service URL for the corresponding Ladok3 service. Currently "student" is
the only service implemented, and even then only a few calls of it.

The `service` and `operation` parameters can be left out and replaced with message headers `ladok3Service` and
`ladok3Operation` which are mandatory unless the information is provided in the URL path. Information provided
in the URL path has precedence over message headers.

The body of the resulting message is the information returned by Ladok3, in the form of an POJO object
of the `se.ladok.schemas` object tree.

### Use case

A *very* fictional use case, but you get the idea:

```
  <camelContext>
    <route>
      <!-- Reading from Atom feed -->
      <from uri="ladok3://{{ladok3.host}}?cert={{ladok3.cert.file}}&amp;key={{ladok3.cert.key}}" />
      <!-- Filter out student events. -->
      <filter>
        <simple>${in.body.class} == "StudentEvent"</simple>
        <!-- Prepare for getting student information. -->
        <setHeader headerName="ladok3Key">
          <simple>${in.body.studentUID}</simple>
        </setHeader>
        <!-- Retrieve student information -->
        <enrich strategyRef="aggregationStrategy">
          <constant>ladok3://{{ladok3.host}}/student?cert={{ladok3.cert.file}}&amp;key={{ladok3.cert.key}}</constant>
        </enrich>
        <!-- Retrieve student kontaktinformation -->
        <enrich strategyRef="aggregationStrategy">
          <constant>ladok3://{{ladok3.host}}/student/kontaktinformation?cert={{ladok3.cert.file}}&amp;key={{ladok3.cert.key}}</constant>
        </enrich>
        <to uri="log:ladok3-log" />
      </filter>
    </route>
  </camelContext>
```


### Expanding the producer

Add basic JAX-RS calls to [../ladok3-rest](../ladok3-rest) if necessary, see `Ladok3StudentInformationService`.
Add calls referencing ladok3-rest to
`se.kth.infosys.smx.ladok3.internal.Ladok3StudentInformationServiceWrapper`.

If adding a new Ladok3 service not previously used, create a new wrapper similar to the
`Ladok3StudentInformationServiceWrapper` and add a registration of it to the
`Ladok3Producer` constructor.