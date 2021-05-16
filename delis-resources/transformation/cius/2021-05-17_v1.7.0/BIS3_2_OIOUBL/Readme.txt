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
This version include a number of correction (since version 1.6.0) in the transformation from Peppol BIS Billing to OIOUBL.

  NH-748: Correction. Wrong default value for CreditedQuantity in negative Credite Note.
  NH-763: Correction. Default value added for AccountingSupplierParty\PartyLegalEntity\CompanyID if Peppol value is empty.


4.0 Revision log
----------------
2018.03.15  Version 1.0.0 mandatory.
2019.02.11  Version 1.1.0.RC1 released.
2019.04.08  Version 1.1.0 mandatory.
2019.05.29  Version 1.2.0 mandatory.
2020.05.05  Version 1.5.0 mandatory.
2020.06.26  Version 1.5.1 optional.
2020.11.05  Version 1.6.0 mandatory.
2021.05.07  Version 1.7.0 mandatory.


5.0 Your feedback
-----------------
Please post your comments and feedback to the following email address:
    support@nemhandel.dk

Thanks!
