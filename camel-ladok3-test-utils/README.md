# camel-ladok3-test-utils

Tools for testing of camel routes using the camel-ladok3-component.

## Ladok3 JSON Dataset

The `Ladok3JsonDataSet` generates a Ladok3 JSON formatted event stream
from a JSON encoded file containing list of objects. The file should have the format:

```
[
 {
   "headers": [],
   "body": {}
 }
]
```

Where headers are any headers to include in the message. The body will
be included as is, but is assumed to be a JSON encoded Ladok3 event.

E.g.

```
[
   {
      "headers" : {
         "ladok3AtomFeed" : "https://api.mit-integration.ladok.se:443/uppfoljning/feed/15016",
         "ladok3EventId" : "a8447c7c-cd8e-11e7-b145-3ce2c3f54be2",
         "ladok3EventType" : "se.ladok.schemas.studiedeltagande.StudentTillLarosateEvent"
      },
      "body" : {
         "Efternamn" : "Jönsson",
         "HandelseUID" : "a8447c7c-cd8e-11e7-b145-3ce2c3f54be2",
         "Tilltalsnamn" : "Fredrik",
         "Personnummer" : "197103211234",
         "StudentUID" : "8c5e0f2b-cd7d-11e7-93d2-de4abaed4dd5",
         "EventContext" : {
            "Anvandarnamn" : "informationskonverterare@kth",
            "LarosateID" : 29
         }
      }
   }
]
```

### Using with camel-spring

```
  <bean id="ladok3DataSet" class="se.kth.infosys.smx.ladok3.Ladok3JsonDataSet">
    <property name="sourceFile" value="classpath:ladok3-data.json"/>
  </bean>
...
    <route id="read-dataset">
      <from uri="dataset:ladok3DataSet"/>
...
```

### Using with camel-spring-boot

```
    @Bean(name = "ladok3DataSet")
    public DataSet ladok3DataSet() throws Exception {
        File ladok3Data = new File(this.getClass().getClassLoader().getResource("ladok3-data.json").toURI());
        return new Ladok3JsonDataSet(ladok3Data);
    }
```

## Ladok3 XML Dataset

Similar to above, but the body is assumed to be a string containing the XML representation of the event.

E.g.

```
[
   {
      "headers" : {
         "ladok3AtomFeed" : "https://api.mit-integration.ladok.se:443/uppfoljning/feed/15016",
         "ladok3EventId" : "a8447c7c-cd8e-11e7-b145-3ce2c3f54be2",
         "ladok3EventType" : "se.ladok.schemas.studiedeltagande.StudentTillLarosateEvent"
      },
      "body" : "<the xml representation/>"
   }
]
```
