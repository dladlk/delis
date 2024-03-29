<?xml version="1.0" encoding="UTF-16" standalone="yes"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"

	xmlns:oioi2="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"
	xmlns:oioc2="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"
	xmlns="urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2"
	xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
	xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
	xmlns:ccp="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-2"
	xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-2"
	xmlns:ext="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2"
	xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDataTypes-2"
	xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnqualifiedDataTypes-2"
	xmlns:ccts="urn:un:unece:uncefact:documentation:2"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	>

	<xsl:output method="xml" encoding="utf-8" indent="yes" />
	
   	<xsl:template match="/">
		<ApplicationResponse>
			<cbc:CustomizationID>urn:fdc:peppol.eu:poacc:trns:invoice_response:3</cbc:CustomizationID>
			<cbc:ProfileID>urn:fdc:peppol.eu:poacc:bis:invoice_response:3</cbc:ProfileID>
			<cbc:ID>ID</cbc:ID>
			<cbc:IssueDate>2019-01-01</cbc:IssueDate>
			<cbc:IssueTime>12:00:00</cbc:IssueTime>
			
			<xsl:apply-templates />
			
   		</ApplicationResponse>
   	</xsl:template>
   	
	<xsl:template match="oioi2:Invoice | oioc2:CreditNote">
		<xsl:apply-templates select="cac:AccountingCustomerParty/cac:Party"/>
		<xsl:apply-templates select="cac:AccountingSupplierParty/cac:Party"/>
		
		<xsl:call-template name="documentResponse"/>
		
	</xsl:template>

	<xsl:template match="cac:AccountingSupplierParty/cac:Party">
		<cac:ReceiverParty>
			<xsl:call-template name="copyParty"/>
		</cac:ReceiverParty>
	</xsl:template>
	<xsl:template match="cac:AccountingCustomerParty/cac:Party">
		<cac:SenderParty>
			<xsl:call-template name="copyParty"/>
		</cac:SenderParty>
	</xsl:template>

	<xsl:template name="copyParty">
		<xsl:copy-of select="*[local-name() = 'EndpointID' or local-name() = 'PartyIdentification' or local-name() = 'PartyLegalEntity']"/>
	</xsl:template>
	
	<xsl:template name="documentResponse">
	
		<cac:DocumentResponse>
			<cac:Response>
				<cbc:ResponseCode>RE</cbc:ResponseCode>
			<!--
				<cbc:EffectiveDate>2019-03-27</cbc:EffectiveDate>
				<cac:Status>
					<cbc:StatusReasonCode listID="OPStatusAction">REF</cbc:StatusReasonCode>
					<cbc:StatusReason>VAT Reference not found</cbc:StatusReason>
					<cac:Condition>
						<cbc:AttributeID>BT-48</cbc:AttributeID>
						<cbc:Description>EU123456789</cbc:Description>
					</cac:Condition>
				</cac:Status>
			-->
			</cac:Response>
			<cac:DocumentReference>
				<cbc:ID><xsl:value-of select="cbc:ID/text()"/></cbc:ID>
				<cbc:IssueDate><xsl:value-of select="cbc:IssueDate/text()"/></cbc:IssueDate>
				<cbc:DocumentTypeCode>
					<xsl:choose>
						<xsl:when test="local-name(/*) = 'Invoice'">380</xsl:when>
						<xsl:when test="local-name(/*) = 'CreditNote'">381</xsl:when>
					</xsl:choose>
				</cbc:DocumentTypeCode>
			</cac:DocumentReference>
			<cac:IssuerParty>
				<cac:PartyIdentification>
					<cbc:ID schemeID="0088">5798009882875</cbc:ID>
				</cac:PartyIdentification>
				<cac:PartyName>
					<cbc:Name>ERST - DANISH BUSINESS AUTHORITY</cbc:Name>
				</cac:PartyName>
			</cac:IssuerParty>
		</cac:DocumentResponse>
		
	</xsl:template>
	
</xsl:stylesheet>
