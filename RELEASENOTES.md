# Release notes

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



