.PHONY: run package test clean
run:
	mvn spring-boot:run
package:
	mvn clean package -DskipTests=true
	cp target/*.jar ~/thubp-docker/thubp/app.jar
test: 
	mvn test
clean:
	mvn clean
