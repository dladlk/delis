Domibus JMS Timeout Patch Plugin
===============

In older versions of Domibus, default receive timeout was either not defined or was 1 second.

This plugin allowed to overwrite most of receiveTimeout values to 5 second to reduce number of 
requests from Domibus to JMS

Now it is not used.