# Release notes

## 1.27.0

* Update to ladok3 schema version 1.27.0.

## 1.24.1

* Update to ladok3 1.24.1.

## 1.16.1

* Update to ladok3 1.16.1.

## 1.14.1

* Update to ladok3 1.14.1.

## 1.1.4

* Update to ladok3 1.10.0

## 1.1.2

* Update to ladok3 1.7.0.

## 1.1.1

* Fix dependency issue.

## 1.1.0

* New endpoints implemented for ladok-rest and camel producer.
  /tillfallesdeltagande/kurstillfallesdeltagande/student/
  /studieresultat/utdata/studieaktivitetochfinansiering

## 1.0.1

* Fix: Camel component cannot be singleton.

## 1.0.0

Let's go final initial.

* Update to ladok3 version 1.6.2.

## 0.9.0

* Update to ladok3 version 1.5.1.

## 0.8.0

* Update to ladok3 version 1.4.1.

## 0.7.0

* Add header for ladok3 username to events. PR #4
  https://github.com/KTH/camel-ladok3/pull/4

## 0.6.0

* Update to ladok3 version 1.3.1.

## 0.5.2

* Revert cxf version due to incompatibility with spring version.

## 0.5.1

* Use jar instead of bundle for cxf dependency.

## 0.5.0

* Update to ladok3 version 1.1.0.
* Update to cxf dependency.

## 0.4.0

* Update to ladok3 version 1.1.0.
* Update to camel 2.19.5.

NOTE: untested due to mit-integration environment issues.

## 0.3.1

* Add dependency check (CVE security).
* Update dependencies.

## 0.3.0

* Add utility method to parse localized dates as produced
  by StockholmLocalDateTimeFormatter.

## 0.2.0

* Update to ladok3 version 0.99.3

## 0.1.0

* Change how configuration is done and make singleton.

## 0.0.39

* Update XSD to Ladok3 0.98.2-20180216_094730

## 0.0.38

* Minor fix.

## 0.0.37

* Added methods to support integral-ths-integration.

## 0.0.36

* Added camel-ladok3-test-utils with a dataset for producing
  ladok3 json formatted events for testing.

## 0.0.35

* Update to ladok3 model 0.97.2-20180203_153346.
* Update camel.

## 0.0.33

* Add atom index to headers.
* Update to ladok3 model 0.92.2-20171107_190726.

## 0.0.31

* Request filters have to be public.

## 0.0.30

* Refactorization.
  Only expose classes that should be exposed.
  Adjust naming to common conventions.

## 0.0.29

* Interface change in order to support streaming mode in camel properly.

## 0.0.28

* Add support for call to student/{uid}/historik needed when importing uids to UG.
* Update to model from ladok 0.88.1. MIT integration will be upgraded to 0.86 as far
  as I know, not quite sure if there will be issues. Changes made in order to handle
  the new model should make it easy to swap to 0.86 though.

## 0.0.26

* Add support for filtrera to list students in component.

## 0.0.25

* Change name of option "events" to "includeEvents".
* Add support for "excludeEvents" option.

## 0.0.24

* Format date in header as in Europe/Stockholm TZ.

## 0.0.23

* This is just a test release to try new release cycle.
  No functional changes.

## 0.0.21

* Add date header.

## 0.0.20

* Use ISO 8601 like dates, code stolen from ATI/Uppsala.

## 0.0.19

* Update camel to 2.19.1.
* Update Ladok3 model to version 0.82.0.

## 0.0.18 - broken release

## 0.0.17

Remove in-function uses of final keyword with unclear effects.
Instead use final keyword consistently in function signatures.

## 0.0.16

Close input stream properly.

## 0.0.15

Set connect and read timeouts on URLConnection.

## 0.0.14

Make options to Camel ScheduledPollConsumer work.

## 0.0.13

Modify model according to discussion in Google groups, remove
nillable=true from simple types at least.

## 0.0.12

Refactorization of API.
Start using interface classes to support mockups down stream.
Extract more common bits to base class.
Basic necessary bits of studentinformation and kataloginformation
implemented.

## 0.0.11

Support down stream re-sequencing with new header.
Support down stream de-duplication.

## 0.0.10

Don't send control messages when there are no event messages sent.

## 0.0.9

Use a ScheduledPollEndpoint with the ScheduledPollConsumer to get
the options.

## 0.0.8

Add entryId to control message and fix bug.

## 0.0.7

Add control messages at start and end of feed to use down the line.

## 0.0.6

Add default cookie manager to camel component.

## 0.0.5

Add ladok3IsLastFeed header.
Set messageID to well-known ladok3-atom:<entry id>. This can be
used for deduplication purposes.

## 0.0.4

Basic functionality of producer.
Parking development here for a while.

## 0.0.3

Most necessary functionality of consumer in place.



