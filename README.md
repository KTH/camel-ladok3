# camel-ladok3

Camel Endpoint Component to Connect to Ladok3 packages as an Apache ServiceMix (Karaf)
feature. The feature consists of the camel-ladok3 component and a support model based
on the Ladok3 WSDL to serialize data from Ladok3 to/from XML and JSON.

## Testing

You can build without tests using `mvn -Dmaven.test.skip=true package`. In order to run
tests you need to copy the file test.properties.sample to test.properties and edit it
to suit your environment and data. In particular you need a Ladok3 service certificate
with permissions enough in the system you are testing, the corresponding certificate key,
and edit the test data in test.properties which is used to match against requests to
your Ladok3 data.
