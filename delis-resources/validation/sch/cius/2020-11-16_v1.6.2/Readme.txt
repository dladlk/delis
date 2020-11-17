Peppol DK CIUS schematron
-------------------------
Peppol schematrons + Danish CIUS schematron.


1.0 Purpose
---------------------
Enforce the national Danish requirements for the Peppol BIS-Billing files, formally described in the CIUS document.
All national Danish rules are currently implemented in the main Peppol schematrons, so the DK-EN16931-UBL schematron file is empty for now.
- being ready to contain specific Danish rules in the future (so add this schematron to your chain of validations).


2.0 Content
-----------
This folder contains:
    CEN-EN16931-CII.xslt        CEN EN16931 CII schematron.
    CEN-EN16931-UBL.xslt        CEN EN16931 UBL schematron.
    PEPPOL-EN16931-CII.xslt     Peppol EN16931 CII schematron.
    PEPPOL-EN16931-UBL.xslt     Peppol EN16931 UBL schematron.
    DK-EN16931-UBL.xslt         Danish EN16931 UBL schematron.
    SCH folder                  The source SCH files.

This package also contains the CEN + Peppol SCH validation files, published at http://docs.peppol.eu/poacc/billing/3.0/
Should there be any doubt about the CEN + PEPPOL XSLT files, the *.sch files are the common reference.


3.0 Release Notes
-----------------
Schematron XSLTs are based on Peppol BIS schematrons version 3.0.8 (see https://docs.peppol.eu/poacc/billing/3.0/release-notes/).

PLEASE NOTE: Validation output is NOT same as Danish OIOUBL schematron output.


4.0 Revision log
----------------
2018.03.15  Version 1.0.0 mandatory.
2019.04.08  Version 1.1.0 mandatory.
2019.05.29  Version 1.2.0 mandatory (based on OpenPeppol version 3.0.3)
2019.08.15  Version 1.3.0 mandatory (based on OpenPeppol version 3.0.4)
2019.11.15  Version 1.4.0 mandatory (based on OpenPeppol version 3.0.5)
2020.05.15  Version 1.5.0 mandatory (based on OpenPeppol version 3.0.6)
2020.06.30  Version 1.5.1 mandatory (based on OpenPeppol version 3.0.7)
2020.11.16  Version 1.6.0 mandatory (based on OpenPeppol version 3.0.8 - including hotfix on greek rules)
2020.11.16  Version 1.6.1 mandatory (based on OpenPeppol version 3.0.8 - including hotfix on greek rules + dk fejlrettelse)
2020.11.16  Version 1.6.2 mandatory (bases on OpenPeppol version 3.0.9 - including hotfix from 2020-11-12)

5.0 Your feedback
-----------------
Please post your comments and feedback to the following email address:
    support@nemhandel.dk

Thanks!
