# Delis-data

## Domain model and db entities

### Root folder contains scripts for changelog generation:

- *01_recreate_db.sh* - drops/creates empty db delis2
- *02_refill_db.sh* - rebuilds delis-data and delis-web and starts delis-web against delis2 database, with disabled liquibase but enabled auto-DDL generation via Spring
- *03_changelog_generate.sh* - via liquibase generates diff changelog between old delis and new empty delis2
- *99_view_diff.sh* - can be used to preview differences