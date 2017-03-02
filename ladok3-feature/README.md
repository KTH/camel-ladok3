# ladok3-feature

A Karaf feature for easy install of the camel-ladok3-component and related dependencies.
It is currently unused and thus untested, but provided for completeness.

## Using

The feature is published to
[maven central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22se.kth.infosys.smx.ladok3%22%20AND%20a%3A%22ladok3-featuret%22). 

```
feature:repo-add mvn:se.kth.infosys.smx.ladok3/ladok3-feature/0.0.3/xml/features
feature:install [-v] ladok3-feature
```
