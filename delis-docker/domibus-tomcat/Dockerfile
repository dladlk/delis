FROM maven:3.6.0-jdk-8-alpine as plugin_compiler

COPY ./erst-domibus-fs-plugin ./fs-plugin
RUN mvn -f ./fs-plugin clean install
COPY ./domibus-patch-jms-timeout ./jms-timeout
RUN mvn -f ./jms-timeout clean install
COPY ./domibus-metrics-prometheus ./domibus-metrics-prometheus
RUN mvn -f ./domibus-metrics-prometheus clean install

FROM tomcat:8.5-jre8-slim

ARG DOMIBUS_VERSION
ENV DOMIBUS_VERSION ${DOMIBUS_VERSION}

ARG DOMIBUS_WAR_URL
ENV DOMIBUS_WAR_URL ${DOMIBUS_WAR_URL}

ARG DOMIBUS_SETUP_URL
ENV DOMIBUS_SETUP_URL ${DOMIBUS_SETUP_URL}

ARG DOMIBUS_FS_URL
ENV DOMIBUS_FS_URL ${DOMIBUS_FS_URL}

ARG DOMIBUS_WS_URL
ENV DOMIBUS_WS_URL ${DOMIBUS_WS_URL}

ENV MYSQL_CONNECTOR mysql-connector-java-5.1.40
ENV DOMIBUS_DIST /usr/local/domibusDist

ENV DOCKERIZE_VERSION v0.6.1

RUN rm -r $CATALINA_HOME/webapps/ROOT/* && \
	rm -r $CATALINA_HOME/webapps/docs && \
	rm -r $CATALINA_HOME/webapps/examples && \
	rm -r $CATALINA_HOME/webapps/host-manager && \
	rm -r $CATALINA_HOME/webapps/manager

RUN apt-get update && apt-get install -y \ 
	wget \
	unzip

RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

RUN wget https://dev.mysql.com/get/Downloads/Connector-J/$MYSQL_CONNECTOR.zip \
    && unzip -d $DOMIBUS_DIST $MYSQL_CONNECTOR.zip \
    && cp $DOMIBUS_DIST/$MYSQL_CONNECTOR/$MYSQL_CONNECTOR-bin.jar $CATALINA_HOME/lib \
    && rm -r $DOMIBUS_DIST/$MYSQL_CONNECTOR \
    && rm $MYSQL_CONNECTOR.zip
    
RUN wget ${DOMIBUS_WAR_URL} \
    && unzip -d $CATALINA_HOME/webapps ${DOMIBUS_WAR_URL##*/} \
    && unzip -d $CATALINA_HOME/webapps/ROOT $CATALINA_HOME/webapps/domibus-MSH-tomcat-${DOMIBUS_VERSION}.war \
    && rm ${DOMIBUS_WAR_URL##*/} \
    && rm $CATALINA_HOME/webapps/domibus-MSH-tomcat-${DOMIBUS_VERSION}.war

RUN wget ${DOMIBUS_SETUP_URL} \
    && unzip -d $DOMIBUS_DIST ${DOMIBUS_SETUP_URL##*/} \
    && mkdir $CATALINA_HOME/domibus \
    && mkdir $CATALINA_HOME/domibus/policies \
    && mkdir $CATALINA_HOME/domibus/internal \
    && cp $DOMIBUS_DIST/internal/*.xml $CATALINA_HOME/domibus/internal \
    && cp $DOMIBUS_DIST/policies/*.xml $CATALINA_HOME/domibus/policies \
    && cp $DOMIBUS_DIST/logback.xml $CATALINA_HOME/domibus/logback.xml \
    && cp $DOMIBUS_DIST/domibus.properties $CATALINA_HOME/domibus/domibus.properties \
    && rm -r $DOMIBUS_DIST \
    && rm ${DOMIBUS_SETUP_URL##*/}
    
RUN wget ${DOMIBUS_FS_URL} \
    && unzip -d $DOMIBUS_DIST ${DOMIBUS_FS_URL##*/} \
    && mkdir $CATALINA_HOME/domibus/plugins \
    && mkdir $CATALINA_HOME/domibus/plugins/config \
    && mkdir $CATALINA_HOME/domibus/plugins/lib \
    && cp $DOMIBUS_DIST/conf/domibus/plugins/config/tomcat/* $CATALINA_HOME/domibus/plugins/config \
	&& cp $DOMIBUS_DIST/conf/domibus/plugins/lib/* $CATALINA_HOME/domibus/plugins/lib \
    && rm -r $DOMIBUS_DIST \
    && rm ${DOMIBUS_FS_URL##*/}
    
RUN wget ${DOMIBUS_WS_URL} \
    && unzip -d $DOMIBUS_DIST ${DOMIBUS_WS_URL##*/} \
    && cp $DOMIBUS_DIST/conf/domibus/plugins/config/tomcat/* $CATALINA_HOME/domibus/plugins/config \
	&& cp $DOMIBUS_DIST/conf/domibus/plugins/lib/* $CATALINA_HOME/domibus/plugins/lib \
    && rm -r $DOMIBUS_DIST \
    && rm ${DOMIBUS_WS_URL##*/}

COPY --from=plugin_compiler fs-plugin/src/main/resources/delis-domibus-fs-plugin-ext.xml  $CATALINA_HOME/domibus/plugins/config/delis-domibus-fs-plugin-ext.xml
COPY --from=plugin_compiler fs-plugin/target/delis-domibus-fs-plugin-ext-4.1.3.jar $CATALINA_HOME/domibus/plugins/lib/delis-domibus-fs-plugin-ext-4.1.3.jar

# Chandes to reduce timeout of jms queues checking
COPY --from=plugin_compiler jms-timeout/target/receive-timeout-1.0.jar $CATALINA_HOME/domibus/plugins/lib/domibus-default-delis-receive-timeout-1.0.jar
COPY custom-plugin.xml $CATALINA_HOME/domibus/plugins/config/domibus-default-delis-receive-timeout-plugin.xml

# Copy domibus-metrics-prometheus to WEB-INF/lib folder of Domibus to expose Prometheus metrics
COPY --from=plugin_compiler domibus-metrics-prometheus/target/domibus-metrics-prometheus-4.1.2.jar $CATALINA_HOME/webapps/ROOT/WEB-INF/lib/domibus-metrics-prometheus-4.1.2.jar

RUN echo '#!/bin/bash' > $CATALINA_HOME/bin/setenv.sh && \
	echo 'export JAVA_OPTS="$JAVA_OPTS -Ddomibus.config.location=$CATALINA_HOME/domibus"' >> $CATALINA_HOME/bin/setenv.sh

ARG EXT_MERGE_ENV_PROPERTIES
ENV EXT_MERGE_ENV_PROPERTIES ${EXT_MERGE_ENV_PROPERTIES}

COPY ${EXT_MERGE_ENV_PROPERTIES} /app/util/${EXT_MERGE_ENV_PROPERTIES}

ENV DOMIBUS_TMPL $CATALINA_HOME/domibus/domibus.properties.tmpl
RUN cp $CATALINA_HOME/domibus/domibus.properties $DOMIBUS_TMPL

RUN sed -i 's/domibus.security.key.private.alias=blue_gw/domibus.security.key.private.alias={{ .Env.KEY_ALIAS }}/g' $DOMIBUS_TMPL && \
	sed -i 's/domibus.security.key.private.password=test123/domibus.security.key.private.password={{ .Env.KEY_PASSWORD }}/g' $DOMIBUS_TMPL && \
	sed -i 's/domibus.database.serverName=localhost/domibus.database.serverName={{ .Env.DATABASE_HOST }}/g' $DOMIBUS_TMPL && \
	sed -i 's/domibus.database.port=localhost/domibus.database.port={{ .Env.DATABASE_PORT }}/g' $DOMIBUS_TMPL && \
	sed -i 's/domibus.datasource.user=edelivery/domibus.datasource.user={{ .Env.DATABASE_USERNAME }}/g' $DOMIBUS_TMPL && \
	sed -i 's/domibus.datasource.password=edelivery/domibus.datasource.password={{ .Env.DATABASE_PASSWORD }}/g' $DOMIBUS_TMPL && \
	sed -i 's/domibus.datasource.xa.property.user=edelivery/domibus.datasource.xa.property.user={{ .Env.DATABASE_USERNAME }}/g' $DOMIBUS_TMPL && \
	sed -i 's/domibus.datasource.xa.property.password=edelivery/domibus.datasource.xa.property.password={{ .Env.DATABASE_PASSWORD }}/g' $DOMIBUS_TMPL && \
	sed -i 's/activeMQ.broker.host=localhost/activeMQ.broker.host={{ .Env.ACTIVEMQ_HOST }}/g' $DOMIBUS_TMPL && \
	sed -i 's/activeMQ.brokerName=localhost/activeMQ.brokerName={{ default .Env.ACTIVEMQ_BROKERNAME "localhost" }}/g' $DOMIBUS_TMPL && \
	sed -i 's/activeMQ.embedded.configurationFile=/{{ if .Env.ACTIVEMQ_BROKERNAME }}#{{end}}activeMQ.embedded.configurationFile=/g' $DOMIBUS_TMPL && \
	sed -i 's/activeMQ.username=domibus/activeMQ.username={{ .Env.ACTIVEMQ_USERNAME }}/g' $DOMIBUS_TMPL && \
	sed -i 's/activeMQ.connectorPort=1199/activeMQ.connectorPort={{ default .Env.ACTIVEMQ_CONNECTOR_PORT "1199"}}/g' $DOMIBUS_TMPL && \
	sed -i 's/activeMQ.rmiServerPort=1200/activeMQ.rmiServerPort={{ default .Env.ACTIVEMQ_RMISERVER_PORT "1200"}}/g' $DOMIBUS_TMPL && \
	sed -i 's/${activeMQ.broker.host}:61616/${activeMQ.broker.host}:61616?jms.watchTopicAdvisories=false/g' $DOMIBUS_TMPL && \
	sed -i 's/61616/{{ default .Env.ACTIVEMQ_TRANSPORT_PORT "61616"}}/g' $DOMIBUS_TMPL && \
	sed -i 's/\/gateway_/\//g' $DOMIBUS_TMPL && \
	sed -i 's/domibus.pmode.dao.implementation=CachingPModeProvider/domibus.pmode.dao.implementation={{ default .Env.PMODE_PROVIDER "CachingPModeProvider"}}/g' $DOMIBUS_TMPL && \
	sed -i 's/#domibus.smlzone=acc.edelivery.tech.ec.europa.eu/domibus.smlzone={{ default .Env.SML_ZONE "acc.edelivery.tech.ec.europa.eu" }}/g' $DOMIBUS_TMPL && \
	sed -i 's/domibus.msh.retry.cron=0\/5 \* \* \* \*/domibus.msh.retry.cron=0\/30 \* \* \* \*/g' $DOMIBUS_TMPL && \
	sed -i 's/#domibus.pull.retry.cron=0\/10 \* \* \* \*/domibus.pull.retry.cron=0\/30 \* \* \* \*/g' $DOMIBUS_TMPL && \
	sed -i 's/domibus.smlzone=acc.edelivery.tech.ec.europa.eu/domibus.smlzone={{ default .Env.SML_ZONE "acc.edelivery.tech.ec.europa.eu" }}/g' $DOMIBUS_TMPL && \
	sed -i 's/#domibus.dynamicdiscovery.useDynamicDiscovery=false/domibus.dynamicdiscovery.useDynamicDiscovery={{ default .Env.DYNAMIC_DISCOVERY "true" }}/g' $DOMIBUS_TMPL && \
	echo '\ndomibus.datasource.xa.property.useSSL={{ default .Env.DATABASE_USE_SSL "false" }}' >> $DOMIBUS_TMPL && \
	echo '\ndomibus.dispatcher.timeout=-1' >>  $DOMIBUS_TMPL && \
	echo 'Finished Domibus template preparation'
# [apal notes]
# when using properties merger, still have to use SED command, since some things can't be done with merger:
# 1. Comment property on condition (activeMQ.embedded.configurationFile)
# 2. Add/change properte with mixed case (with merger, domibus.datasource.xa.property.useSSL -> domibus.datasource.xa.property.usessl)
#RUN 	sed -i 's/activeMQ.embedded.configurationFile=/{{ if .Env.ACTIVEMQ_BROKERNAME }}#{{end}}activeMQ.embedded.configurationFile=/g' $DOMIBUS_TMPL && \
#	echo '\ndomibus.datasource.xa.property.useSSL={{ default .Env.DATABASE_USE_SSL "false" }}' >> $DOMIBUS_TMPL && \
#	echo 'Finished Domibus template preparation'

ENV ACTIVEMQ_XML $CATALINA_HOME/domibus/internal/activemq.xml

# Disable adivsory support in JMX
RUN sed -i 's/broker useJmx="true"/broker advisorySupport="false" useJmx="true"/g' $ACTIVEMQ_XML && \
	echo 'Finished activeMQ template preparation'

VOLUME ["$CATALINA_HOME/domibus/keystores"]

# Instead of placing Domibus webapp to webapps (long path), we place it to the root app directory 
# and create a link to it via Catalina/localhost xml
# Also it gives us possibility to name context dynamically

ENV DOMIBUS_ROOT /app/domibus-webapp

RUN mkdir --parents ${DOMIBUS_ROOT}

RUN mv $CATALINA_HOME/webapps/ROOT/* ${DOMIBUS_ROOT}

RUN mkdir --parents ${CATALINA_HOME}/conf/Catalina/localhost

RUN echo '<Context path="/{{ default .Env.CONTEXT_NAME "/" }}" docBase="'${DOMIBUS_ROOT}'" reloadable="false" crossContext="false"></Context>' \
	> ${CATALINA_HOME}/conf/Catalina/localhost/context.tmpl

ENV STARTUP /startup.sh

RUN echo '#!/bin/bash' > ${STARTUP} && \
 	echo 'if [[ "${CONTEXT_NAME:-ROOT}" != ROOT ]]; then' >> ${STARTUP} && \
 	echo '	echo Change context from ROOT to $CONTEXT_NAME' >> ${STARTUP} && \
 	echo '	mv ${CATALINA_HOME}/conf/Catalina/localhost/ROOT.xml ${CATALINA_HOME}/conf/Catalina/localhost/$CONTEXT_NAME.xml;' >> ${STARTUP} && \
 	echo 'else' >> ${STARTUP} && \
 	echo '	echo Keep context ROOT;' >> ${STARTUP} && \
 	echo 'fi' >> ${STARTUP} && \
	echo 'if [ -z "${PREPARED_DOMIBUS_PROPERTIES}" ]' >> ${STARTUP} && \
	echo 'then' >> ${STARTUP} && \
	echo '      echo "PREPARED_DOMIBUS_PROPERTIES is empty, check merged domibus.properties"' >> ${STARTUP} && \
	echo '      if [ -z "${MERGE_DOMIBUS_PROPERTIES_ENV_PREFIX}" ]' >> ${STARTUP} && \
	echo '      then' >> ${STARTUP} && \
	echo '          echo "MERGE_DOMIBUS_PROPERTIES_ENV_PREFIX is empty, try templated merging"' >> ${STARTUP} && \
	echo '          dockerize -template $DOMIBUS_TMPL:$CATALINA_HOME/domibus/domibus.properties' >> ${STARTUP} && \
	echo '      else' >> ${STARTUP} && \
	echo '          echo "MERGE_DOMIBUS_PROPERTIES_ENV_PREFIX is ${MERGE_DOMIBUS_PROPERTIES_ENV_PREFIX}, invoke properties merger utility"' >> ${STARTUP} && \
	echo '          java -jar /app/util/${EXT_MERGE_ENV_PROPERTIES} ${MERGE_DOMIBUS_PROPERTIES_ENV_PREFIX} /$CATALINA_HOME/domibus/domibus.properties' >> ${STARTUP} && \
	echo '          diff /$CATALINA_HOME/domibus/domibus.properties.bak /$CATALINA_HOME/domibus/domibus.properties' >> ${STARTUP} && \
	echo '      fi' >> ${STARTUP} && \
	echo 'else' >> ${STARTUP} && \
	echo '      echo "PREPARED_DOMIBUS_PROPERTIES is ${PREPARED_DOMIBUS_PROPERTIES}, use it instead of templated"' >> ${STARTUP} && \
	echo '      cp ${PREPARED_DOMIBUS_PROPERTIES} /$CATALINA_HOME/domibus/domibus.properties' >> ${STARTUP} && \
	echo 'fi' >> ${STARTUP} && \
	echo 'if [ -z "${MERGE_FS_PROPERTIES_ENV_PREFIX}" ]' >> ${STARTUP} && \
	echo 'then' >> ${STARTUP} && \
	echo '    echo "MERGE_FS_PROPERTIES_ENV_PREFIX is empty"' >> ${STARTUP} && \
	echo 'else' >> ${STARTUP} && \
	echo '    echo "MERGE_FS_PROPERTIES_ENV_PREFIX is ${MERGE_FS_PROPERTIES_ENV_PREFIX}, invoke properties merger utility"' >> ${STARTUP} && \
	echo '    java -jar /app/util/${EXT_MERGE_ENV_PROPERTIES} ${MERGE_FS_PROPERTIES_ENV_PREFIX} /$CATALINA_HOME/domibus/plugins/config/fs-plugin.properties' >> ${STARTUP} && \
	echo '    diff /$CATALINA_HOME/domibus/plugins/config/fs-plugin.properties.bak /$CATALINA_HOME/domibus/plugins/config/fs-plugin.properties' >> ${STARTUP} && \
	echo 'fi' >> ${STARTUP} && \
	echo 'if [ -z "${DOMIBUS_HOME_OVERWRITE}" ]' >> ${STARTUP} && \
	echo 'then' >> ${STARTUP} && \
	echo '      echo "DOMIBUS_HOME_OVERWRITE is empty, continue without overwriting"' >> ${STARTUP} && \
	echo 'else' >> ${STARTUP} && \
	echo '      echo "DOMIBUS_HOME_OVERWRITE is ${DOMIBUS_HOME_OVERWRITE}, copy recursively its contents to $CATALINA_HOME/domibus"' >> ${STARTUP} && \
	echo '      cp  --recursive --dereference ${DOMIBUS_HOME_OVERWRITE} $CATALINA_HOME/domibus' >> ${STARTUP} && \
	echo 'fi' >> ${STARTUP} && \
	echo 'cd ${CATALINA_HOME}/bin && ./catalina.sh run' >> ${STARTUP}

RUN cat ${STARTUP}	
RUN chmod +x ${STARTUP}

ARG DELIS_UID="987"
ARG DELIS_GID="987"

RUN addgroup --system --gid $DELIS_GID delis && \
    adduser --system -uid $DELIS_UID --ingroup delis delis && \
	chown delis ${CATALINA_HOME}/conf/Catalina/localhost -R && \
	chown delis /$CATALINA_HOME/domibus -R && \
	mkdir --parents /home/domibus/fs_plugin_data/MAIN && \
	chown delis /home/domibus -R

USER delis

CMD dockerize \
	-template ${CATALINA_HOME}/conf/Catalina/localhost/context.tmpl:${CATALINA_HOME}/conf/Catalina/localhost/ROOT.xml \
	-wait tcp://$DATABASE_HOST:$DATABASE_PORT -timeout 120s \
	${STARTUP}

