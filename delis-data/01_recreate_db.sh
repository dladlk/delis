#!/bin/bash
echo Drops delis2 database and create an empty one

mysql -u root --password=test < recreate_delis2.sql
echo Press any key to finish...
read