Installation
============

To compile stallone you need maven (>= 2.0) and a recent JDK (>= 1.6)
Invoking maven may be done with 'mvn', 'mvn2' or 'mvn3', depending on your system/installation.

To build a distribution of stallone (without running unit tests) you invoke maven like this:

    mvn -Dmaven.test.skip=true install

To build the API documentation (javadoc), run:

    mvn javadoc:javadoc

You will then find the API docs under

    target/site/apidocs/

Note: 
Some tests are currently failing (not yet impled), so they have to be skipped
in the maven invocation.
