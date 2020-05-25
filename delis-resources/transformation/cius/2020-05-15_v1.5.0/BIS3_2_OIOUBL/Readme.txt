
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
This version include a number of correction in the transformation from PEPPOL BIS Billing to OIOUBL.

- NH-326/NH-388: Endpoint ID's: Update based on Peppol code list version 5 + 6. Adjustment regarding code list value NL:KVK.
- NH-383: InvoicedQuantity: Peppol invoice accept invoicedquantity = 0 on InvoiceLine, but this is not valid in OIOUBL schematron. Default value invoiced quantity = 1 is added if invoicedquantity is 0 (or blank) when LineExtensionAmount is 0.
- NH-384: PrepaidAmount: Correction to ensure that existing mapping rule (if zero then field is removed) is removing the field to be able to pass OIOUBL schematron.
- NH-385: OIOUBL Schematron requires that AllowanceTotalAmount and AllowanceChangeàAmount (on invoice level) is <> 0. New Mapping rule added on AllowanceChangeàAmount (if zero then field is removed) to avoid F-LIB019 on invoices with AllowanceChangeàAmount = 0.  
- NH-387: If OrderReferende is  missing, "N/A" is added in ID. No change in mapping rules.


4.0 Revision log
----------------
2018.03.15  Version 1.0.0 mandatory.
2019.02.11  Version 1.1.0.RC1 released.
2019.04.08  Version 1.1.0 mandatory.
2019.05.29  Version 1.2.0 mandatory.
2020.05.05  Version 1.5.0 mandatory.


5.0 Your feedback
-----------------
Please post your comments and feedback to the following email address:
    support@nemhandel.dk

Thanks!
