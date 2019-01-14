# Skeleton for Java/Maven/JUnit project

A simple skeleton written in Java using Maven for the build and jUnit for tests.

* Java 8
* Unit tests with [JUnit 5](https://junit.org/junit5/)
* Integration tests with [JUnit 5](https://junit.org/junit5/)
* Code coverage reports via [JaCoCo](https://www.jacoco.org/jacoco/)
* A Maven build that puts it all together

## Running the tests

* To run the unit tests, call `mvn test`
* To run the integration tests as well, call `mvn verify`
* Code coverage reports are generated when `mvn verify` (or a full `mvn clean install`) is called.
  Point a browser at the output in `target/site/jacoco-both/index.html` to see the report.

## Tests & Integration Tests

* Write your test cases in `src/test/java/*Test.java` 
* Write your Integration Test cases in `src/test/java/*IT.java`

