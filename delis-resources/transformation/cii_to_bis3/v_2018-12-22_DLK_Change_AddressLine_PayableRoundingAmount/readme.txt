Copied version from cii_to_bis3/v_2018-03-15_34856/CII_2_BIS-Billing.xslt with only 2 set of changes:

1. tag cac:AddressLine in cac:PostalAddress according to UBL 2.1 should be located at the end of PostalAddress, before cac:Country - see http://www.datypic.com/sc/ubl21/e-cac_PostalAddress.html
Moved to be just in front of cac:Country.
Done for cac:AccountingSupplierParty, cac:AccountingCustomerParty, cac:TaxRepresentativeParty and cac:Delivery.

2. There is no tag cbc:RoundingAmount in cac:LegalMonetaryTotal, but cbc:PayableRoundingAmount, see http://www.datypic.com/sc/ubl21/e-cac_LegalMonetaryTotal.html
Renamed to cbc:cbc:PayableRoundingAmount.
Done at single place in cac:LegalMonetaryTotal