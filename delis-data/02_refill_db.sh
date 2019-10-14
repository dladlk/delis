#!/bin/bash -e
echo Rebuilding delis-data...
pushd ../delis-data
mvn install -DskipTests=true 
popd
pushd ../delis-web
echo Rebuilding delis-web...
mvn install -DskipTests=true 
echo Starting delis-web...
java -jar target/delis-web-1.0.0.jar \
	--application.database=delis2 \
	--spring.liquibase.enabled=false \
	--spring.jpa.hibernate.ddl-auto=update
popd
