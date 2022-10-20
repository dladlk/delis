# Custom FS plugin looks for environment variable
# fsplugin.validation.endpoint

If it is present, fs plugin regards it as rest endpoint and sends request to validate "partyTo" for each new file

Endpoint should be path to delis server + path to checking rest service (currently it is /rest/open/receivercheck)

#Example: fsplugin.validation.endpoint = http://delis:8080/rest/open/receivercheck

If no identifier exists for the party or other error, document will be rejected.

Note. Need to test it in real life to ensure that party type and id corresponds to ones we expect.  