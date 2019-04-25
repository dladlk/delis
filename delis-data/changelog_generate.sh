#!/bin/bash -e
DELIS_ORGINAL=delis
DELIS_MODIFIED=delis2
RESULT_FILE_NAME=changelog.xml
LIQUIBASE_PATH=/d/work/liquibase/liquibase.sh

DB_USERNAME=delis
DB_PASSWORD=delis

${LIQUIBASE_PATH} --driver=com.mysql.jdbc.Driver \
        --url=jdbc:mysql://localhost:3306/${DELIS_ORGINAL}?useSSL=false \
        --username=${DB_USERNAME} \
        --password=${DB_PASSWORD} \
    diffChangeLog \
        --referenceUrl=jdbc:mysql://localhost:3306/${DELIS_MODIFIED}?useSSL=false \
        --referenceUsername=${DB_USERNAME} \
        --referencePassword=${DB_PASSWORD} \
        > ${RESULT_FILE_NAME}
