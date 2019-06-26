# DELIS 

### ERST integration of eDelivery and eInvoicing 


* [Introduction](#introduction)
* [Functionality](#functionality)
* [Build](#build)
* [Install](#install)
* [Run](#run)
		  
## <a id="introduction">Introduction</a>

This is the code repository for DELIS, the integration of eDelivery and eInvoicing for Danish public organisations, open source project of the ERST.

Project name is defined as "e-DELI-very-S".

The main goal of the project is to simplify implementation of [eDelivery](https://ec.europa.eu/cefdigital/wiki/display/CEFDIGITAL/eDelivery) and [eInvoicing](https://ec.europa.eu/cefdigital/wiki/display/CEFDIGITAL/eInvoicing) from [CEF building blocks](https://ec.europa.eu/cefdigital/wiki/display/CEFDIGITAL/Building+Blocks) by Danish public organisations.

## <a id="functionality">Functionality</a>

Key functionality of the project is registration of Danish organisations in OpenPEPPOL network and implementation of corner three in [4 corner model](https://ec.europa.eu/cefdigital/wiki/display/CEFDIGITAL/Message+exchange) (documnets receiving) and corner two for message responses (documents sending). To simplify integration with OpenPEPPOL, documents can be converted to OIOUBL format, already supported by all public Danish orgianisations.

This includes:

1. Support of multiple organisations on the same instance of the application, with own setup, user access and list of identifiers
1. Automatic synchronization of the list of identifiers of organisation
1. Automatic publishing of identifiers to SMP with configured list of profiles
1. Receiving documents by AS4 and AS2 protocols via CEF Domibus and DIFI Oxalis
1. On-the-fly validation during document receiving of receiver identifier and supported profiles by message details
1. Ingoing document format and receiver identification by document contents
1. Validation of ingoing format by XML schema and multiple schematron files
1. Transformation of ingoing format according to organisation configuration
1. Supported formats: 
  * Cross Industry Invoice (v. 16B), conversion to BIS3 Invoice/CreditNote
  * BIS3 Invoice/CreditNote, conversion to OIOUBL
  * OIOUBL Invoice/CreditNote
  * BIS3 Invoice Response
  * BIS3 Message Level Response
8. Automatic generation of BIS3 Message Level Response in case of validation error of ingoing or transformed format as a technical rejection
1. Sending documents by AS4 and AS4 protocols by dynamic lookup in OpenPEPPOL network
1. Web GUI for reviewing list of received and sent documents by corresponding organisation, processing details and downloading of technical receipts
1. Web GUI for manual generation of BIS3 Invoice Response as a business response on received document
1. Automatic forwarding of generated BIS3 Message Level Response to ERST Error Statistics module
1. Error Statistics module for analysis of found schema/schematron errors in processed documents and loaded from BIS3 Message Level Response files

## <a id="build">Build</a>

To build DELIS for Tomcat:

    mvn clean install

## <a id="install">Install</a>

### Prepare database

MySQL example:

```sql
drop schema if exists delis;
create schema delis;
alter database delis charset=utf8 collate=utf8_bin; 
create user delis@localhost identified by 'delis';
grant all on delis.* to delis@localhost;
```

## <a id="functionality">Run</a>

### <a id="functionality">Test</a>
There are two test classes. Separated by naming convention.
1. Unit tests ends with *Test - run every time on build (by surefire plugin)
2. Integration tests ends with *IT - run with goal 'mvg failsafe:integration-test' and 'mvn failsafe:verify'

### Test Coverage Check
1. Copy settings.xml file from project root to your ./m2 folder
2. mvn clean clover:setup test failsafe:integration-test clover:aggregate clover:clover
3. Open /delis/target/site/clover/index.html

### Liquibase
No DB update with spring.jpa.hibernate.ddl-auto from now. Update and versioning with Liquibase only.
1. If you commit requires changes of DB, you 'must' to add changelog script as well. 
See liquibase docs or ask co-worker how to do it.
Changelog example: \delis\delis-web\src\main\resources\db\changelog\changes\changelog-1.02-organisation_id-nullable.xml

Hint:
To automatically create diff change log for two tables, original and changed, use command

```
liquibase.sh --driver=com.mysql.jdbc.Driver \
        --url=jdbc:mysql://localhost:3306/<delis_orig>?useSSL=false \
        --username=delis_name \
        --password=delis_pass \
    diffChangeLog \
        --referenceUrl=jdbc:mysql://localhost:3306/<delis_modified>?useSSL=false \
        --referenceUsername=delis_name \
        --referencePassword=delis_pass \
        > <changelog_filename.xml>
```

2. There are 2 ways to create-update tables (precondition: you have empty delis scheme or partially updated with older changelogs):
 - run delis-web application - it creates-updates all automatically
 - run maven goal in delis-web folder 'mvn liquibase:update'
 Note: run maven goal in delis-data module  
 Note: if you got error "Validation Failed: 1 change sets check sum", run maven 'mvn liquibase:clearCheckSums' and try update again
 
 ### Identifier Validation Skip Values
 You can set env variable "identifier.check.step.skip" to skip Service or Action checks
 Values:
  Any letters combinations that contains (or not) "SERVICE" and-or "ACTION"
  
 ### DELIS :: Schematron -> XSLT Converter
  
 1. Set /delis/delis-sch2xslt-converter current dir
 2. Copy schematron file into /delis/delis-sch2xslt-converter/sch folder
 3. Run mvn clean install
 4. Look fo converted files in /delis/delis-sch2xslt-converter/xslt folder
 Note. Currently, /sch and /xslt contain example file "PEPPOLBIS-T111" - schematron and converted xslt.  