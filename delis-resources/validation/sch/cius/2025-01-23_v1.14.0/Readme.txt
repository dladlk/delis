Danish eInvoice implementation for CEN EN standards
---------------------------------------------------
This package is released by Nemhandel / Erhvervsstyrelsen (Danish Business Authority).


1.0 Purpose and usage
---------------------
Purpose of this ZIP package:
-   Support the implementation of eInvoicing in Denmark following provisions and standards referred
    by the European directive for eInvoicing (2014/55/EU).
-   Help Danish public authorities and their suppliers understand the national Danish requirements and
    interpretation of the CII and Peppol BIS syntax formats.

The content of this package is based on and refers to the following standards and specifications:
-	CEN eInvoicing standard EN 16931 including Semantic Datamodel and other subparts such as:
    "List of syntaxes" and Syntax bindings for UBL and CII. Distributed by Danish Standards and other
    CEN Member organisations.
-	Peppol BIS3 Billing profile including the Peppol CIUS available from OpenPeppol AISBL (publish
    for review by december 21st 2017).


2.0 Content of the package
--------------------------
This ZIP package contains:
-   Danish CIUS: Danish Core Invoice Usage Specification, extending the Peppol CIUS (in Danish and English).
-   Example files (CII and BIS) for valid invoice and credit note.
-   Schematron.


3.0 For Implementers
--------------------
The implementation of the eInvoice Directive (2014/55/EU) in Denmark has three levels.

1: CEN European standard for eInvoicing (EN16931)

The European standard is a normative description of the required and optional fields in an Invoice and Credit note,
supplemented with descriptions and examples (EN16931-1).
For some of the fields the European standard points to requested code lists.
In the European standard also syntax mappings to UBL 2.1 and CII are described (EN16931-3).

The European standard is the foundation for understanding the eInvoice format.
The European standard is distributed from the national standardisation organisations.

2: Peppol BIS Billing (CIUS)

As part of the European standard it is described, how restrictions can be made to the European standard e.g. to support
national legislation. This restriction must be created as a "Core Invoice Usage Specification" (CIUS).

The OpenPeppol organization has created a European CIUS "Peppol BIS Billing", where some restrictions has been made to the
European standard, to focus the processes and align with the Peppol documents already in use.

This CIUS (Peppol BIS Billing) is the Invoice and Credit note implementation that the Public Authorities in Denmark are obligated to receive.
The Public Authorities are also obligated to receive the CII Invoice and Credit note in accordance with the requirements in
the EU eInvoicing Directive, either directly, or mapped to Peppol BIS Billing.

The most essential Danish business rules known from OIOUBL today are incorporated to the Peppol BIS Billing validation.

The Peppol BIS Billing and the related tools can be found here:

- Peppol BIS:        https://docs.peppol.eu/poacc/billing/3.0/2024-Q4/
- Peppol Validation: https://github.com/OpenPeppol/peppol-bis-invoice-3/tree/master/rules/sch

3: Danish CIUS

This package contains the third level of the implementation.

In the package there is a Danish CIUS describing a few required supplementary business rules, which are not implemented in the
Peppol BIS Billing. Besides that, the CIUS is describing the main differences or attention points between the Peppol BIS Billing
and the OIOUBL Invoice and Credit note.


4.0 Release Notes
-----------------
- Peppol BIS3 validation artifacts updated (based on Peppol version 3.0.18).
- Conversion BIS3_2_OIOUBL & CII_2_PEPPOL are removed from this package. 
  - All OIOUBL conversions will be releases separately as a part of the OIOUBL 3 major release package.


5.0 Revision log
----------------
- 2018.03.15  Version 1.0.0 mandatory.
- 2019.04.08  Version 1.1.0 mandatory.
- 2019.05.29  Version 1.2.0 mandatory.
- 2019.08.15  Version 1.3.0 mandatory (based on OpenPeppol version 3.0.4)
- 2019.11.15  Version 1.4.0 mandatory (based on OpenPeppol version 3.0.5)
- 2019.12.04  Version 1.4.1 optional (fix bug in CII_2_BIS3 conversion)
- 2020.05.15  Version 1.5.0 mandatory (based on OpenPeppol version 3.0.6)
- 2020.06.30  Version 1.5.1 mandatory (based on OpenPeppol version 3.0.7)
- 2020.11.16  Version 1.6.0 mandatory (based on OpenPeppol version 3.0.8)
- 2020.11.16  Version 1.6.1 mandatory (based on OpenPeppol version 3.0.8) - inkl. dk rettelse
- 2020.11.16  Version 1.6.2 mandatory (based on OpenPeppol version 3.0.9) - inkl. hotfix
- 2021.05.07  Version 1.7.0 mandatory (based on OpenPeppol version 3.0.10)
- 2021.11.15  Version 1.8.0 mandatory (based on OpenPeppol version 3.0.12)
- 2022.05.30  Version 1.9.0 mandatory (based on OpenPeppol version 3.0.13)
- 2022.05.30  Version 1.9.1 mandatory (based on OpenPeppol version 3.0.13 - Inkl. hotfix for schematron opdatering)
- 2022.11.18  Version 1.10.0 mandatory (based on OpenPeppol version 3.0.14)
- 2023.05.23  Version 1.11.0 mandatory (based on OpenPeppol version 3.0.15)
- 2023.12.22  Version 1.12.0 mandatory (based on OpenPeppol version 3.0.16)
- 2024.05.27  Version 1.13.0 mandatory (based on OpenPeppol version 3.0.17)
- 2025.01.23  Version 1.14.0 mandatory (based on OpenPeppol version 3.0.18)

6.0 Your feedback
-----------------
Please post your comments and feedback to the following address:

https://nemhandel.dk/nemhandel-support-og-hjaelp

Thanks!
