Peppol BIS-Billing to OIOUBL conversion
---------------------------------------


1.0 Purpose
---------------------
Transformation from Peppol BIS syntax to OIOUBL syntax for invoice and credit note.
Main target are the Danish public authorities, only supporting OIOUBL in their accounting systems.


2.0 Content
-----------
This folder contains:
    XSLT files (.xslt) for converting invoice and credit note. Note that the "BIS-Billing_2_OIOUBL_MASTER" include the two other XSLT files.
    Excel mapping documentation (.xlsx) for both file types.


3.0 Release Notes
-----------------
This version include a number of correction (since version 1.5.0) in the transformation from Peppol BIS Billing to OIOUBL.

- NH-462: Correction. regarding FreeOfChargeIndicator in Credite Note. No changes in mapping rules.  
- NH-476: Correction. Delivery information on line level is omitted if found on header.
- NH-481: Correction. regarding empty PayeeParty. No changes in mapping rules.
- NH-482: Correction. Unique InvoiceLine ID. Add prefix to ensure unique ID's.
- NH-512: Correction. PaymentMeans = 31 not converted correctly.
- NH-553: Correction. OrderReference. Remove not required dummy value. UOM prefix X correction. 
- NH-555: Correction. InvoicedQuantity. No more than 6 decimals.
- NH-633: Correction. Add default CompanyID '0000000', if CompanyID is missing in Peppol document (related to NH-553).


4.0 Revision log
----------------
2018.03.15  Version 1.0.0 mandatory.
2019.02.11  Version 1.1.0.RC1 released.
2019.04.08  Version 1.1.0 mandatory.
2019.05.29  Version 1.2.0 mandatory.
2020.05.05  Version 1.5.0 mandatory.
2020.06.26  Version 1.5.1 optional.
2020.11.05  Version 1.6.0 mandatory.


5.0 Your feedback
-----------------
Please post your comments and feedback to the following email address:
    support@nemhandel.dk

Thanks!
