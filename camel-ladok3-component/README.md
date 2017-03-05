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

## The Consumer

The consumer uses a URI of the form: `ladok3://host.ladok.se?cert=path-to-cert&key=cert-passphrase[&lastEntry=x][&lastFeed=y][&events=u,v,x]`

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

## The producer

The consumer uses a URI of the form: `ladok3://host.ladok.se?cert=path-to-cert&key=cert-passphrase`

