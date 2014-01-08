To compile stallone you need maven (<= 2.0) and a recent JDK.

To build a distribution of stallone you invoke maven like this:
mvn -PbuildDistribution -Dmaven.test.skip=true install

To build the pythonic wrapper (named 'pystallone') you invoke:
mvn -Ppython-wrapper -Dmaven.test.skip=true install

Note: 
Some tests are currently failing (not yet impled), so they have to be skipped
in the maven invocation.
