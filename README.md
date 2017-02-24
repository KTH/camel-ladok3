# camel-ladok3

Camel Endpoint Component to Connect to Ladok3 packages as an Apache ServiceMix (Karaf)
feature. The feature consists of the camel-ladok3 component and a support model based
on the Ladok3 WSDL to serialize data from Ladok3 to/from XML and JSON.

## The Consumer

The consumer uses a URI of the form: `ladok3://host.ladok.se?cert=path-to-cert&key=cert-passphrase[&lastEntry=x][&lastFeed=y]`

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

# The project

There are three parts in the project

1. ladok3-rest is a library to talk to Ladok3 REST api services. It includes the 
   data model derived from the XSD published by Ladok.
1. ladok3-events is a library with data model for events derived from the XSD
   published by Ladok. They are currently separated from the model used in
   ladok3-rest since they currently differ in some common files. It's unclear
   if that is intentional.
1. camel-ladok3-component is Camel Component defining a ladok3 endpoint to use in
   Camel routes.
1. ladok3-feature is a Karaf Feature packaging of the camel-ladok3-component.

## Testing

You can build without tests using `mvn -Dmaven.test.skip=true package`. In order to run
tests you need to copy the file test.properties.sample to test.properties and edit it
to suit your environment and data. In particular you need a Ladok3 service certificate
with permissions enough in the system you are testing, the corresponding certificate key,
and edit the test data in test.properties which is used to match against requests to
your Ladok3 data.
