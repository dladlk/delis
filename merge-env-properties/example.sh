#!/bin/bash -e

mvn install

export DOMIBUS_OVERWRITE_domibus_alert_active=true
export DOMIBUS_OVERWRITE_domibus_alert_mail_sending_active=true
export DOMIBUS_OVERWRITE_domibus_alert_sender_smtp_url=172.16.2.4
export DOMIBUS_OVERWRITE_domibus_alert_sender_smtp_port=25
export DOMIBUS_OVERWRITE_domibus_alert_sender_email=dlk@edelivery-test.trueservice.dk
export DOMIBUS_OVERWRITE_domibus_alert_receiver_email=dlk@truelink.dk
export DOMIBUS_OVERWRITE_domibus_alert_user_login__failure_active=true


echo "List of properties in environment variable to merge into domibus.properties example:"
env | grep "DOMIBUS_OVERWRITE_"

echo "Create temp folder and copy example there"
mkdir -p ./temp
cp ./src/test/resources/domibus.properties ./temp

echo 
echo "Invoke merger..."
echo "********************************"
java -jar ./target/merge-env-properties-1.1.jar "DOMIBUS_OVERWRITE_" ./temp/domibus.properties
echo "********************************"
echo 
#echo "Difference between backup copy and result file:"
#diff ./temp/domibus.properties ./temp/domibus.properties.bak && echo

echo "Delete temp folder"
rm ./temp --recursive --verbose
