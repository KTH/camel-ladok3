# camel-ladok3

Components to use for integrating with Ladok3 from Java in general and with
Apache Camel in particular.

## The project

There are three parts in the project

1. [ladok3-rest](ladok3-rest) is a JAXRS based client library to talk to Ladok3 REST api services.
   It is currently just a proof of concept and incomplete.
1. [ladok3-model](ladok3-model) is a library with data model for events and other objects
   derived from the XSD published by Ladok.
1. [camel-ladok3-component](camel-ladok3-component) is Camel Component defining a
   ladok3 endpoint to use in Camel routes. As a consumer it consumes events from Ladok3 
   atom-feeds (complete), and as a producer it uses the REST interface to query and 
   write to Ladok (proof of concept and incomplete).
1. [ladok3-feature](ladok3-feature) is a Karaf Feature packaging of the camel-ladok3-component. It
   is currently not used by us and untested but included for completeness.
   
## Using

All components are published to Maven central with groupId
[se.kth.infosys.smx.ladok3](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22se.kth.infosys.smx.ladok3%22).
See the readme of each component for details.

### Authentication

Ladok3 uses certificates for authentication which are requested from the Ladok3 support.
The REST library and component assumes that these are in the form of a PKCS12 keystore
with certificate and key combined, protected by a password.

Given the current form recieved from Ladok3 and that extraction of the key and certficate
is done as instructed in the accompanying email resulting in files `certificate.key` and
`certificate.crt` ("certificate" should be replaced with your specifics), convertion
can be done as with openssl.

```
openssl pkcs12 -export \
  -out certificate.p12 \
  -inkey certificate.key \
  -in certificate.crt \
  -certfile Ladok3.LED.MIT.API.Chain.CA.pem
```

The password to use with the api and camel component is the one you enter for protecting
the exported keystore when running above command.


## Development

The project uses git-flow branch strategy, see
[introduction](http://nvie.com/posts/a-successful-git-branching-model/)
and the [git-flow tool](https://github.com/nvie/gitflow). Mainly all
development goes into development branch and via releases into master
which is built and pushed to docker hub continously by Jenkins.

Set the version in all components with `mvn versions:set` from project root.

### Building

A complete local build of the image can be made with `mvn clean install`.

Builds can be pushed to Maven central with `mvn clean deploy` but that
requires additional setup with accounts to push to central.

Pre-built binaries are available in Maven central. 

### Testing

You can build without tests using `mvn -Dmaven.test.skip=true package`. In order to run
tests you need to copy the file test.properties.sample to test.properties and edit it
to suit your environment and data. In particular you need a Ladok3 service certificate
with permissions enough in the system you are testing, the corresponding certificate key,
and edit the test data in test.properties which is used to match against requests to
your Ladok3 data.

### Release process with git flow

```
git flow release start x.y.z
mvn versions:set
 *enter x.y.z when prompted*
 *commit changes*

git flow release publish x.y.z
 *do whatever testing and update of RELEASENOTES.md*
 *commit changes*

git flow release finish x.y.z
```
