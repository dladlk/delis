<?xml version="1.0" encoding="UTF-8"?>
<!--
******************************************************************************************************************

	Main file for conversion from PEPPOL BIS Billing to OIOUBL.

	Publisher:          NemHandel / Erhvervsstyrelsen
	Last changed by:    $Author: DanO $
	Last changed date:  $Date: 2020-05-05 16:52:11 +0100 (ti, 5 maj 2020) $

	Description:		General conversion of the PEPPOL BIS Billing syntax to the danish OIOUBL syntax.
	Rights:				It can be used following the Common Creative License

	all terms derived from http://dublincore.org/documents/dcmi-terms/
	For more information, see www.nemhandel.dk

*****************************************************************************************************************
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs   = "http://www.w3.org/2001/XMLSchema"
	xmlns:cac  = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
	xmlns:cbc  = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
	xmlns:ccts = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-2"
	xmlns:sdt  = "urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-2"
	exclude-result-prefixes="xs">
<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:strip-space elements="*" />

	<xsl:variable name="Converted" select="number(/*/cac:LegalMonetaryTotal/cbc:PayableAmount) &lt; 0"/>

	<!--Including main templates-->
	<xsl:include href="BIS-Billing-INV_2_OIOUBL_INV.xslt"/>
	<xsl:include href="BIS-Billing-CRN_2_OIOUBL_CRN.xslt"/>

    	<xsl:template match="/">

			<!--Selecting to build Invoice if tje received BIS is an Invoice or a Negative CreditNote and selecting to build OIUBL CreditNote if received PEPPOL BIS CreditNote or negative Invoice-->
			<xsl:choose>
				<xsl:when test="number(*[local-name()='Invoice']/cac:LegalMonetaryTotal/cbc:PayableAmount) &gt;= 0">
					<xsl:apply-templates mode="invoice"/>
				</xsl:when>
				<xsl:when test="number(*[local-name()='Invoice']/cac:LegalMonetaryTotal/cbc:PayableAmount) &lt; 0">
					<xsl:apply-templates mode="creditnote"/>
				</xsl:when>
				<xsl:when test="number(*[local-name()='CreditNote']/cac:LegalMonetaryTotal/cbc:PayableAmount) &gt;= 0">
					<xsl:apply-templates mode="creditnote"/>
				</xsl:when>
				<xsl:when test="number(*[local-name()='CreditNote']/cac:LegalMonetaryTotal/cbc:PayableAmount) &lt; 0">
					<xsl:apply-templates mode="invoice"/>
				</xsl:when>
			</xsl:choose>
     	</xsl:template>


	<xsl:template match="*" mode="invoice">
		<Error>
			<Errortext>Fatal error: Unsupported documenttype! This stylesheet only supports conversion of a PEPPOL BIS 3.0 Invoice.</Errortext>
			<Input><xsl:value-of select="."/></Input>
		</Error>
	</xsl:template>

	<xsl:template match="*" mode="creditnote">
		<Error>
			<Errortext>Fatal error: Unsupported documenttype! This stylesheet only supports conversion of a PEPPOL BIS 3.0 CreditNote.</Errortext>
			<Input><xsl:value-of select="."/></Input>
		</Error>
	</xsl:template>

</xsl:stylesheet>