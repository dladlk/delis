# Delis 

### ERST integration of eDelivery and eInvoicing 


* [Introduction](#introduction)
* [Build](#build)
* [Install](#install)
* [Run](#run)
		  
## Introduction

This is the code repository for Delis, the integration of eDelivery and eInvoicing for Danish public organisations, open source project of the ERST.

Project name is built as "e-DELI-very-S".

## Build

To build Delis for Tomcat:

    mvn clean install

## Install

### Prepare database

MySQL example:

```sql
drop schema if exists delis;
create schema delis;
alter database delis charset=utf8 collate=utf8_bin; 
create user delis@localhost identified by 'delis';
grant all on delis.* to delis@localhost;
```

## Run

## Test
There are two test classes. Separated by naming convention.
1. Unit tests ends with *Test - run every time on build (by surefire plugin)
2. Integration tests ends with *IT - run with goal 'mvg failsafe:integration-test' and 'mvn failsafe:verify'

## Test Coverage Check
1. Copy settings.xml file from project root to your ./m2 folder
2. mvn clean clover:setup test failsafe:integration-test clover:aggregate clover:clover
3. Open /delis/target/site/clover/index.html

## Liquibase
No DB update with spring.jpa.hibernate.ddl-auto from now. Update and versioning with Liquibase only.
1. If you commit requires changes of DB, you 'must' to add changelog script as well. 
See liquibase docs or ask co-worker how to do it.
Changelog example: \delis\delis-web\src\main\resources\db\changelog\changes\changelog-1.02-organisation_id-nullable.xml
-----------------------------------------------------------------------------------------------------
Hint:
To automatically create diff change log for two tables, original and changed, use command

liquibase.sh --driver=com.mysql.jdbc.Driver \
        --url=jdbc:mysql://localhost:3306/<delis_orig>?useSSL=false \
        --username=delis_name \
        --password=delis_pass \
    diffChangeLog \
        --referenceUrl=jdbc:mysql://localhost:3306/<delis_modified>?useSSL=false \
        --referenceUsername=delis_name \
        --referencePassword=delis_pass \
        > <changelog_filename.xml>
-----------------------------------------------------------------------------------------------------
2. There are 2 ways to create-update tables (precondition: you have empty delis scheme or partially updated with older changelogs):
 - run delis-web application - it creates-updates all automatically
 - run maven goal in delis-web folder 'mvn liquibase:update'
 Note: run maven goal in delis-data module  
 Note: if you got error "Validation Failed: 1 change sets check sum", run maven 'mvn liquibase:clearCheckSums' and try update again
 
 ## Identifier Validation Skip Values
 You can set env variable "identifier.check.step.skip" to skip Service or Action checks
 Values:
  Any letters combinations that contains (or not) "SERVICE" and-or "ACTION"
  
 #Delis :: Schematron -> XSLT Converter
  
 1. Set /delis/delis-sch2xslt-converter current dir
 2. Copy schematron file into /delis/delis-sch2xslt-converter/sch folder
 3. Run mvn clean install
 4. Look fo converted files in /delis/delis-sch2xslt-converter/xslt folder
 Note. Currently, /sch and /xslt contain example file "PEPPOLBIS-T111" - schematron and converted xslt.  