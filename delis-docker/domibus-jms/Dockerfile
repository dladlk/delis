FROM webcenter/activemq:5.14.3

ARG DEBIAN_FRONTEND=noninteractive

ARG EXT_MERGE_ENV_PROPERTIES
ENV EXT_MERGE_ENV_PROPERTIES ${EXT_MERGE_ENV_PROPERTIES}

ENV ACTIVEMQ_CONF_FOLDER /opt/activemq/conf

COPY ./activemq.xml /opt/activemq/conf
COPY ./domibus.properties /opt/activemq/conf
COPY ./jetty.xml /opt/activemq/conf

ENV DOMIBUS_PROPERTIES /opt/activemq/conf/domibus.properties

COPY ${EXT_MERGE_ENV_PROPERTIES} /app/util/${EXT_MERGE_ENV_PROPERTIES}

ENV STARTUP /startup.sh

RUN echo '#!/bin/bash' > ${STARTUP} && \
	echo 'if [ -z "${MERGE_DOMIBUS_PROPERTIES_ENV_PREFIX}" ]' >> ${STARTUP} && \
	echo 'then' >> ${STARTUP} && \
	echo '    echo "MERGE_DOMIBUS_PROPERTIES_ENV_PREFIX is empty, leave unchanged"' >> ${STARTUP} && \
	echo 'else' >> ${STARTUP} && \
	echo '    echo "MERGE_DOMIBUS_PROPERTIES_ENV_PREFIX is ${MERGE_DOMIBUS_PROPERTIES_ENV_PREFIX}, invoke properties merger utility"' >> ${STARTUP} && \
	echo '    java -jar /app/util/${EXT_MERGE_ENV_PROPERTIES} ${MERGE_DOMIBUS_PROPERTIES_ENV_PREFIX} ${DOMIBUS_PROPERTIES}' >> ${STARTUP} && \
	echo '    diff ${DOMIBUS_PROPERTIES}.bak ${DOMIBUS_PROPERTIES}' >> ${STARTUP} && \
	echo 'fi' >> ${STARTUP} && \
	echo 'cd ${APP_HOME} && /app/run.sh' >> ${STARTUP}



#RUN sed -i 's/serviceRun.setting_all()/serviceRun.do_init_activemq()\r\n    serviceRun.do_remove_default_account()/g' /app/entrypoint/Init.py
#RUN cat /app/entrypoint/Init.py | grep "serviceRun"

RUN cat ${STARTUP}	
RUN chmod +x ${STARTUP}

CMD ["/startup.sh"]

EXPOSE 1199
EXPOSE 1200
