<?xml version="1.0" encoding="UTF-16" standalone="yes"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"

	xmlns:oioi2="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"
	xmlns:oioc2="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"
	xmlns:oioar2="urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2"

	xmlns:oioo2   ="urn:oasis:names:specification:ubl:schema:xsd:Order-2"
	xmlns:oioor2  ="urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2"
	xmlns:oiocat2 ="urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2"
	
	xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
	xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
	xmlns:ccp="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-2"
	xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-2"
	xmlns:ext="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2"
	
	xmlns:rsm="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100"
	xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100"
	xmlns:udt="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100"
	xmlns:qdt="urn:un:unece:uncefact:data:standard:QualifiedDataType:100" 
	
	exclude-result-prefixes="oioi2 oioc2 oioar2 cac cbc ccp ext sdt xs rsm ram udt qdt"
	>

	<xsl:output method="xml" encoding="utf-8" indent="yes" />
	
   	<xsl:template match="/">
   		<Info>
			<xsl:apply-templates mode="general"/>
			<xsl:apply-templates mode="specific"/>
   		</Info>
   	</xsl:template>
   	
   	<xsl:template match="*" mode="general">
   		<DocumentRoot>
			<RootTag><xsl:value-of select="local-name()" /></RootTag>
			<NameSpace><xsl:value-of select="namespace-uri()" /></NameSpace>
   		</DocumentRoot>
   	</xsl:template>

	<xsl:template match="oioi2:Invoice" mode="specific">
		<xsl:call-template name="oioubl"/>
		<xsl:call-template name="oioubl-accounting-supplier-customer"/>
	</xsl:template>
	<xsl:template match="oioc2:CreditNote" mode="specific">
		<xsl:call-template name="oioubl"/>
		<xsl:call-template name="oioubl-accounting-supplier-customer"/>
	</xsl:template>

	<xsl:template match="oioo2:Order" mode="specific">
		<xsl:call-template name="oioubl"/>
		<xsl:call-template name="ubl-buyer-seller"/>
	</xsl:template>
	<xsl:template match="oioor2:OrderResponse" mode="specific">
		<xsl:call-template name="oioubl"/>
		<xsl:call-template name="ubl-seller-buyer"/>
	</xsl:template>
	<xsl:template match="oiocat2:Catalogue" mode="specific">
		<xsl:call-template name="oioubl"/>
		<xsl:call-template name="oioubl-sender-receiver"/>
	</xsl:template>

	<xsl:template match="oioar2:ApplicationResponse" mode="specific">
		<xsl:call-template name="oioubl"/>
		<xsl:call-template name="oioubl-sender-receiver"/>
	</xsl:template>
	<xsl:template match="rsm:CrossIndustryInvoice" mode="specific">
		<xsl:call-template name="cii"/>
	</xsl:template>

	<xsl:template match="*" mode="specific">
		<Unsupported/>
	</xsl:template>	
	
	<xsl:template name="oioubl">
		<ID><xsl:value-of select="cbc:ID/text()"/></ID>
		<Date><xsl:value-of select="cbc:IssueDate/text()"/></Date>
		<CustomizationID><xsl:value-of select="cbc:CustomizationID/text()"/></CustomizationID>
		<Profile>
			<ID>
				<xsl:value-of select="cbc:ProfileID/text()"/>
			</ID>
			<SchemeAgencyID>
				<xsl:value-of select="cbc:ProfileID/@schemeAgencyID"/>
			</SchemeAgencyID>
			<SchemeID>
				<xsl:value-of select="cbc:ProfileID/@schemeID"/>
			</SchemeID>
		</Profile>
	</xsl:template>
	<xsl:template name="oioubl-accounting-supplier-customer">
		<Sender>
			<xsl:call-template name="fill-party">
				<xsl:with-param name="party" select="cac:AccountingSupplierParty/cac:Party"/>
			</xsl:call-template>		
		</Sender>
		<Receiver>
			<xsl:call-template name="fill-party">
				<xsl:with-param name="party" select="cac:AccountingCustomerParty/cac:Party"/>
			</xsl:call-template>		
		</Receiver>
		<AmountNegative>
			<xsl:value-of select="cac:LegalMonetaryTotal/cbc:PayableAmount/text() &lt; 0"/>
		</AmountNegative>
	</xsl:template>
	
	<xsl:template name="ubl-buyer-seller">
		<Sender>
			<xsl:call-template name="fill-party">
				<xsl:with-param name="party" select="cac:BuyerCustomerParty/cac:Party"/>
			</xsl:call-template>		
		</Sender>
		<Receiver>
			<xsl:call-template name="fill-party">
				<xsl:with-param name="party" select="cac:SellerSupplierParty/cac:Party"/>
			</xsl:call-template>		
		</Receiver>
	</xsl:template>
	
	<xsl:template name="ubl-seller-buyer">
		<Sender>
			<xsl:call-template name="fill-party">
				<xsl:with-param name="party" select="cac:SellerSupplierParty/cac:Party"/>
			</xsl:call-template>		
		</Sender>
		<Receiver>
			<xsl:call-template name="fill-party">
				<xsl:with-param name="party" select="cac:BuyerCustomerParty/cac:Party"/>
			</xsl:call-template>		
		</Receiver>
	</xsl:template>

	<xsl:template name="fill-party">
		<xsl:param name="party"/>

		<ID>
			<xsl:value-of select="$party/cbc:EndpointID/text()"/>
		</ID>
		<SchemeID>
			<xsl:value-of select="$party/cbc:EndpointID/@schemeID"/>
		</SchemeID>
		<Name>
			<xsl:choose>
				<xsl:when test="$party/cac:PartyName/cbc:Name/text()">
					<xsl:value-of select="$party/cac:PartyName/cbc:Name/text()"/>
				</xsl:when>				
				<xsl:when test="$party/cac:PartyLegalEntity/cbc:RegistrationName/text()">
					<xsl:value-of select="$party/cac:PartyLegalEntity/cbc:RegistrationName/text()"/>
				</xsl:when>				
			</xsl:choose>
		</Name>
		<Country>
			<xsl:value-of select="$party/cac:PostalAddress/cac:Country/cbc:IdentificationCode/text()"/>
		</Country>
		<Email>
			<xsl:value-of select="$party/cac:Contact/cbc:ElectronicMail/text()"/>
		</Email>
		
	</xsl:template>

	<xsl:template name="oioubl-sender-receiver">
		<Sender>
			<xsl:choose>
				<xsl:when test="cac:SenderParty">
					<xsl:call-template name="fill-party">
						<xsl:with-param name="party" select="cac:SenderParty"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="cac:ProviderParty">
					<xsl:call-template name="fill-party">
						<xsl:with-param name="party" select="cac:ProviderParty"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</Sender>
		
		<Receiver>
			<xsl:call-template name="fill-party">
				<xsl:with-param name="party" select="cac:ReceiverParty"/>
			</xsl:call-template>		
		</Receiver>

	</xsl:template>
	
	<xsl:template name="cii">
		<!-- Copied from PEPPOL_DK_CIUS_2018-03-15_v1.0.0.34866\Konvertering\CII_2_BIS3\CII_2_BIS-Billing.xslt -->
		<ID>
			<xsl:value-of select="rsm:ExchangedDocument/ram:ID"/>
		</ID>
		<Date>
			<xsl:value-of select="substring(rsm:ExchangedDocument/ram:IssueDateTime/udt:DateTimeString, 1,4)"/>-<xsl:value-of select="substring(rsm:ExchangedDocument/ram:IssueDateTime/udt:DateTimeString, 5,2)"/>-<xsl:value-of select="substring(rsm:ExchangedDocument/ram:IssueDateTime/udt:DateTimeString, 7,2)"/>
		</Date>

		<!-- Hardcoded -->
		<CustomizationID><xsl:value-of select="'urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0'"/></CustomizationID>
		<Profile>
			<ID><xsl:value-of select="'urn:fdc:peppol.eu:2017:poacc:billing:01:1.0'"/></ID>
		</Profile>

		<Sender>
			<ID>
				<xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:URIUniversalCommunication/ram:URIID/text()"/>
			</ID>
			<SchemeID>
				<xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:URIUniversalCommunication/ram:URIID/@schemeID"/>
			</SchemeID>
			<Name>
				<xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:Name/text()"/>
			</Name>
			<Country>
				<xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress/ram:CountryID/text()"/>
			</Country>
			<Email>
				<xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:DefinedTradeContact/ram:EmailURIUniversalCommunication/ram:URIID"/>
			</Email>
		</Sender>
		<Receiver>
			<ID>
				<xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:URIUniversalCommunication/ram:URIID/text()"/>
			</ID>
			<SchemeID>
				<xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:URIUniversalCommunication/ram:URIID/@schemeID"/>
			</SchemeID>
			<Name>
				<xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:Name/text()"/>
			</Name>
			<Country>
				<xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress/ram:CountryID/text()"/>
			</Country>
			<Email>
				<xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:DefinedTradeContact/ram:EmailURIUniversalCommunication/ram:URIID"/>
			</Email>
		</Receiver>
		<AmountNegative>
			<xsl:value-of select="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:DuePayableAmount/text() &lt; 0"/>
		</AmountNegative>
	</xsl:template>
	
</xsl:stylesheet>
