#!/bin/bash -e

NEW_VERSION=1.1.0
az acr login --name=tledelivery

docker tag delis-angular:latest					tledelivery.azurecr.io/delis-angular:${NEW_VERSION}
docker tag delis-web-admin:latest				tledelivery.azurecr.io/delis-web-admin:${NEW_VERSION}
docker tag delis-web-api:latest					tledelivery.azurecr.io/delis-web-api:${NEW_VERSION}
docker tag delis-sender-service:latest			tledelivery.azurecr.io/delis-sender-service:${NEW_VERSION}
docker tag delis-domibus-ws-sender:latest		tledelivery.azurecr.io/delis-domibus-ws-sender:${NEW_VERSION}

docker push tledelivery.azurecr.io/delis-angular:${NEW_VERSION}
docker push tledelivery.azurecr.io/delis-web-admin:${NEW_VERSION}
docker push tledelivery.azurecr.io/delis-web-api:${NEW_VERSION}
docker push tledelivery.azurecr.io/delis-sender-service:${NEW_VERSION}
docker push tledelivery.azurecr.io/delis-domibus-ws-sender:${NEW_VERSION}
