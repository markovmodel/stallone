To compile stallone you need maven (>= 2.0) and a recent JDK (>= 1.6).

To build a distribution of stallone you invoke maven like this:
mvn -PbuildDistribution -Dmaven.test.skip=true package 

Note: 
Some tests are currently failing (not yet impled), so they have to be skipped
in the maven invocation.
