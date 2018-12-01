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

