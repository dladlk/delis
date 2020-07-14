
PEPPOL BIS-Billing to OIOUBL conversion
---------------------------------------


1.0 Purpose
---------------------
Transformation from PEPPOL BIS syntax to OIOUBL syntax for invoice and credit note.
Main target are the Danish public authorities, only supporting OIOUBL in their accounting systems.


2.0 Content
-----------
This folder contains:
    XSLT files (.xslt) for converting invoice and credit note. Note that the "BIS-Billing_2_OIOUBL_MASTER" include the two other XSLT files.
    Excel mapping documentation (.xlsx) for both file types.


3.0 Release Notes
-----------------
This version include a number of correction (since version 1.5.0) in the transformation from PEPPOL BIS Billing to OIOUBL.

- NH-462: Correction regarding FreeOfChargeIndicator in Credite Note. No changes in mapping rules.  
- NH-476: Correction. Delivery information on line level is omitted if found on header.
- NH-481: Correction regarding empty PayeeParty. No changes in mapping rules.


4.0 Revision log
----------------
2018.03.15  Version 1.0.0 mandatory.
2019.02.11  Version 1.1.0.RC1 released.
2019.04.08  Version 1.1.0 mandatory.
2019.05.29  Version 1.2.0 mandatory.
2020.05.05  Version 1.5.0 mandatory.
2020.06.26  Version 1.5.1 optional. 


5.0 Your feedback
-----------------
Please post your comments and feedback to the following email address:
    support@nemhandel.dk

Thanks!
