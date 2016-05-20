Ticketing Service Demo
====================
Summary: The quickstarts demonstrate the use of a Spring Boot application framework that facilitates the discovery, temporary hold, and final reservation of seats within a high-demand performance venue.

Overview
------------


The demo uses the Spring Boot framework, utilizing its ability to quickly configure and build boilerplate code. The ticketing API is exposed as a REST service exposed under the /tickets noun. The available URI's are



* [http://localhost:8080/tickets/available](http://localhost:8080/tickets/available): Get the list of avaialable tickets, optionally by Venue level using the **venueLevel** query parameter.

* [http://localhost:8080/tickets/reserve/{numSeats}](http://localhost:8080/tickets/reserve/{numSeats}): Post the number of seats required, identified by the **customerEmail** query parameter. Can optionally request by **minLevel** and **maxLevel** parameters. The method attempts to get the maximum number of tickets possible filtered via the query parameters and hold id. The hold is held for configurable number of seconds (default 100).

* [http://localhost:8080/tickets/confirm/{seatHoldId}](http://localhost:8080/tickets/confirm/{seatHoldId}): Post the request for confirmation of the hold.

* [http://localhost:8080/tickets/view/{confirmationId}](http://localhost:8080/tickets/view/{confirmationId}): View the tickets confirmed via the confirmation code.

The app uses Hazelcast as a cache to perform in memory ticket holds and ticket allocation. It stores confirmed tickets in an in-memory DB (HSQL) which can be configured for disk storage. The intial dataset is read as an SQL of available venue levels and seats available per level into the DB, and then loaded into cache as individual seats.



Maven execution
---------------


1. To run the application from command, make sure you have maven in your classpath, then launc from the app base directory using:

			 mvn clean package exec:java

2. To run just the tests for the app :

			 mvn clean test
			 


Configuration
---------------

To change the number of seconds tickets can be put on hold, change ticket.holdTime in the application properties in the resources folder.

To change the seed data for Venues, change the data.sql in the resources folders.