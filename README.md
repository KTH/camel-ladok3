![Azure Artifact version](https://feeds.dev.azure.com/kth-integration/_apis/public/Packaging/Feeds/886f0719-97f5-4915-991f-619a1b556ad0/Packages/e5bb1b25-63c4-4256-bfc1-d5d7a503a93a/Badge)

# camel-ladok3

Components to use for integrating with Ladok3 from Java in general and with
Apache Camel in particular.

## The project

There are three parts in the project

1. [ladok3-rest](ladok3-rest) is a JAXRS based client library to talk to Ladok3 REST api services.
   The primary goal is to implement necessary methods for integration with the identity
   management solution at KTH and not a complete Ladok3 interface library.
   I.e., focus is on methods useful for identity, group and authorization
   management purposes. Other integrations concerning student records and such may
   be driven by other technology and possibly never warrant implementation in Java at KTH.
1. [camel-ladok3-component](camel-ladok3-component) is Camel Component defining a
   ladok3 endpoint to use in Camel routes. As a consumer it consumes events from Ladok3 
   atom-feeds (complete), and as a producer it uses the REST interface to query and 
   write to Ladok (proof of concept and incomplete).
1. [camel-ladok3-test-utils](camel-ladok3-test-utils) is a collection of tools to use 
   when testing camel routes using camel-ladok3-component. Currently only `DataSet`s 
   to emulate the `Ladok3Consumer`.
   
## Using

All components are published to a private Maven repository and therefore is not publicly available. 

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


### Update the model

A common task is to update the model dependencies due to changes in the Ladok3 api.
The model dependencies are in separate projects and automatically builds new versions whenever there's a 
new version of either Ladok API or Ladok Event-models. 

This component then automatically builds, tests and pushes a new version using a scheduled build pipeline. 


### Building

A complete local build of the image can be made with `mvn clean install`.

Builds can be pushed to Maven central with `mvn clean deploy` but that
requires additional setup with accounts to push to the private Maven artifact repository.

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
