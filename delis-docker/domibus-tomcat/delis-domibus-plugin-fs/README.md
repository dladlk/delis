# Default Domibus FS Plugin customization

- pre-validation of receiver and profile via external REST service with params /RECEIVER/SERVICE/ACTION
- pre-validation of XML payload via external REST service
- default FS Plugin subfolders in MAIN/IN folders tuning - skip creation of RECEIVER/MESSAGEID subfolders, but either just MESSAGEID or RECEIVER_MESSAGEID subfolder

## Configuration options

```
#
# Validation
#

# Receipient and action/profile validatation endpoint URL
# E.g.: https://edelivery.trueservice.dk/validator/validate
fsplugin.validation.endpoint=

# XML XSD/schematron validation endpoint URL
fsplugin.payload.validation.endpoint=


#
# MAIN/IN folder storage tuning
#

# If true - received payload and metadata will be stored in single subfolder of IN, otherwise - as by default in RECEIPIENTID/MESSAGEID folders
fsplugin.messages.location.in.onefolder=false

# If true - subfolder in IN will be built as RECIPIENTID_MESSAGEID, if false - just MESSAGEID
fsplugin.messages.location.in.onefolder.includerecipient=true
```
