
PEPPOL DK CIUS schematron
-------------------------
PEPPOL schematrons + Danish CIUS schematron.


1.0 Purpose
---------------------
Enforce the national Danish requirements for the PEPPOL BIS-Billing files, formally described in the CIUS document.
All national Danish rules are currently implemented in the main PEPPOL schematrons, so the DK-EN16931-UBL schematron file is empty for now.
- being ready to contain specific Danish rules in the future (so add this schematron to your chain of validations).


2.0 Content
-----------
This folder contains:
    CEN-EN16931-CII.xslt        CEN EN16931 CII schematron.
    CEN-EN16931-UBL.xslt        CEN EN16931 UBL schematron.
    PEPPOL-EN16931-CII.xslt     PEPPOL EN16931 CII schematron.
    PEPPOL-EN16931-UBL.xslt     PEPPOL EN16931 UBL schematron.
    DK-EN16931-UBL.xslt         Danish EN16931 UBL schematron.
    SCH folder                  The source SCH files.

This package also contains the CEN + PEPPOL SCH validation files, published at http://docs.peppol.eu/poacc/billing/3.0/
Current files are based on commit 96b09e5 (mandatory 30.06.2020) from https://github.com/OpenPEPPOL/peppol-bis-invoice-3/tree/master/rules/sch
Should there be any doubt about the CEN + PEPPOL XSLT files, the *.sch files are the common reference.


3.0 Release Notes
-----------------
Schematron XSLTs are based on PEPPOL BIS schematrons version 3.0.7 (see https://docs.peppol.eu/poacc/billing/3.0/release-notes/).

PLEASE NOTE: Validation output is NOT same as Danish OIOUBL schematron output.


4.0 Revision log
----------------
2018.03.15  Version 1.0.0 mandatory.
2019.04.08  Version 1.1.0 mandatory.
2019.05.29  Version 1.2.0 mandatory (based on OpenPEPPOL version 3.0.3)
2019.08.15  Version 1.3.0 mandatory (based on OpenPEPPOL version 3.0.4)
2019.11.15  Version 1.4.0 mandatory (based on OpenPEPPOL version 3.0.5)
2020.05.15  Version 1.5.0 mandatory (based on OpenPEPPOL version 3.0.6)
2020.06.30  Version 1.5.1 mandatory (based on OpenPEPPOL version 3.0.7)

5.0 Your feedback
-----------------
Please post your comments and feedback to the following email address:
    support@nemhandel.dk

Thanks!
