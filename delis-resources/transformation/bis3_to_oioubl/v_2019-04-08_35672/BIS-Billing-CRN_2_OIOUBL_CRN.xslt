<?xml version="1.0" encoding="UTF-8"?>
<!--
******************************************************************************************************************

	Sub conversion from PEPPOL BIS Billing to OIOUBL (use the MASTER conversion!)

	Publisher:          NemHandel / Erhvervsstyrelsen
	Repository path:    $HeadURL: https://svn.softwareborsen.dk/nemhandelinternal/Dokumentstandarder/PEPPOL/BIS3/Konvertering/BIS3_2_OIOUBL/BIS-Billing-CRN_2_OIOUBL_CRN.xslt $
	File version:       $Revision: 35669 $
	Last changed by:    $Author: PeterSone $
	Last changed date:  $Date: 2019-03-14 15:47:17 +0100 (to, 14 mar. 2019) $

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

	
	<xsl:template match="*[local-name()='CreditNote' or local-name()='Invoice']" mode="creditnote">
		<!-- Parameters (please assign before using this stylesheet) -->

		<xsl:variable name="RoundingAmount" select="format-number(cac:LegalMonetaryTotal/cbc:PayableRoundingAmount, '0.00')"/>


		<!-- Global Headerfields -->

		<xsl:variable name="UBLVersionID" select="'2.0'"/>
		<xsl:variable name="CustomizationID" select="'OIOUBL-2.02'"/>
		<xsl:variable name="ProfileID" select="'urn:www.nesubl.eu:profiles:profile5:ver2.0'"/>
		<xsl:variable name="ProfileID_schemeID" select="'urn:oioubl:id:profileid-1.2'"/>
		<xsl:variable name="ProfileID_schemeAgencyID" select="'320'"/>
		<xsl:variable name="ProjectReference_ID" select="cac:ProjectReference/cbc:ID"/>
		<xsl:variable name="DocumentCurrencyCode" select="cbc:DocumentCurrencyCode"/>
	
		<!-- Start of CreditNote -->
		<CreditNote xmlns="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2">

			<cbc:UBLVersionID>
				<xsl:value-of select="$UBLVersionID"/>
			</cbc:UBLVersionID>
			
			<cbc:CustomizationID>
				<xsl:value-of select="$CustomizationID"/>
			</cbc:CustomizationID>
			
			<cbc:ProfileID schemeAgencyID="{$ProfileID_schemeAgencyID}" schemeID="{$ProfileID_schemeID}">
				<xsl:value-of select="$ProfileID"/>
			</cbc:ProfileID>
			
			<cbc:ID>
				<xsl:value-of select="cbc:ID"/>
			</cbc:ID>

			<cbc:IssueDate>
				<xsl:value-of select="cbc:IssueDate"/>
			</cbc:IssueDate>


			<xsl:if test="string(cbc:TaxPointDate)">
				<cbc:TaxPointDate><xsl:value-of select="cbc:TaxPointDate"/>
			</cbc:TaxPointDate></xsl:if>	
			
			<xsl:call-template name="NoteCreditNote"></xsl:call-template>


			<cbc:DocumentCurrencyCode>
				<xsl:value-of select="cbc:DocumentCurrencyCode"/>
			</cbc:DocumentCurrencyCode>
			
			<xsl:if test="string(cbc:TaxCurrencyCode)">
				<cbc:TaxCurrencyCode><xsl:value-of select="cbc:TaxCurrencyCode"/>
			</cbc:TaxCurrencyCode></xsl:if>
			
			<xsl:if test="string(cbc:AccountingCost)">
				<cbc:AccountingCost><xsl:value-of select="cbc:AccountingCost"/>
			</cbc:AccountingCost></xsl:if>

			<!-- InvoicePeriod -->
			<xsl:apply-templates select="cac:InvoicePeriod" mode="creditnote"/>

			<!-- OrderReference -->

			<xsl:apply-templates select="cac:OrderReference" mode="creditnote"/>
			
			<!--BillingReference-->
			<xsl:apply-templates select="cac:BillingReference" mode="creditnote"/>
			
			<!-- DespatchDocumentReference -->
			<xsl:apply-templates select="cac:DespatchDocumentReference" mode="creditnote"/>

			<!-- ReceiptDocumentReference -->
			<xsl:apply-templates select="cac:ReceiptDocumentReference" mode="creditnote"/>
			
			<!-- OrginatorDocumentReference -->
			<xsl:apply-templates select="cac:OrginatorDocumentReference" mode="creditnote"/>

			<!-- ContractDocumentReference -->
			<xsl:apply-templates select="cac:ContractDocumentReference" mode="creditnote"/>

			<!-- AdditionalDocumentReference -->
			<xsl:call-template name="AdditionalDocumentReferenceCreditNote"/>
			<xsl:apply-templates select="cac:AdditionalDocumentReference" mode="creditnote"/>
			
			<xsl:if test="string($ProjectReference_ID)">
				<xsl:call-template name="ProjectReferenceIDCreditNote"/>
			</xsl:if>

		
			<!-- AccountingSupplierParty -->
			<xsl:apply-templates select="cac:AccountingSupplierParty" mode="creditnote"/>

			<!-- AccountingCustomerParty -->
			<xsl:apply-templates select="cac:AccountingCustomerParty" mode="creditnote"/>

			<!-- PayeeParty -->
			<xsl:apply-templates select="cac:PayeeParty" mode="creditnote"/>
			
			<!-- cac:TaxRepresentativeParty -->
			<xsl:apply-templates select="cac:TaxRepresentativeParty" mode="creditnote"/>

			<!-- AllowanceCharge -->
			<xsl:apply-templates select="cac:AllowanceCharge" mode="creditnote"/>
			
			<!-- TaxExchangeRate -->
			<xsl:apply-templates select="cac:TaxExchangeRate" mode="creditnote"/>
			
			<!--TaxTotal -->
			<xsl:choose>
				<xsl:when test="cac:TaxTotal/cac:TaxSubtotal/cbc:TaxAmount/@currencyID = $DocumentCurrencyCode">
					<xsl:apply-templates select="cac:TaxTotal" mode="creditnote">
						<xsl:with-param name="RoundingAmount" select="$RoundingAmount"/>
					</xsl:apply-templates>
				</xsl:when>
			</xsl:choose>
			

			<!-- LegalMonetaryTotal -->
			<xsl:apply-templates select="cac:LegalMonetaryTotal" mode="creditnote"/>

			<!-- CreditNoteLine -->
				
			<xsl:choose>
				<xsl:when test="$Converted">
					<xsl:for-each select="cac:InvoiceLine">
						<xsl:call-template name="CreditNoteLine"/>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each select="cac:CreditNoteLine">
						<xsl:call-template name="CreditNoteLine"/>
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
		<!--End of CreditNote-->	
		</CreditNote>
	</xsl:template>


	<!-- .............................. -->
	<!--           Templates            -->
	<!-- .............................. -->


	<!--  InvoicePeriod template -->
	<xsl:template match="cac:InvoicePeriod" mode="creditnote">
		<cac:InvoicePeriod>
			<xsl:if test="string(cbc:StartDate)">
				<cbc:StartDate>
					<xsl:value-of select="cbc:StartDate"/>
				</cbc:StartDate>
			</xsl:if>
			<xsl:if test="string(cbc:EndDate)">
				<cbc:EndDate>
					<xsl:value-of select="cbc:EndDate"/>
				</cbc:EndDate>
			</xsl:if>
			<xsl:if test="string(cbc:DescriptionCode)">
				<cbc:Description>
					<xsl:value-of select="cbc:DescriptionCode"/>
				</cbc:Description>
			</xsl:if>
		</cac:InvoicePeriod>
	</xsl:template>

	<!--  OrderReference template -->
	<xsl:template match="cac:OrderReference" mode="creditnote">
	<xsl:variable name="POnumber" select="cbc:ID"/>
	<xsl:variable name="DefaultID" select="'N/A'"/>

	
		<cac:OrderReference>
			<xsl:choose>
				<xsl:when test="string($POnumber)">
					<cbc:ID><xsl:value-of select="cbc:ID"/></cbc:ID>
				</xsl:when>
				<xsl:otherwise><cbc:ID><xsl:value-of select="$DefaultID"/></cbc:ID></xsl:otherwise>
			</xsl:choose>
			<xsl:if test="cbc:SalesOrderID != ''">
				<cbc:SalesOrderID>
					<xsl:value-of select="cbc:SalesOrderID"/>
				</cbc:SalesOrderID>
			</xsl:if>
		</cac:OrderReference>	
	</xsl:template>


	<!--  BillingReference template -->
	<xsl:template match="cac:BillingReference" mode="creditnote">
		<cac:BillingReference>
			<cac:InvoiceDocumentReference>
				<cbc:ID><xsl:value-of select="cac:InvoiceDocumentReference/cbc:ID"/></cbc:ID>
				<xsl:if test="cac:InvoiceDocumentReference/cbc:IssueDate != ''"><cbc:IssueDate><xsl:value-of select="cac:InvoiceDocumentReference/cbc:IssueDate"/></cbc:IssueDate></xsl:if>
			</cac:InvoiceDocumentReference>
		</cac:BillingReference>
	</xsl:template>

	<!--  ContractDocumentReference template -->
	<xsl:template match="cac:ContractDocumentReference" mode="creditnote">
		<cac:ContractDocumentReference>
			<cbc:ID>
				<xsl:value-of select="cbc:ID"/>
			</cbc:ID>
		</cac:ContractDocumentReference>
	</xsl:template>

	<!-- AdditionalDocumentReference template, allows negative amounts -->
	<xsl:template name="AdditionalDocumentReferenceCreditNote">
		<cac:AdditionalDocumentReference>
			 <cbc:ID><xsl:value-of select="//cbc:ID"/></cbc:ID>
			 <cbc:DocumentTypeCode listAgencyName="ERST">PEPPOLBIS32OIOUBL</cbc:DocumentTypeCode>
		</cac:AdditionalDocumentReference>
	</xsl:template>

	<!--  AdditionalDocumentReference template -->
	<xsl:template match="cac:AdditionalDocumentReference" mode="creditnote">
		<xsl:variable name="format" select="cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode"/>
		<xsl:variable name="mimeCode">
			<xsl:choose>
				<xsl:when test="cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode ='application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'"><xsl:value-of select="'application/vnd.ms-excel'"/></xsl:when>
				<xsl:when test="cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode ='application/vnd.oasis.opendocument.spreadsheet'"><xsl:value-of select="'application/vnd.ms-excel'"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="DocumentTypeDefault" select="'N/A'"></xsl:variable>
		<xsl:variable name="DocumentType">
			<xsl:choose>
				<xsl:when test="cbc:DocumentTypeCode = '130'"><xsl:value-of select="'Invoiced object identifier'"/></xsl:when>
				<xsl:when test="string(cbc:DocumentDescription) and string(cbc:DocumentType)"><xsl:value-of select="cbc:DocumentType"/>&amp;<xsl:value-of select="cbc:DocumentDescription"/></xsl:when>
				<xsl:when test="string(cbc:DocumentDescription) and not(cbc:DocumentType)"><xsl:value-of select="cbc:DocumentDescription"/></xsl:when>
				<xsl:when test="string(cbc:DocumentType) and not(cbc:DocumentDescription)"><xsl:value-of select="cbc:DocumentType"/></xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="DocumentTypeCode">
			<xsl:choose>
				<xsl:when test="string(cbc:DocumentTypeCode) and string(cbc:ID/@schemeID)"><xsl:value-of select="cbc:DocumentTypeCode"/><xsl:value-of select="cbc:ID/@schemeID"/></xsl:when>
				<xsl:when test="string(cbc:DocumentTypeCode) and not(cbc:ID/@schemeID)"><xsl:value-of select="cbc:DocumentTypeCode"/></xsl:when>
				<xsl:when test="string(cbc:ID/@schemeID) and not(cbc:DocumentTypeCode)"><xsl:value-of select="cbc:ID/@schemeID"/></xsl:when>
			</xsl:choose>
		</xsl:variable>
	
		<cac:AdditionalDocumentReference>
			<cbc:ID>
				<xsl:value-of select="cbc:ID"/>
			</cbc:ID>
			<xsl:choose>
			
				<!--Deciding DocumentTypeCode-->
				<xsl:when test="cbc:ID/@schemeID">
					<cbc:DocumentTypeCode><xsl:value-of select="cbc:ID/@schemeID"/></cbc:DocumentTypeCode>
				</xsl:when>
				<xsl:when test="$DocumentTypeCode != '' and $DocumentType = ''">
					<cbc:DocumentTypeCode><xsl:value-of select="$DocumentTypeCode"/></cbc:DocumentTypeCode>
				</xsl:when>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="$DocumentType">
					<cbc:DocumentType><xsl:value-of select="$DocumentType"/></cbc:DocumentType>
				</xsl:when>
			</xsl:choose>
			
			
				
				<!--<xsl:when test="$DocumentTypeCode = '' and $DocumentType != ''">
					<cbc:DocumentType><xsl:value-of select="$DocumentType"/></cbc:DocumentType>
				</xsl:when>
				<xsl:when test="$DocumentTypeCode != '' and $DocumentType != ''">
					<cbc:DocumentType><xsl:value-of select="$DocumentType"/>&amp;<xsl:value-of select="$DocumentTypeCode"/></cbc:DocumentType>
				</xsl:when>
				<xsl:otherwise>
					<cbc:DocumentType><xsl:value-of select="$DocumentTypeDefault"/></cbc:DocumentType>
				</xsl:otherwise>-->
				
				
				<!--  Attachment template -->
				<xsl:if test="cac:Attachment">
				
					<cac:Attachment>
					
					<!--Inserting valid mimeCode if it is one of the accepted codes in OIOUBL-->
					<xsl:choose>
						<xsl:when test="$mimeCode = 'application/pdf' or $mimeCode = 'image/png' or $mimeCode = 'image/jpeg' or $mimeCode = 'text/csv'">
							<cbc:EmbeddedDocumentBinaryObject mimeCode="{$mimeCode}" filename="{cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename}"><xsl:value-of select="cac:Attachment/cbc:EmbeddedDocumentBinaryObject"/></cbc:EmbeddedDocumentBinaryObject>
						</xsl:when>
						<xsl:when test="$mimeCode = 'application/vnd.ms-excel'">
							<cbc:EmbeddedDocumentBinaryObject format="{$format}"  mimeCode="{$mimeCode}" filename="{cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename}"><xsl:value-of select="cac:Attachment/cbc:EmbeddedDocumentBinaryObject"/></cbc:EmbeddedDocumentBinaryObject>
						</xsl:when>
					</xsl:choose>

					
					<!--Inserting External reference if it is present in BIS3.0-->
					<xsl:if test="cac:Attachment/cac:ExternalReference/cbc:URI">
						<cac:ExternalReference>
							<cbc:URI><xsl:value-of select="cac:Attachment/cac:ExternalReference/cbc:URI"/></cbc:URI>
						</cac:ExternalReference>
					</xsl:if>
				</cac:Attachment>
				</xsl:if>
			</cac:AdditionalDocumentReference>
		</xsl:template>

	
	<xsl:template name="ProjectReferenceIDCreditNote">
	<xsl:variable name="DefaultZZZ" select="'ZZZ'"/>
		<cac:AdditionalDocumentReference>
			<cbc:ID><xsl:value-of select="cac:ProjectReference/cbc:ID"/></cbc:ID>
			<cbc:DocumentTypeCode><xsl:value-of select="$DefaultZZZ"/></cbc:DocumentTypeCode>
		</cac:AdditionalDocumentReference>
	</xsl:template>


	
	<!--  ReceiptDocumentReference template -->
	<xsl:template match="cac:ReceiptDocumentReference" mode="creditnote">
		<cac:ReceiptDocumentReference>
			<cbc:ID><xsl:value-of select="cbc:ID"/></cbc:ID>
		</cac:ReceiptDocumentReference>
	</xsl:template>

<!--  DespatchDocumentReference template -->
	<xsl:template match="cac:DespatchDocumentReference" mode="creditnote">
		<cac:DespatchDocumentReference>
			<cbc:ID><xsl:value-of select="cbc:ID"/></cbc:ID>
		</cac:DespatchDocumentReference>
	</xsl:template>
	
	<!--  OriginatorDocumentReference template -->
	<xsl:template match="cac:OriginatorDocumentReference" mode="creditnote">
		<cac:OriginatorDocumentReference>
			<cbc:ID><xsl:value-of select="cbc:ID"/></cbc:ID>
		</cac:OriginatorDocumentReference>
	</xsl:template>


	<!--  ExternalReference template -->
	<xsl:template match="cac:ExternalReference" mode="creditnote">
		<cac:ExternalReference>
			<xsl:if test="string(cbc:URI)"><cbc:URI><xsl:value-of select="cbc:URI"/></cbc:URI></xsl:if>
		</cac:ExternalReference>
	</xsl:template>

	<!--  AccountingSupplierParty template -->
	<xsl:template match="cac:AccountingSupplierParty" mode="creditnote">
		<cac:AccountingSupplierParty>
			<cac:Party>
				<xsl:call-template name="EndpointIDSupplierCreditNote"/>
				<xsl:apply-templates select="cac:Party/cac:PartyIdentification" mode="creditnote"/>
				<xsl:call-template name="PartyNameCreditNote"/>
				<xsl:apply-templates select="cac:Party/cac:PostalAddress" mode="creditnote"/>
				<xsl:apply-templates select="cac:Party/cac:PartyTaxScheme" mode="creditnote"/>
				<xsl:call-template name="SupplierPartyLegalEntityCreditNote"/>
				<xsl:apply-templates select="cac:Party/cac:Contact" mode="creditnote"/>
				<!--<xsl:apply-templates select="cac:Party/cac:Person"/>-->
			</cac:Party>
		</cac:AccountingSupplierParty>
	</xsl:template>

	<!--  AccountingCustomerParty template -->
	<xsl:template match="cac:AccountingCustomerParty" mode="creditnote">
		<cac:AccountingCustomerParty>
			<cac:Party>
				<xsl:call-template name="EndpointIDCustomerCreditNote"/>
				<xsl:apply-templates select="cac:Party/cac:PartyIdentification" mode="creditnote"/>
				<xsl:call-template name="PartyNameCreditNote"/>
				<xsl:apply-templates select="cac:Party/cac:PostalAddress" mode="creditnote"/>
				<xsl:apply-templates select="cac:Party/cac:PartyTaxScheme" mode="creditnote"/>
				<xsl:apply-templates select="cac:Party/cac:PartyLegalEntity" mode="creditnote"/>
				<xsl:call-template name="CustomerContactCreditNote"/>
				<xsl:apply-templates select="cac:Party/cac:Person" mode="creditnote"/>
			</cac:Party>
		</cac:AccountingCustomerParty>
	</xsl:template>

	<!--  PayeeParty template -->
	<xsl:template match="cac:PayeeParty" mode="creditnote">
		<cac:PayeeParty>
			<xsl:apply-templates select="cac:PartyIdentification" mode="creditnote"/>
			<xsl:call-template name="PartyNameCreditNote"/>
			<xsl:apply-templates select="cac:PartyLegalEntity" mode="creditnote"/>
		</cac:PayeeParty>
	</xsl:template>

<xsl:template match="cac:TaxRepresentativeParty" mode="creditnote">
		<cac:TaxRepresentativeParty>
			<xsl:apply-templates select="cac:PartyName" mode="creditnote"/>
			<xsl:apply-templates select="cac:PostalAddress" mode="creditnote"/>
			<xsl:apply-templates select="cac:PartyTaxScheme" mode="creditnote"/>
		</cac:TaxRepresentativeParty>
	</xsl:template>



	<!--  PartyIdentification template -->
	<xsl:template match="cac:PartyIdentification" mode="creditnote">
		<xsl:variable name="OIOUBLids" select="',DUNS,GLN,IBAN,ISO 6523,ZZZ,DK:CPR,DK:CVR,DK:P,DK:SE,DK:TELEFON,FI:ORGNR,IS:KT,IS:VSKNR,NO:EFO,NO:NOBB,NO:NODI,NO:ORGNR,NO:VAT,SE:ORGNR,SE:VAT,FR:SIRET,FI:OVT,IT:FTI,IT:SIA,IT:SECETI,IT:VAT,IT:CF,HU:VAT,EU:VAT,EU:REID,AT:VAT,AT:GOV,AT:CID,IS:KT,AT:KUR,ES:VAT,IT:IPA,AD:VAT,AL:VAT,BA:VAT,BE:VAT,BG:VAT,CH:VAT,CY:VAT,CZ:VAT,DE:VAT,EE:VAT,GB:VAT,GR:VAT,HR:VAT,IE:VAT,LI:VAT,LT:VAT,LU:VAT,LV:VAT,MC:VAT,ME:VAT,MK:VAT,MT:VAT,NL:VAT,PL:VAT,PT:VAT,RO:VAT,RS:VAT,SI:VAT,SK:VAT,SM:VAT,TR:VAT,VA:VAT,'"/>
		<xsl:variable name="t1" select="cbc:ID/@schemeID"/>
		<xsl:variable name="t2" select="cbc:ID"/>
		<xsl:variable name="ft1">
			<xsl:choose>
				<xsl:when test="$t1 = '0002'">FR:SIRENE</xsl:when>
				<xsl:when test="$t1 = '0007'">SE:ORGNR</xsl:when>
				<xsl:when test="$t1 = '0009'">FR:SIRET</xsl:when>
				<xsl:when test="$t1 = '0037'">FI:OVT</xsl:when>
				<xsl:when test="$t1 = '0060'">DUNS</xsl:when>
				<xsl:when test="$t1 = '0088'">GLN</xsl:when>
				<xsl:when test="$t1 = '0096'">DK:P</xsl:when>
				<xsl:when test="$t1 = '0097'">IT:FTI</xsl:when>
				<xsl:when test="$t1 = '0106'">NL:KVK</xsl:when>
				<xsl:when test="$t1 = '0135'">IT:SIA</xsl:when>
				<xsl:when test="$t1 = '0142'">IT:SECETI</xsl:when>
				<xsl:when test="$t1 = '0184' or $t1 = '9902'">DK:CVR</xsl:when>
				<xsl:when test="$t1 = 'SEPA'">SEPA</xsl:when>
				<xsl:when test="$t1 = '0192' or $t1 = '9908'">NO:ORGNR</xsl:when>
				<xsl:when test="$t1 = '0198'">DK:SE</xsl:when>			
				<xsl:otherwise><xsl:value-of select="'ZZZ'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="ft2" select="$t2"/>
		<cac:PartyIdentification>
			<cbc:ID schemeID="{$ft1}"><xsl:value-of select="$ft2"/></cbc:ID>
		</cac:PartyIdentification>
	</xsl:template>

	<!--  PartyName template -->
	<xsl:template match="cac:PartyName" mode="creditnote">
		<cac:PartyName>
			<cbc:Name><xsl:value-of select="cbc:Name"/></cbc:Name>
		</cac:PartyName>
	</xsl:template>
	
	<!--  PartyNameSupplier template -->
	<xsl:template name="PartyNameCreditNote">
	<xsl:variable name="PartyName" select="cac:Party/cac:PartyName/cbc:Name"/>
	<xsl:variable name="PartyName2" select="cac:Party/cac:PartyLegalEntity/cbc:RegistrationName"/>
		<xsl:choose>
			<xsl:when test="string($PartyName)">
			<cac:PartyName>
			<cbc:Name><xsl:value-of select="$PartyName"/></cbc:Name>
		</cac:PartyName>
		</xsl:when>
		<xsl:when test="string($PartyName2) and not($PartyName)">
			<cac:PartyName>
			<cbc:Name><xsl:value-of select="$PartyName2"/></cbc:Name>
		</cac:PartyName>
		</xsl:when>		
		</xsl:choose>
	</xsl:template>
	

	<!--  PostalAddress template -->
	<xsl:template match="cac:PostalAddress" mode="creditnote">
		<cac:PostalAddress>
			<xsl:call-template name="AddressCreditNote"></xsl:call-template>
		</cac:PostalAddress>
	</xsl:template>

	<!--  Address template -->
	<xsl:template match="cac:Address" mode="creditnote">
		<cac:Address>
			<xsl:call-template name="AddressCreditNote"></xsl:call-template>
		</cac:Address>
	</xsl:template>

	<!--  PartyTaxScheme template -->
	<xsl:template match="cac:PartyTaxScheme" mode="creditnote">
		<xsl:variable name="OIOUBLids" select="',DK:SE,ZZZ,'"/>
		<xsl:variable name="t1" select="cbc:CompanyID/@schemeID"/>
		<xsl:variable name="t2" select="cbc:CompanyID"/>
		<xsl:variable name="t2Test" select="substring(cbc:CompanyID, 1,2)"/>
		<xsl:variable name="ExemptionReason" select="cbc:ExemptionReason"/>
		<xsl:variable name="ft1">
			<xsl:choose>
				<xsl:when test="$t2Test = 'DK'"><xsl:value-of select="'DK:SE'"/></xsl:when>
				<xsl:when test="contains($OIOUBLids, concat(',',$t1,','))"><xsl:value-of select="$t1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="'ZZZ'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="ft2" select="$t2"/>
		<cac:PartyTaxScheme>
			<cbc:CompanyID schemeID="{$ft1}"><xsl:value-of select="$ft2"/></cbc:CompanyID>
			<xsl:if test="string($ExemptionReason)"><cbc:ExemptionReason><xsl:value-of select="$ExemptionReason"></xsl:value-of></cbc:ExemptionReason></xsl:if>
			<xsl:apply-templates select="cac:TaxScheme" mode="creditnote"/>
		</cac:PartyTaxScheme>
	</xsl:template>

	<!--  PartyLegalEntity template -->
	<xsl:template match="cac:PartyLegalEntity" mode="creditnote">
		<xsl:variable name="OIOUBLids" select="',DK:CVR,DK:CPR,ZZZ,'"/>
		<xsl:variable name="t1" select="cbc:CompanyID/@schemeID"/>
		<xsl:variable name="t2" select="cbc:CompanyID"/>
		<xsl:variable name="ft1">
			<xsl:choose>
				<xsl:when test="$t1 = '0184'">DK:CVR</xsl:when>
				<xsl:otherwise><xsl:value-of select="'ZZZ'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="ft2" select="$t2"/>
		<cac:PartyLegalEntity>
			<xsl:if test="string(cbc:RegistrationName)"><cbc:RegistrationName><xsl:value-of select="cbc:RegistrationName"/></cbc:RegistrationName></xsl:if>
			<cbc:CompanyID schemeID="{$ft1}"><xsl:value-of select="$ft2"/></cbc:CompanyID>

		</cac:PartyLegalEntity>
	</xsl:template>

	<!--  Contact template -->
	<xsl:template match="cac:Contact" mode="creditnote">
		<cac:Contact>
			<xsl:if test="string(cbc:ID)"><cbc:ID><xsl:value-of select="cbc:ID"/></cbc:ID></xsl:if>
			<xsl:if test="string(cbc:Name)"><cbc:Name><xsl:value-of select="cbc:Name"/></cbc:Name></xsl:if>
			<xsl:if test="string(cbc:Telephone)"><cbc:Telephone><xsl:value-of select="cbc:Telephone"/></cbc:Telephone></xsl:if>
			<xsl:if test="string(cbc:Telefax)"><cbc:Telefax><xsl:value-of select="cbc:Telefax"/></cbc:Telefax></xsl:if>
			<xsl:if test="string(cbc:ElectronicMail)"><cbc:ElectronicMail><xsl:value-of select="cbc:ElectronicMail"/></cbc:ElectronicMail></xsl:if>
		</cac:Contact>
	</xsl:template>


	<!--  AllowanceCharge template -->
	<xsl:template match="cac:AllowanceCharge" mode="creditnote">
<!--		<xsl:variable name="t1"	select="format-number(cbc:Amount, '0.00')"/>
		<xsl:variable name="t2"	select="format-number(cbc:BaseAmount, '0.00')"/>-->
		<xsl:variable name="t1_raw" >
			<xsl:choose>
				<xsl:when test="number(cbc:Amount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:Amount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:Amount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="t1"	select="format-number($t1_raw, '0.00')"/>
		<xsl:variable name="t2_raw" >
			<xsl:choose>
				<xsl:when test="number(cbc:BaseAmount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:BaseAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:BaseAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="t2"	select="format-number($t2_raw, '0.00')"/>
		<xsl:variable name="MultiplierFactorNumeric" select="cbc:MultiplierFactorNumeric div 100"/>
		<cac:AllowanceCharge>
			<cbc:ChargeIndicator><xsl:value-of select="cbc:ChargeIndicator"/></cbc:ChargeIndicator>
			<xsl:if test="string(cbc:AllowanceChargeReasonCode)"><cbc:AllowanceChargeReasonCode><xsl:value-of select="cbc:AllowanceChargeReasonCode"/></cbc:AllowanceChargeReasonCode></xsl:if>
			<xsl:if test="string(cbc:AllowanceChargeReason)"><cbc:AllowanceChargeReason><xsl:value-of select="cbc:AllowanceChargeReason"/></cbc:AllowanceChargeReason></xsl:if>
			<xsl:if test="string(cbc:MultiplierFactorNumeric)"><cbc:MultiplierFactorNumeric><xsl:value-of select="$MultiplierFactorNumeric"/></cbc:MultiplierFactorNumeric></xsl:if>
			<cbc:Amount currencyID="{cbc:Amount/@currencyID}"><xsl:value-of select="$t1"/></cbc:Amount>
			<xsl:if test="string(cbc:BaseAmount)"><cbc:BaseAmount currencyID="{cbc:BaseAmount/@currencyID}"><xsl:value-of select="$t2"/></cbc:BaseAmount></xsl:if>
			<xsl:apply-templates select="cac:TaxCategory" mode="creditnote"/>
		</cac:AllowanceCharge>
	</xsl:template>
	
	<!--TaxExchangeRate template-->
	<xsl:template match="cac:TaxExchangeRate" mode="creditnote">
		<xsl:variable name="SourceCurrency"	select="cbc:SourceCurrencyCode"/>
		<xsl:variable name="TargetCurrency"	select="cbc:TargetCurrencyCode"/>
		<xsl:variable name="CalcRate"	select="cbc:CalculationRate"/>
		<xsl:variable name="MathCode"	select="cbc:MathematicOperatorCode"/>
		<xsl:variable name="Date"	select="cbc:Date"/>
		<cac:TaxExchangeRate>
			<xsl:if test="string($SourceCurrency)"><cbc:SourceCurrencyCode><xsl:value-of select="$SourceCurrency"></xsl:value-of></cbc:SourceCurrencyCode></xsl:if>
			<xsl:if test="string($TargetCurrency)"><cbc:TargetCurrencyCode><xsl:value-of select="$TargetCurrency"></xsl:value-of></cbc:TargetCurrencyCode></xsl:if>
			<xsl:if test="string($CalcRate)"><cbc:CalculationRate><xsl:value-of select="$CalcRate"></xsl:value-of></cbc:CalculationRate></xsl:if>
			<xsl:if test="string($CalcRate)"><cbc:MathematicOperatorCode><xsl:value-of select="$CalcRate"></xsl:value-of></cbc:MathematicOperatorCode></xsl:if>
			<xsl:if test="string($Date)"><cbc:Date><xsl:value-of select="$Date"></xsl:value-of></cbc:Date></xsl:if>
		</cac:TaxExchangeRate>
	</xsl:template>
	
	<!--  TaxTotal template -->
	<xsl:template match="cac:TaxTotal" mode="creditnote">
		<xsl:param name="RoundingAmount"/>
<!--		<xsl:variable name="t1"	select="format-number(cbc:TaxAmount, '0.00')"/>-->
		<xsl:variable name="t2"	select="cbc:TaxAmount/@currencyID"/>
		<xsl:variable name="t2_2"	select="cac:TaxSubtotal/cbc:TaxAmount/@currencyID"/>
		<xsl:variable name="t3"	select="../cbc:DocumentCurrencyCode"/>
		<xsl:variable name="TaxTotalTaxAmount" select="cbc:TaxAmount"/>
<!--		<xsl:variable name="SummeringTaxSubtotalTaxAmount" select="format-number(sum(cac:TaxSubtotal/cbc:TaxAmount), '0.00')"/>-->
		<xsl:variable name="t1_raw" >
			<xsl:choose>
			<xsl:when test="number(cbc:TaxAmount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:TaxAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:TaxAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="t1"	select="format-number($t1_raw, '0.00')"/>
		<xsl:variable name="SummeringTaxSubtotalTaxAmount_raw" >
			<xsl:choose>
				<xsl:when test="$Converted"><xsl:value-of select="sum(cac:TaxSubtotal/cbc:TaxAmount) * (-1)"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="sum(cac:TaxSubtotal/cbc:TaxAmount)"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="SummeringTaxSubtotalTaxAmount"	select="format-number($SummeringTaxSubtotalTaxAmount_raw, '0.00')"/>
		
	<xsl:if test="string($t2_2) = string($t3)">
		<cac:TaxTotal>
		<xsl:choose>
			<xsl:when test="string($TaxTotalTaxAmount)">
				<cbc:TaxAmount currencyID="{$t2}"><xsl:value-of select="$t1"/></cbc:TaxAmount>
			</xsl:when>
			<xsl:otherwise>
				<cbc:TaxAmount currencyID="{../cbc:DocumentCurrencyCode}"><xsl:value-of select="$SummeringTaxSubtotalTaxAmount"/></cbc:TaxAmount>
			</xsl:otherwise>
		</xsl:choose>
			<!--<xsl:if test="number($RoundingAmount)"><cbc:RoundingAmount currencyID="{$t2}"><xsl:value-of select="$RoundingAmount"/></cbc:RoundingAmount></xsl:if>-->
			<xsl:apply-templates select="cac:TaxSubtotal" mode="creditnote"/>
		</cac:TaxTotal>
	</xsl:if>
	</xsl:template>

	<xsl:template match="cac:TaxSubtotal" mode="creditnote">
		<!--<xsl:variable name="t1"	select="format-number(cbc:TaxableAmount, '0.00')"/>-->
		<!--<xsl:variable name="t2"	select="format-number(cbc:TaxAmount, '0.00')"/>-->
		<xsl:variable name="t3"	select="../../cbc:DocumentCurrencyCode"/>
		<xsl:variable name="t4"	select="../../cbc:TaxCurrencyCode"/>
		<xsl:variable name="t1_raw" >
			<xsl:choose>
				<xsl:when test="number(cbc:TaxableAmount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:TaxableAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:TaxableAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="t1"	select="format-number($t1_raw, '0.00')"/>
		<xsl:variable name="t2_raw" >
			<xsl:choose>
				<xsl:when test="number(cbc:TaxAmount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:TaxAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:TaxAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="t2"	select="format-number($t2_raw, '0.00')"/>
 		<cac:TaxSubtotal>
			<cbc:TaxableAmount currencyID="{cbc:TaxableAmount/@currencyID}"><xsl:value-of select="$t1"/></cbc:TaxableAmount>
			<cbc:TaxAmount currencyID="{cbc:TaxAmount/@currencyID}"><xsl:value-of select="$t2"/></cbc:TaxAmount>
			<xsl:if test="cac:TaxCategory/cbc:ID ='S'">
			<xsl:for-each select="//cac:TaxTotal[cbc:TaxAmount/@currencyID != $t3]">
			<!-- [cbc:TaxAmount/@CurrencyID != $t3] -->
			<xsl:choose>
				<xsl:when test="number(cbc:TaxAmount) = 0 "><cbc:TransactionCurrencyTaxAmount currencyID="{$t4}">0.00</cbc:TransactionCurrencyTaxAmount></xsl:when>
				<xsl:when test="$Converted"><cbc:TransactionCurrencyTaxAmount currencyID="{$t4}"><xsl:value-of select="cbc:TaxAmount * -1"/></cbc:TransactionCurrencyTaxAmount>
				</xsl:when>
				<xsl:otherwise><cbc:TransactionCurrencyTaxAmount currencyID="{$t4}"><xsl:value-of select="cbc:TaxAmount"></xsl:value-of></cbc:TransactionCurrencyTaxAmount></xsl:otherwise>
			</xsl:choose>
			</xsl:for-each>
			</xsl:if>
			<xsl:apply-templates select="cac:TaxCategory" mode="creditnote"/>
		</cac:TaxSubtotal>
	</xsl:template>

	<!--  TaxCategory template -->
	<xsl:template match="cac:TaxCategory" mode="creditnote">
		<xsl:variable name="t1" select="cbc:ID"/>
		<xsl:variable name="deufaul0" select="'0'"/>
		<xsl:variable name="ft1">
			<xsl:call-template name="TaxCategoryIDConvertCreditNote">
				<xsl:with-param name="p1"><xsl:value-of select="$t1"/></xsl:with-param>
			</xsl:call-template>								
		</xsl:variable>
		<cac:TaxCategory>
			<cbc:ID schemeAgencyID="320" schemeID="urn:oioubl:id:taxcategoryid-1.2"><xsl:value-of select="$ft1"/></cbc:ID>
			<xsl:choose>
				<xsl:when test="$t1 = 'O'">
						<cbc:Percent><xsl:value-of select="$deufaul0"/></cbc:Percent>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="string(cbc:Percent)"><cbc:Percent><xsl:value-of select="cbc:Percent"/></cbc:Percent></xsl:if>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="string(cbc:TaxExemptionReasonCode)"><cbc:TaxExemptionReasonCode><xsl:value-of select="cbc:TaxExemptionReasonCode"/></cbc:TaxExemptionReasonCode></xsl:if>
			<xsl:if test="string(cbc:TaxExemptionReason)"><cbc:TaxExemptionReason><xsl:value-of select="cbc:TaxExemptionReason"/></cbc:TaxExemptionReason></xsl:if>
			<xsl:apply-templates select="cac:TaxScheme" mode="creditnote"/>
		</cac:TaxCategory>
	</xsl:template>

	<!--  TaxScheme template -->
	<xsl:template match="cac:TaxScheme" mode="creditnote">
		<xsl:variable name="t1" select="cbc:ID"/>
		<xsl:variable name="ft1">
			<xsl:call-template name="TaxSchemeIDConvertCreditNote">
				<xsl:with-param name="p1"><xsl:value-of select="$t1"/></xsl:with-param>
				<xsl:with-param name="SupplierParty"><xsl:value-of select="//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/></xsl:with-param>
			</xsl:call-template>								
		</xsl:variable>
		<xsl:variable name="ft2">
			<xsl:call-template name="TaxSchemeNameConvertCreditNote">
				<xsl:with-param name="p1"><xsl:value-of select="$ft1"/></xsl:with-param>
			</xsl:call-template>								
		</xsl:variable>
		<cac:TaxScheme>
			<cbc:ID schemeAgencyID="320" schemeID="urn:oioubl:id:taxschemeid-1.5"><xsl:value-of select="$ft1"/></cbc:ID>
			<cbc:Name><xsl:value-of select="$ft2"/></cbc:Name>
			<!--<xsl:if test="$ft1 != '63'"><cbc:TaxTypeCode listAgencyID="320" listID="urn:oioubl:codelist:taxtypecode-1.1"><xsl:value-of select="'StandardRated'"/></cbc:TaxTypeCode></xsl:if>-->
		</cac:TaxScheme>
	</xsl:template>

	<!--  LegalMonetaryTotal template -->
	<xsl:template match="cac:LegalMonetaryTotal" mode="creditnote">
<!--		<xsl:variable name="t1"	select="format-number(cbc:LineExtensionAmount, '0.00')"/>-->
<!--		<xsl:variable name="t2"	select="format-number(cbc:TaxExclusiveAmount, '0.00')"/>-->
<!--		<xsl:variable name="t3"	select="format-number(cbc:TaxInclusiveAmount, '0.00')"/>
		<xsl:variable name="t4"	select="format-number(cbc:AllowanceTotalAmount, '0.00')"/>
		<xsl:variable name="t5"	select="format-number(cbc:ChargeTotalAmount, '0.00')"/>
		<xsl:variable name="t6"	select="format-number(cbc:PrepaidAmount, '0.00')"/>
		<xsl:variable name="t7"	select="format-number(cbc:PayableRoundingAmount, '0.00')"/>-->
		<xsl:variable name="t1_raw" >
			<xsl:choose>
				<xsl:when test="number(cbc:LineExtensionAmount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:LineExtensionAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:LineExtensionAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="t1"	select="format-number($t1_raw, '0.00')"/>
		<xsl:variable name="t2_raw" >
			<xsl:choose>
				<xsl:when test="number(cbc:TaxExclusiveAmount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:TaxExclusiveAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:TaxExclusiveAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="t2"	select="format-number($t2_raw, '0.00')"/>
		<xsl:variable name="t3_raw" >
			<xsl:choose>
				<xsl:when test="number(cbc:TaxInclusiveAmount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:TaxInclusiveAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:TaxInclusiveAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!--<xsl:variable name="t3"	select="format-number($t3_raw, '0.00')"/>-->
		<xsl:variable name="t4_raw" >
			<xsl:choose>
				<xsl:when test="count(cbc:AllowanceTotalAmount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:AllowanceTotalAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:AllowanceTotalAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="t4"	select="format-number($t4_raw, '0.00')"/>
		<xsl:variable name="t5_raw" >
			<xsl:choose>
				<xsl:when test="count(cbc:ChargeTotalAmount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:ChargeTotalAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:ChargeTotalAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="t5"	select="format-number($t5_raw, '0.00')"/>
		<xsl:variable name="t6_raw" >
			<xsl:choose>
				<xsl:when test="number(cbc:PrepaidAmount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:PrepaidAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:PrepaidAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="t6"	select="format-number($t6_raw, '0.00')"/>
		<xsl:variable name="t7_raw" >
			<xsl:choose>
				<xsl:when test="count(cbc:PayableRoundingAmount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:PayableRoundingAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:PayableRoundingAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="t7"	select="format-number($t7_raw, '0.00')"/>
		<xsl:variable name="t8_raw" >
			<xsl:choose>
				<xsl:when test="number(cbc:PayableAmount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:PayableAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:PayableAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="t8"	select="format-number($t8_raw, '0.00')"/>
		<xsl:variable name="ft2">
			<xsl:choose>
				<xsl:when test="$t2 = 'NaN'"><xsl:value-of select="'0.00'"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="$t2"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="TaxAmount">
			<xsl:choose>
				<xsl:when test="number(../cac:TaxTotal/cbc:TaxAmount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="../cac:TaxTotal/cbc:TaxAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="../cac:TaxTotal/cbc:TaxAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="t3">
			<xsl:choose>
				<xsl:when test="$Converted"><xsl:value-of select="$t1_raw + $TaxAmount - $t4_raw + $t5_raw + $t7_raw * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="$t1_raw + $TaxAmount - $t4_raw + $t5_raw + $t7_raw"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		
		<!--<xsl:variable name="t3"	select="format-number($t1_raw + $TaxAmount - $t4_raw + $t5_raw + $t7_raw, '0.00')"/>-->
		
		<xsl:variable name="ft3">
			<xsl:choose>
				<xsl:when test="$t3 = 'NaN'"><xsl:value-of select="'0.00'"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="$t3"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="ft7">
			<xsl:choose>
				<xsl:when test="$t7 = 'NaN'"><xsl:value-of select="'0.00'"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="$t7"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
				
		<!--  LegalMonetaryTotal template -->
		<cac:LegalMonetaryTotal>
			<cbc:LineExtensionAmount currencyID="{cbc:LineExtensionAmount/@currencyID}"><xsl:value-of select="$t1"/></cbc:LineExtensionAmount>
			<cbc:TaxExclusiveAmount currencyID="{cbc:TaxExclusiveAmount/@currencyID}"><xsl:value-of select="format-number($TaxAmount, '0.00')"/></cbc:TaxExclusiveAmount>
			<xsl:if test="string(cbc:TaxInclusiveAmount)"><cbc:TaxInclusiveAmount currencyID="{cbc:TaxInclusiveAmount/@currencyID}"><xsl:value-of select="format-number($t3, '0.00')"/></cbc:TaxInclusiveAmount></xsl:if>
			<xsl:if test="cbc:AllowanceTotalAmount != '0.00'"><cbc:AllowanceTotalAmount currencyID="{cbc:AllowanceTotalAmount/@currencyID}"><xsl:value-of select="$t4"/></cbc:AllowanceTotalAmount></xsl:if>
			<xsl:if test="cbc:ChargeTotalAmount != '0.00'"><cbc:ChargeTotalAmount currencyID="{cbc:ChargeTotalAmount/@currencyID}"><xsl:value-of select="$t5"/></cbc:ChargeTotalAmount></xsl:if>
			<xsl:if test="string(cbc:PrepaidAmount)"><cbc:PrepaidAmount currencyID="{cbc:PrepaidAmount/@currencyID}"><xsl:value-of select="$t6"/></cbc:PrepaidAmount></xsl:if>
			<xsl:if test="cbc:PayableRoundingAmount != '0.00'"><cbc:PayableRoundingAmount currencyID="{cbc:PayableRoundingAmount/@currencyID}"><xsl:value-of select="$t7"/></cbc:PayableRoundingAmount></xsl:if>
			<cbc:PayableAmount currencyID="{cbc:PayableAmount/@currencyID}"><xsl:value-of select="$t8"/></cbc:PayableAmount>
		</cac:LegalMonetaryTotal>
	</xsl:template>

		<!-- CreditNoteLine template -->
		
	<xsl:template name="CreditNoteLine">
		<xsl:variable name="ALCFlag">
			<xsl:choose>
				<xsl:when test="count(cac:AllowanceCharge) &gt; 0">true</xsl:when>
				<xsl:otherwise><xsl:value-of select="'false'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="BaseQFlag">
			<xsl:choose>
				<xsl:when test="string(cac:Price/cbc:BaseQuantity) and cac:Price/cbc:BaseQuantity != 1">true</xsl:when>
				<xsl:otherwise><xsl:value-of select="'false'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="fQuantity">
			<xsl:choose>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:InvoicedQuantity"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:CreditedQuantity"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="fUnitCode">
			<xsl:choose>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:InvoicedQuantity/@unitCode"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:CreditedQuantity/@unitCode"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
				<xsl:variable name="TestUnitForX">
			<xsl:value-of select="substring($fUnitCode, 1,1)"/>
		</xsl:variable>
		<xsl:variable name="OIOUBLUnits" select="',04,05,08,10,11,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,40,41,43,44,45,46,47,48,53,54,56,57,58,59,60,61,62,63,64,66,69,71,72,73,74,76,77,78,80,81,84,85,87,89,90,91,92,93,94,95,96,97,98,1A,1B,1C,1D,1E,1F,1G,1H,1I,1J,1K,1L,1M,1X,2A,2B,2C,2I,2J,2K,2L,2M,2N,2P,2Q,2R,2U,2V,2W,2X,2Y,2Z,3B,3C,3E,3G,3H,3I,4A,4B,4C,4E,4G,4H,4K,4L,4M,4N,4O,4P,4Q,4R,4T,4U,4W,4X,5A,5B,5C,5E,5F,5G,5H,5I,5J,5K,5P,5Q,A1,A10,A11,A12,A13,A14,A15,A16,A17,A18,A19,A2,A20,A21,A22,A23,A24,A25,A26,A27,A28,A29,A3,A30,A31,A32,A33,A34,A35,A36,A37,A38,A39,A4,A40,A41,A42,A43,A44,A45,A47,A48,A49,A5,A50,A51,A52,A53,A54,A55,A56,A57,A58,A6,A60,A61,A62,A63,A64,A65,A66,A67,A68,A69,A7,A70,A71,A73,A74,A75,A76,A77,A78,A79,A8,A80,A81,A82,A83,A84,A85,A86,A87,A88,A89,A9,A90,A91,A93,A94,A95,A96,A97,A98,AA,AB,ACR,AD,AE,AH,AI,AJ,AK,AL,AM,AMH,AMP,ANN,AP,APZ,AQ,AR,ARE,AS,ASM,ASU,ATM,ATT,AV,AW,AY,AZ,B0,B1,B11,B12,B13,B14,B15,B16,B18,B2,B20,B21,B22,B23,B24,B25,B26,B27,B28,B29,B3,B31,B32,B33,B34,B35,B36,B37,B38,B39,B4,B40,B41,B42,B43,B44,B45,B46,B47,B48,B49,B5,B50,B51,B52,B53,B54,B55,B56,B57,B58,B59,B6,B60,B61,B62,B63,B64,B65,B66,B67,B69,B7,B70,B71,B72,B73,B74,B75,B76,B77,B78,B79,B8,B81,B83,B84,B85,B86,B87,B88,B89,B9,B90,B91,B92,B93,B94,B95,B96,B97,B98,B99,BAR,BB,BD,BE,BFT,BG,BH,BHP,BIL,BJ,BK,BL,BLD,BLL,BO,BP,BQL,BR,BT,BTU,BUA,BUI,BW,BX,BZ,C0,C1,C10,C11,C12,C13,C14,C15,C16,C17,C18,C19,C2,C20,C22,C23,C24,C25,C26,C27,C28,C29,C3,C30,C31,C32,C33,C34,C35,C36,C38,C39,C4,C40,C41,C42,C43,C44,C45,C46,C47,C48,C49,C5,C50,C51,C52,C53,C54,C55,C56,C57,C58,C59,C6,C60,C61,C62,C63,C64,C65,C66,C67,C68,C69,C7,C70,C71,C72,C73,C75,C76,C77,C78,C8,C80,C81,C82,C83,C84,C85,C86,C87,C88,C89,C9,C90,C91,C92,C93,C94,C95,C96,C97,C98,C99,CA,CCT,CDL,CEL,CEN,CG,CGM,CH,CJ,CK,CKG,CL,CLF,CLT,CMK,CMQ,CMT,CNP,CNT,CO,COU,CQ,CR,CS,CT,CTM,CU,CUR,CV,CWA,CWI,CY,CZ,D1,D10,D12,D13,D14,D15,D16,D17,D18,D19,D2,D20,D21,D22,D23,D24,D25,D26,D27,D28,D29,D30,D31,D32,D33,D34,D35,D37,D38,D39,D40,D41,D42,D43,D44,D45,D46,D47,D48,D49,D5,D50,D51,D52,D53,D54,D55,D56,D57,D58,D59,D6,D60,D61,D62,D63,D64,D65,D66,D67,D69,D7,D70,D71,D72,D73,D74,D75,D76,D77,D79,D8,D80,D81,D82,D83,D85,D86,D87,D88,D89,D9,D90,D91,D92,D93,D94,D95,D96,D97,D98,D99,DAA,DAD,DAY,DB,DC,DD,DE,DEC,DG,DI,DJ,DLT,DMK,DMQ,DMT,DN,DPC,DPR,DPT,DQ,DR,DRA,DRI,DRL,DRM,DS,DT,DTN,DU,DWT,DX,DY,DZN,DZP,E2,E3,E4,E5,EA,EB,EC,EP,EQ,EV,F1,F9,FAH,FAR,FB,FC,FD,FE,FF,FG,FH,FL,FM,FOT,FP,FR,FS,FTK,FTQ,G2,G3,G7,GB,GBQ,GC,GD,GE,GF,GFI,GGR,GH,GIA,GII,GJ,GK,GL,GLD,GLI,GLL,GM,GN,GO,GP,GQ,GRM,GRN,GRO,GRT,GT,GV,GW,GWH,GY,GZ,H1,H2,HA,HAR,HBA,HBX,HC,HD,HE,HF,HGM,HH,HI,HIU,HJ,HK,HL,HLT,HM,HMQ,HMT,HN,HO,HP,HPA,HS,HT,HTZ,HUR,HY,IA,IC,IE,IF,II,IL,IM,INH,INK,INQ,IP,IT,IU,IV,J2,JB,JE,JG,JK,JM,JO,JOU,JR,K1,K2,K3,K5,K6,KA,KB,KBA,KD,KEL,KF,KG,KGM,KGS,KHZ,KI,KJ,KJO,KL,KMH,KMK,KMQ,KNI,KNS,KNT,KO,KPA,KPH,KPO,KPP,KR,KS,KSD,KSH,KT,KTM,KTN,KUR,KVA,KVR,KVT,KW,KWH,KWT,KX,L2,LA,LBR,LBT,LC,LD,LE,LEF,LF,LH,LI,LJ,LK,LM,LN,LO,LP,LPA,LR,LS,LTN,LTR,LUM,LUX,LX,LY,M0,M1,M4,M5,M7,M9,MA,MAL,MAM,MAW,MBE,MBF,MBR,MC,MCU,MD,MF,MGM,MHZ,MIK,MIL,MIN,MIO,MIU,MK,MLD,MLT,MMK,MMQ,MMT,MON,MPA,MQ,MQH,MQS,MSK,MT,MTK,MTQ,MTR,MTS,MV,MVA,MWH,N1,N2,N3,NA,NAR,NB,NBB,NC,NCL,ND,NE,NEW,NF,NG,NH,NI,NIU,NJ,NL,NMI,NMP,NN,NPL,NPR,NPT,NQ,NR,NRL,NT,NTT,NU,NV,NX,NY,OA,OHM,ON,ONZ,OP,OT,OZ,OZA,OZI,P0,P1,P2,P3,P4,P5,P6,P7,P8,P9,PA,PAL,PB,PD,PE,PF,PG,PGL,PI,PK,PL,PM,PN,PO,PQ,PR,PS,PT,PTD,PTI,PTL,PU,PV,PW,PY,PZ,Q3,QA,QAN,QB,QD,QH,QK,QR,QT,QTD,QTI,QTL,QTR,R1,R4,R9,RA,RD,RG,RH,RK,RL,RM,RN,RO,RP,RPM,RPS,RS,RT,RU,S3,S4,S5,S6,S7,S8,SA,SAN,SCO,SCR,SD,SE,SEC,SET,SG,SHT,SIE,SK,SL,SMI,SN,SO,SP,SQ,SR,SS,SST,ST,STI,STN,SV,SW,SX,T0,T1,T3,T4,T5,T6,T7,T8,TA,TAH,TC,TD,TE,TF,TI,TJ,TK,TL,TN,TNE,TP,TPR,TQ,TQD,TR,TRL,TS,TSD,TSH,TT,TU,TV,TW,TY,U1,U2,UA,UB,UC,UD,UE,UF,UH,UM,VA,VI,VLT,VQ,VS,W2,W4,WA,WB,WCD,WE,WEB,WEE,WG,WH,WHR,WI,WM,WR,WSD,WTT,WW,X1,YDK,YDQ,YL,YRD,YT,Z1,Z2,Z3,Z4,Z5,Z6,Z8,ZP,ZZ,'"/>		
		<xsl:variable name="UnitOfMeasure">
			<xsl:choose>
			<!--REmoving X when the unit is prefixed with X-->
				<xsl:when test="$TestUnitForX = 'X'"><xsl:value-of select="substring-after($fUnitCode, 'X')"/></xsl:when>
				<xsl:when test="contains($OIOUBLUnits, concat(',',$fUnitCode,','))"><xsl:value-of select="$fUnitCode"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="'EA'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!--<xsl:variable name="LineExtensionAmount" select="format-number(cbc:LineExtensionAmount, '0.00')"/>-->
		<xsl:variable name="LineExtensionAmount_raw" >
			<xsl:choose>
				<xsl:when test="number(cbc:LineExtensionAmount) = 0 ">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:LineExtensionAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:LineExtensionAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="fLineExtensionAmount"	select="format-number($LineExtensionAmount_raw, '0.00')"/>
		
		<xsl:variable name="FreeOfCharge" select="'true'"/>
		<cac:CreditNoteLine>
			<cbc:ID><xsl:value-of select="cbc:ID"/></cbc:ID>
			<!--Inserting excluded elements in the Note on line-->
			<xsl:choose>
				<xsl:when test="string(cbc:Note)"><cbc:Note><xsl:value-of select="cbc:Note"/><xsl:call-template name="ExcludedElementsHeaderCreditNote"/></cbc:Note></xsl:when>
				<xsl:otherwise><xsl:call-template name="NoteContentCreditNote"></xsl:call-template></xsl:otherwise>
			</xsl:choose>
			<cbc:CreditedQuantity unitCode="{$UnitOfMeasure}"><xsl:value-of select="$fQuantity"/></cbc:CreditedQuantity>
			<cbc:LineExtensionAmount currencyID="{cbc:LineExtensionAmount/@currencyID}"><xsl:value-of select="$fLineExtensionAmount"/></cbc:LineExtensionAmount>
			<xsl:if test="string(cbc:AccountingCost)"><cbc:AccountingCost><xsl:value-of select="cbc:AccountingCost"/></cbc:AccountingCost></xsl:if>
			<xsl:if test="$fLineExtensionAmount = '0.00'"><cbc:FreeOfChargeIndicator><xsl:value-of select="$FreeOfCharge"></xsl:value-of></cbc:FreeOfChargeIndicator></xsl:if>
			<xsl:apply-templates select="cac:DocumentReference" mode="creditnote"/>

			
			<xsl:choose>
				<xsl:when test="../cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:ID = 'O'"><xsl:call-template name="NoLineTaxTotalCreditNote"/></xsl:when>
				<xsl:otherwise><xsl:call-template name="LineTaxTotalCreditNote"/></xsl:otherwise>
			</xsl:choose>

			<xsl:apply-templates select="cac:Item" mode="creditnote"/>
			<xsl:apply-templates select="cac:Price" mode="creditnote">
				<xsl:with-param name="ALCFlag" select="$ALCFlag"/>
				<xsl:with-param name="BaseQFlag" select="$BaseQFlag"/>
				<xsl:with-param name="LineExtensionAmount" select="cbc:LineExtensionAmount"/>
			</xsl:apply-templates>
		</cac:CreditNoteLine>
	</xsl:template>

	<!--  OrderLineReference template -->
	<xsl:template match="cac:OrderLineReference" mode="creditnote">
		<cac:OrderLineReference>
			<cbc:LineID><xsl:value-of select="cbc:LineID"/></cbc:LineID>
		</cac:OrderLineReference>
	</xsl:template>
	
<xsl:template match="cac:DocumentReference" mode="creditnote">
		<cac:DocumentReference>
			<cbc:ID><xsl:value-of select="cbc:ID"/></cbc:ID>
			<xsl:choose>
				<xsl:when test="cbc:ID/@schemeID"><cbc:DocumentTypeCode><xsl:value-of select="cbc:ID/@schemeID"/></cbc:DocumentTypeCode></xsl:when>
				<xsl:otherwise><cbc:DocumentTypeCode><xsl:value-of select="'N/A'"/></cbc:DocumentTypeCode></xsl:otherwise>
			</xsl:choose>
			
		</cac:DocumentReference>
	</xsl:template>
	
	
		<!--  Item template -->
	<xsl:template match="cac:Item" mode="creditnote">
		<xsl:variable name="fDescription">
			<xsl:choose>
				<xsl:when test="not(string(cbc:Description))"><xsl:value-of select="cbc:Name"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:Description"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="fName">
			<xsl:choose>
				<xsl:when test="string(cbc:Name)"><xsl:value-of select="substring(cbc:Name, 1, 40)"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="substring($fDescription, 1, 40)"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<cac:Item>
			<xsl:if test="string($fDescription)"><cbc:Description><xsl:value-of select="$fDescription"/></cbc:Description></xsl:if>
			<xsl:if test="string($fName)"><cbc:Name><xsl:value-of select="$fName"/></cbc:Name></xsl:if>
			<xsl:apply-templates select="cac:SellersItemIdentification" mode="creditnote"/>
			<xsl:apply-templates select="cac:StandardItemIdentification" mode="creditnote"/>
			<xsl:apply-templates select="cac:OriginCountry" mode="creditnote"/>
			<xsl:apply-templates select="cac:CommodityClassification" mode="creditnote"/>
			<xsl:apply-templates select="cac:AdditionalItemProperty" mode="creditnote"/>
		</cac:Item>
	</xsl:template>
	
	<!--  SellersItemIdentification template -->
	<xsl:template match="cac:SellersItemIdentification" mode="creditnote">
	<xsl:if test="string(cbc:ID)">
	<cac:SellersItemIdentification>
		<cbc:ID>
		<xsl:if test="cbc:ID/@schemeID">
			<xsl:attribute name="schemeID"><xsl:value-of select="cbc:ID/@schemeID"></xsl:value-of></xsl:attribute>
		</xsl:if>
		<xsl:if test="cbc:ID/@schemeAgencyID">
			<xsl:attribute name="schemeAgencyID"><xsl:value-of select="cbc:ID/@schemeAgencyID"></xsl:value-of></xsl:attribute>
		</xsl:if>
		<xsl:value-of select="cbc:ID"/>
		</cbc:ID>
	</cac:SellersItemIdentification>
	</xsl:if>
	</xsl:template>
	
	<!--  StandardItemIdentification template -->
	<xsl:template match="cac:StandardItemIdentification" mode="creditnote">
	<xsl:if test="string(cbc:ID)">
	<cac:StandardItemIdentification>
		<cbc:ID>
		<xsl:choose>
			<xsl:when test="cbc:ID/@schemeID = '0160'">
				<xsl:attribute name="schemeID"><xsl:value-of select="'GTIN'"/></xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="schemeID">
					<xsl:value-of select="cbc:ID/@schemeID"/>
				</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="cbc:ID/@schemeAgencyID">
			<xsl:attribute name="schemeAgencyID"><xsl:value-of select="cbc:ID/@schemeAgencyID"></xsl:value-of></xsl:attribute>
		</xsl:if>
		<xsl:value-of select="cbc:ID"/>
		</cbc:ID>
	</cac:StandardItemIdentification>
	</xsl:if>
	</xsl:template>

	<xsl:template match="cac:OriginCountry" mode="creditnote">

		<xsl:if test="cac:OriginCountry/cbc:IdentificationCode">
			<cac:OriginCountry>
				<cbc:IdentificationCode><xsl:value-of select="cbc:IdentificationCode"/></cbc:IdentificationCode>
			</cac:OriginCountry>
		</xsl:if>
	</xsl:template>

	<!--  CommodityClassification template -->
	<xsl:template match="cac:CommodityClassification" mode="creditnote">
		<xsl:variable name="t1" select="cbc:ItemClassificationCode/@listID"/>
		<xsl:if test="$t1 = 'UNSPSC'">
			<cac:CommodityClassification>
				<cbc:ItemClassificationCode listAgencyID="113" listID="UNSPSC" listVersionID="7.0401"><xsl:value-of select="cbc:ItemClassificationCode"/></cbc:ItemClassificationCode>
			</cac:CommodityClassification>
		</xsl:if>
	</xsl:template>

	<!--  AdditionalItemProperty template -->
	<xsl:template match="cac:AdditionalItemProperty" mode="creditnote">
		<cac:AdditionalItemProperty>
			<cbc:Name><xsl:value-of select="cbc:Name"/></cbc:Name>
			<cbc:Value><xsl:value-of select="cbc:Value"/></cbc:Value>
		</cac:AdditionalItemProperty>
	</xsl:template>

	<!--  Price template -->
	<xsl:template match="cac:Price" mode="creditnote">
		<xsl:param name="ALCFlag"/>
		<xsl:param name="BaseQFlag"/>
		<xsl:param name="LineExtensionAmount"/>
		<xsl:variable name="fUnitCode" select="cbc:BaseQuantity/@unitCode"/>
		<xsl:variable name="OIOUBLUnits" select="',04,05,08,10,11,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,40,41,43,44,45,46,47,48,53,54,56,57,58,59,60,61,62,63,64,66,69,71,72,73,74,76,77,78,80,81,84,85,87,89,90,91,92,93,94,95,96,97,98,1A,1B,1C,1D,1E,1F,1G,1H,1I,1J,1K,1L,1M,1X,2A,2B,2C,2I,2J,2K,2L,2M,2N,2P,2Q,2R,2U,2V,2W,2X,2Y,2Z,3B,3C,3E,3G,3H,3I,4A,4B,4C,4E,4G,4H,4K,4L,4M,4N,4O,4P,4Q,4R,4T,4U,4W,4X,5A,5B,5C,5E,5F,5G,5H,5I,5J,5K,5P,5Q,A1,A10,A11,A12,A13,A14,A15,A16,A17,A18,A19,A2,A20,A21,A22,A23,A24,A25,A26,A27,A28,A29,A3,A30,A31,A32,A33,A34,A35,A36,A37,A38,A39,A4,A40,A41,A42,A43,A44,A45,A47,A48,A49,A5,A50,A51,A52,A53,A54,A55,A56,A57,A58,A6,A60,A61,A62,A63,A64,A65,A66,A67,A68,A69,A7,A70,A71,A73,A74,A75,A76,A77,A78,A79,A8,A80,A81,A82,A83,A84,A85,A86,A87,A88,A89,A9,A90,A91,A93,A94,A95,A96,A97,A98,AA,AB,ACR,AD,AE,AH,AI,AJ,AK,AL,AM,AMH,AMP,ANN,AP,APZ,AQ,AR,ARE,AS,ASM,ASU,ATM,ATT,AV,AW,AY,AZ,B0,B1,B11,B12,B13,B14,B15,B16,B18,B2,B20,B21,B22,B23,B24,B25,B26,B27,B28,B29,B3,B31,B32,B33,B34,B35,B36,B37,B38,B39,B4,B40,B41,B42,B43,B44,B45,B46,B47,B48,B49,B5,B50,B51,B52,B53,B54,B55,B56,B57,B58,B59,B6,B60,B61,B62,B63,B64,B65,B66,B67,B69,B7,B70,B71,B72,B73,B74,B75,B76,B77,B78,B79,B8,B81,B83,B84,B85,B86,B87,B88,B89,B9,B90,B91,B92,B93,B94,B95,B96,B97,B98,B99,BAR,BB,BD,BE,BFT,BG,BH,BHP,BIL,BJ,BK,BL,BLD,BLL,BO,BP,BQL,BR,BT,BTU,BUA,BUI,BW,BX,BZ,C0,C1,C10,C11,C12,C13,C14,C15,C16,C17,C18,C19,C2,C20,C22,C23,C24,C25,C26,C27,C28,C29,C3,C30,C31,C32,C33,C34,C35,C36,C38,C39,C4,C40,C41,C42,C43,C44,C45,C46,C47,C48,C49,C5,C50,C51,C52,C53,C54,C55,C56,C57,C58,C59,C6,C60,C61,C62,C63,C64,C65,C66,C67,C68,C69,C7,C70,C71,C72,C73,C75,C76,C77,C78,C8,C80,C81,C82,C83,C84,C85,C86,C87,C88,C89,C9,C90,C91,C92,C93,C94,C95,C96,C97,C98,C99,CA,CCT,CDL,CEL,CEN,CG,CGM,CH,CJ,CK,CKG,CL,CLF,CLT,CMK,CMQ,CMT,CNP,CNT,CO,COU,CQ,CR,CS,CT,CTM,CU,CUR,CV,CWA,CWI,CY,CZ,D1,D10,D12,D13,D14,D15,D16,D17,D18,D19,D2,D20,D21,D22,D23,D24,D25,D26,D27,D28,D29,D30,D31,D32,D33,D34,D35,D37,D38,D39,D40,D41,D42,D43,D44,D45,D46,D47,D48,D49,D5,D50,D51,D52,D53,D54,D55,D56,D57,D58,D59,D6,D60,D61,D62,D63,D64,D65,D66,D67,D69,D7,D70,D71,D72,D73,D74,D75,D76,D77,D79,D8,D80,D81,D82,D83,D85,D86,D87,D88,D89,D9,D90,D91,D92,D93,D94,D95,D96,D97,D98,D99,DAA,DAD,DAY,DB,DC,DD,DE,DEC,DG,DI,DJ,DLT,DMK,DMQ,DMT,DN,DPC,DPR,DPT,DQ,DR,DRA,DRI,DRL,DRM,DS,DT,DTN,DU,DWT,DX,DY,DZN,DZP,E2,E3,E4,E5,EA,EB,EC,EP,EQ,EV,F1,F9,FAH,FAR,FB,FC,FD,FE,FF,FG,FH,FL,FM,FOT,FP,FR,FS,FTK,FTQ,G2,G3,G7,GB,GBQ,GC,GD,GE,GF,GFI,GGR,GH,GIA,GII,GJ,GK,GL,GLD,GLI,GLL,GM,GN,GO,GP,GQ,GRM,GRN,GRO,GRT,GT,GV,GW,GWH,GY,GZ,H1,H2,HA,HAR,HBA,HBX,HC,HD,HE,HF,HGM,HH,HI,HIU,HJ,HK,HL,HLT,HM,HMQ,HMT,HN,HO,HP,HPA,HS,HT,HTZ,HUR,HY,IA,IC,IE,IF,II,IL,IM,INH,INK,INQ,IP,IT,IU,IV,J2,JB,JE,JG,JK,JM,JO,JOU,JR,K1,K2,K3,K5,K6,KA,KB,KBA,KD,KEL,KF,KG,KGM,KGS,KHZ,KI,KJ,KJO,KL,KMH,KMK,KMQ,KNI,KNS,KNT,KO,KPA,KPH,KPO,KPP,KR,KS,KSD,KSH,KT,KTM,KTN,KUR,KVA,KVR,KVT,KW,KWH,KWT,KX,L2,LA,LBR,LBT,LC,LD,LE,LEF,LF,LH,LI,LJ,LK,LM,LN,LO,LP,LPA,LR,LS,LTN,LTR,LUM,LUX,LX,LY,M0,M1,M4,M5,M7,M9,MA,MAL,MAM,MAW,MBE,MBF,MBR,MC,MCU,MD,MF,MGM,MHZ,MIK,MIL,MIN,MIO,MIU,MK,MLD,MLT,MMK,MMQ,MMT,MON,MPA,MQ,MQH,MQS,MSK,MT,MTK,MTQ,MTR,MTS,MV,MVA,MWH,N1,N2,N3,NA,NAR,NB,NBB,NC,NCL,ND,NE,NEW,NF,NG,NH,NI,NIU,NJ,NL,NMI,NMP,NN,NPL,NPR,NPT,NQ,NR,NRL,NT,NTT,NU,NV,NX,NY,OA,OHM,ON,ONZ,OP,OT,OZ,OZA,OZI,P0,P1,P2,P3,P4,P5,P6,P7,P8,P9,PA,PAL,PB,PD,PE,PF,PG,PGL,PI,PK,PL,PM,PN,PO,PQ,PR,PS,PT,PTD,PTI,PTL,PU,PV,PW,PY,PZ,Q3,QA,QAN,QB,QD,QH,QK,QR,QT,QTD,QTI,QTL,QTR,R1,R4,R9,RA,RD,RG,RH,RK,RL,RM,RN,RO,RP,RPM,RPS,RS,RT,RU,S3,S4,S5,S6,S7,S8,SA,SAN,SCO,SCR,SD,SE,SEC,SET,SG,SHT,SIE,SK,SL,SMI,SN,SO,SP,SQ,SR,SS,SST,ST,STI,STN,SV,SW,SX,T0,T1,T3,T4,T5,T6,T7,T8,TA,TAH,TC,TD,TE,TF,TI,TJ,TK,TL,TN,TNE,TP,TPR,TQ,TQD,TR,TRL,TS,TSD,TSH,TT,TU,TV,TW,TY,U1,U2,UA,UB,UC,UD,UE,UF,UH,UM,VA,VI,VLT,VQ,VS,W2,W4,WA,WB,WCD,WE,WEB,WEE,WG,WH,WHR,WI,WM,WR,WSD,WTT,WW,X1,YDK,YDQ,YL,YRD,YT,Z1,Z2,Z3,Z4,Z5,Z6,Z8,ZP,ZZ,'"/>		
		<xsl:variable name="UnitOfMeasure">
			<xsl:choose>
				<xsl:when test="contains($OIOUBLUnits, concat(',',$fUnitCode,','))"><xsl:value-of select="$fUnitCode"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="'EA'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="UnitPriceNew">
			<xsl:choose>
				<xsl:when test="$ALCFlag = 'true' and $BaseQFlag = 'false'"><xsl:value-of select="$LineExtensionAmount div ../cbc:CreditedQuantity"/></xsl:when>
				<xsl:when test="$ALCFlag = 'true' and $BaseQFlag = 'true'"><xsl:value-of select="($LineExtensionAmount div ../cbc:CreditedQuantity) * number(cbc:BaseQuantity)"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:PriceAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="UnitPriceNew2">
			<xsl:choose>
				<xsl:when test="$ALCFlag = 'true' and $BaseQFlag = 'false'"><xsl:value-of select="$LineExtensionAmount div ../cbc:InvoicedQuantity"/></xsl:when>
				<xsl:when test="$ALCFlag = 'true' and $BaseQFlag = 'true'"><xsl:value-of select="($LineExtensionAmount div ../cbc:InvoicedQuantity) * number(cbc:BaseQuantity)"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:PriceAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>	

		
		<cac:Price>
			<xsl:choose>
				<xsl:when test="number(cbc:PriceAmount) = 0">
				<cbc:PriceAmount currencyID="{cbc:PriceAmount/@currencyID}"><xsl:value-of select="'0.00'"/></cbc:PriceAmount>
					<xsl:if test="string(cbc:BaseQuantity)"><cbc:BaseQuantity unitCode="{$UnitOfMeasure}"><xsl:value-of select="cbc:BaseQuantity"/></cbc:BaseQuantity></xsl:if>
					<xsl:if test="$BaseQFlag = 'true'"><cbc:OrderableUnitFactorRate><xsl:value-of select="$LineExtensionAmount div (cbc:PriceAmount * ../cbc:CreditedQuantity)"/></cbc:OrderableUnitFactorRate></xsl:if>
					<xsl:if test="string(/cac:InvoicePeriod)">
						<cac:ValidityPeriod>
							<xsl:if test="string(/cac:InvoicePeriod/cbc:StartDate)"><cbc:StartDate><xsl:value-of select="/cac:InvoicePeriod/cbc:StartDate"/></cbc:StartDate></xsl:if>
							<xsl:if test="string(/cac:InvoicePeriod/cbc:EndDate)"><cbc:EndDate><xsl:value-of select="/cac:InvoicePeriod/cbc:EndDate"/></cbc:EndDate></xsl:if>
						</cac:ValidityPeriod>
					</xsl:if>
					<xsl:apply-templates select="cac:AllowanceCharge" mode="creditnote"/>
				</xsl:when>
				<xsl:when test="$Converted">
					<cbc:PriceAmount currencyID="{cbc:PriceAmount/@currencyID}"><xsl:value-of select="$UnitPriceNew2 * -1"/></cbc:PriceAmount>
					<xsl:if test="string(cbc:BaseQuantity)"><cbc:BaseQuantity unitCode="{$UnitOfMeasure}"><xsl:value-of select="cbc:BaseQuantity"/></cbc:BaseQuantity></xsl:if>
					<xsl:if test="$BaseQFlag = 'true'"><cbc:OrderableUnitFactorRate><xsl:value-of select="$LineExtensionAmount div (cbc:PriceAmount * ../cbc:InvoicedQuantity)"/></cbc:OrderableUnitFactorRate></xsl:if>
					<xsl:if test="string(/cac:InvoicePeriod)">
						<cac:ValidityPeriod>
							<xsl:if test="string(/cac:InvoicePeriod/cbc:StartDate)"><cbc:StartDate><xsl:value-of select="/cac:InvoicePeriod/cbc:StartDate"/></cbc:StartDate></xsl:if>
							<xsl:if test="string(/cac:InvoicePeriod/cbc:EndDate)"><cbc:EndDate><xsl:value-of select="/cac:InvoicePeriod/cbc:EndDate"/></cbc:EndDate></xsl:if>
						</cac:ValidityPeriod>
					</xsl:if>
					<xsl:apply-templates select="cac:AllowanceCharge" mode="creditnote"/>
				</xsl:when>
				<xsl:otherwise>
					<cbc:PriceAmount currencyID="{cbc:PriceAmount/@currencyID}"><xsl:value-of select="$UnitPriceNew"/></cbc:PriceAmount>
					<xsl:if test="string(cbc:BaseQuantity)"><cbc:BaseQuantity unitCode="{$UnitOfMeasure}"><xsl:value-of select="cbc:BaseQuantity"/></cbc:BaseQuantity></xsl:if>
					<xsl:if test="$BaseQFlag = 'true'"><cbc:OrderableUnitFactorRate><xsl:value-of select="$LineExtensionAmount div (cbc:PriceAmount * ../cbc:CreditedQuantity)"/></cbc:OrderableUnitFactorRate></xsl:if>
					<xsl:if test="string(/cac:InvoicePeriod)">
						<cac:ValidityPeriod>
							<xsl:if test="string(/cac:InvoicePeriod/cbc:StartDate)"><cbc:StartDate><xsl:value-of select="/cac:InvoicePeriod/cbc:StartDate"/></cbc:StartDate></xsl:if>
							<xsl:if test="string(/cac:InvoicePeriod/cbc:EndDate)"><cbc:EndDate><xsl:value-of select="/cac:InvoicePeriod/cbc:EndDate"/></cbc:EndDate></xsl:if>
						</cac:ValidityPeriod>
					</xsl:if>
					<xsl:apply-templates select="cac:AllowanceCharge" mode="creditnote"/>
				</xsl:otherwise>
			</xsl:choose>
		</cac:Price>
	</xsl:template>



	<!-- .............................. -->
	<!--   Utility Templates            -->
	<!-- .............................. -->


	<!-- Basis Note template  -->
	<xsl:template name="NoteCreditNote">
			<xsl:call-template name="Note1CreditNote"/>
	</xsl:template>
	
	<!--Regular Note Template-->
	<xsl:template name="Note1CreditNote">
	<xsl:variable name="t1"	select="cbc:Note[1]"/>
	<xsl:variable name="t2"	select="'[Bemærk: dette dokument er automatisk konverteret fra PEPPOL til OIOUBL]'"/>
	<xsl:variable name="t3"	select="'[Bemærk: Konvertering er lavet fra en negativ faktura tilladt i PEPPOL til en OIOUBL Kreditnota, hvilket betyder at alle beløb har fået omvendt fortegn.]'"/>
	<xsl:choose>
		<xsl:when test="string($t1) and $Converted">
			<cbc:Note><xsl:value-of select="$t1"/>&#160;<xsl:value-of select="$t2"/>&#160;<xsl:value-of select="$t3"/><xsl:call-template name="ExcludedElementsHeaderCreditNote"></xsl:call-template></cbc:Note>
		</xsl:when>
		<xsl:when test="string($t1)">
			<cbc:Note><xsl:value-of select="$t1"/>&#160;<xsl:value-of select="$t2"/><xsl:call-template name="ExcludedElementsHeaderCreditNote"></xsl:call-template></cbc:Note>
		</xsl:when>
			<xsl:otherwise><cbc:Note><xsl:value-of select="$t2"/><xsl:call-template name="ExcludedElementsHeaderCreditNote"></xsl:call-template></cbc:Note></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!--Template for handling excluded elements on header. This is alle the excluded elements in OIOUBL that will be shown in the note field on header-->
	<xsl:template name="ExcludedElementsHeaderCreditNote">
	<!--Variables-->
	<xsl:variable name="CompanyLegalForm" select="cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyLegalForm"></xsl:variable>
	
	<!--Generate outout and place in Note-->
		<xsl:if test="string($CompanyLegalForm)">&#160;Legal Information:&#160;<xsl:value-of select="$CompanyLegalForm"></xsl:value-of></xsl:if>
	</xsl:template>
	
	<xsl:template name="NoteContentCreditNote">	
	<xsl:variable name="TestTemplate">
		<!--<xsl:call-template name="ExcludedElementsLine"></xsl:call-template>-->
	</xsl:variable>
	<xsl:if test="string($TestTemplate)"><cbc:Note><xsl:copy-of select="$TestTemplate"></xsl:copy-of></cbc:Note></xsl:if>
	</xsl:template>
	

	<!--  EndpointID template - supplier -->
	<xsl:template name="EndpointIDSupplierCreditNote">
		<xsl:variable name="OIOUBLids" select="',DUNS,GLN,IBAN,ISO 6523,ZZZ,DK:CPR,DK:CVR,DK:P,DK:SE,DK:TELEFON,FI:ORGNR,IS:KT,IS:VSKNR,NO:EFO,NO:NOBB,NO:NODI,NO:ORGNR,NO:VAT,SE:ORGNR,SE:VAT,FR:SIRET,FI:OVT,IT:FTI,IT:SIA,IT:SECETI,IT:VAT,IT:CF,HU:VAT,EU:VAT,EU:REID,AT:VAT,AT:GOV,AT:CID,IS:KT,AT:KUR,ES:VAT,IT:IPA,AD:VAT,AL:VAT,BA:VAT,BE:VAT,BG:VAT,CH:VAT,CY:VAT,CZ:VAT,DE:VAT,EE:VAT,GB:VAT,GR:VAT,HR:VAT,IE:VAT,LI:VAT,LT:VAT,LU:VAT,LV:VAT,MC:VAT,ME:VAT,MK:VAT,MT:VAT,NL:VAT,PL:VAT,PT:VAT,RO:VAT,RS:VAT,SI:VAT,SK:VAT,SM:VAT,TR:VAT,VA:VAT,'"/>
		<xsl:variable name="t1" select="cac:Party/cbc:EndpointID/@schemeID"/>
		<xsl:variable name="t2" select="cac:Party/cbc:EndpointID"/>
		<xsl:variable name="ft1">
			<xsl:choose>
				<xsl:when test="$t1 = '0002'">FR:SIRENE</xsl:when>
				<xsl:when test="$t1 = '0007'">SE:ORGNR</xsl:when>
				<xsl:when test="$t1 = '0009'">FR:SIRET</xsl:when>
				<xsl:when test="$t1 = '0037'">FI:OVT</xsl:when>
				<xsl:when test="$t1 = '0060'">DUNS</xsl:when>
				<xsl:when test="$t1 = '0088'">GLN</xsl:when>
				<xsl:when test="$t1 = '0096'">DK:P</xsl:when>
				<xsl:when test="$t1 = '0097'">IT:FTI</xsl:when>
				<xsl:when test="$t1 = '0106'">NL:KVK</xsl:when>
				<xsl:when test="$t1 = '0135'">IT:SIA</xsl:when>
				<xsl:when test="$t1 = '0142'">IT:SECETI</xsl:when>
				<xsl:when test="$t1 = '0184' or $t1 = '9902'">DK:CVR</xsl:when>
				<xsl:when test="$t1 = 'SEPA'">SEPA</xsl:when>
				<xsl:when test="$t1 = '0192' or $t1 = '9908'">NO:ORGNR</xsl:when>
				<xsl:when test="$t1 = '0198'">DK:SE</xsl:when>
				<xsl:otherwise><xsl:value-of select="'ZZZ'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="ft2" select="$t2"/>
		<cbc:EndpointID schemeID="{$ft1}"><xsl:value-of select="$ft2"/></cbc:EndpointID>
	</xsl:template>
	
	
	<!--  EndpointID template - customer -->
	<xsl:template name="EndpointIDCustomerCreditNote">
		<xsl:variable name="OIOUBLids" select="',DUNS,GLN,IBAN,ISO 6523,ZZZ,DK:CPR,DK:CVR,DK:P,DK:SE,DK:TELEFON,FI:ORGNR,IS:KT,IS:VSKNR,NO:EFO,NO:NOBB,NO:NODI,NO:ORGNR,NO:VAT,SE:ORGNR,SE:VAT,FR:SIRET,FI:OVT,IT:FTI,IT:SIA,IT:SECETI,IT:VAT,IT:CF,HU:VAT,EU:VAT,EU:REID,AT:VAT,AT:GOV,AT:CID,IS:KT,AT:KUR,ES:VAT,IT:IPA,AD:VAT,AL:VAT,BA:VAT,BE:VAT,BG:VAT,CH:VAT,CY:VAT,CZ:VAT,DE:VAT,EE:VAT,GB:VAT,GR:VAT,HR:VAT,IE:VAT,LI:VAT,LT:VAT,LU:VAT,LV:VAT,MC:VAT,ME:VAT,MK:VAT,MT:VAT,NL:VAT,PL:VAT,PT:VAT,RO:VAT,RS:VAT,SI:VAT,SK:VAT,SM:VAT,TR:VAT,VA:VAT,'"/>
		<xsl:variable name="t1" select="cac:Party/cbc:EndpointID/@schemeID"/>
		<xsl:variable name="t2" select="cac:Party/cbc:EndpointID"/>
		<xsl:variable name="ft1">
			<xsl:choose>
				<xsl:when test="$t1 = '0002'">FR:SIRENE</xsl:when>
				<xsl:when test="$t1 = '0007'">SE:ORGNR</xsl:when>
				<xsl:when test="$t1 = '0009'">FR:SIRET</xsl:when>
				<xsl:when test="$t1 = '0037'">FI:OVT</xsl:when>
				<xsl:when test="$t1 = '0060'">DUNS</xsl:when>
				<xsl:when test="$t1 = '0088'">GLN</xsl:when>
				<xsl:when test="$t1 = '0096'">DK:P</xsl:when>
				<xsl:when test="$t1 = '0097'">IT:FTI</xsl:when>
				<xsl:when test="$t1 = '0106'">NL:KVK</xsl:when>
				<xsl:when test="$t1 = '0135'">IT:SIA</xsl:when>
				<xsl:when test="$t1 = '0142'">IT:SECETI</xsl:when>
				<xsl:when test="$t1 = '0184' or $t1 = '9902'">DK:CVR</xsl:when>
				<xsl:when test="$t1 = 'SEPA'">SEPA</xsl:when>
				<xsl:when test="$t1 = '0192' or $t1 = '9908'">NO:ORGNR</xsl:when>
				<xsl:when test="$t1 = '0198'">DK:SE</xsl:when>
				<xsl:otherwise><xsl:value-of select="'ZZZ'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="ft2" select="$t2"/>
		<cbc:EndpointID schemeID="{$ft1}"><xsl:value-of select="$ft2"/></cbc:EndpointID>
	</xsl:template>

	<!-- Address template -->
	<xsl:template name="AddressCreditNote">
			
		<cbc:AddressFormatCode listAgencyID="320" listID="urn:oioubl:codelist:addressformatcode-1.1">StructuredLax</cbc:AddressFormatCode>
		<xsl:if test="string(cbc:StreetName)"><cbc:StreetName><xsl:value-of select="cbc:StreetName"/></cbc:StreetName></xsl:if>
		<xsl:choose>
			<xsl:when test="string(cbc:AdditionalStreetName) and not(cac:AddressLine/cbc:Line)">
			<cbc:AdditionalStreetName><xsl:value-of select="cbc:AdditionalStreetName"/></cbc:AdditionalStreetName></xsl:when>
			<xsl:when test="string(cbc:AdditionalStreetName) and (cac:AddressLine/cbc:Line)">
			<cbc:AdditionalStreetName><xsl:value-of select="cbc:AdditionalStreetName"/>,&#160;<xsl:value-of select="cac:AddressLine/cbc:Line"/></cbc:AdditionalStreetName></xsl:when>
			<xsl:when test="string(cac:AddressLine/cbc:Line) and not(cbc:AdditionalStreetName)">
			<cbc:AdditionalStreetName><xsl:value-of select="cac:AddressLine/cbc:Line"/></cbc:AdditionalStreetName></xsl:when>
		</xsl:choose>

		<xsl:if test="string(cbc:CityName)"><cbc:CityName><xsl:value-of select="cbc:CityName"/></cbc:CityName></xsl:if>
		<xsl:if test="string(cbc:PostalZone)"><cbc:PostalZone><xsl:value-of select="cbc:PostalZone"/></cbc:PostalZone></xsl:if>
		<xsl:if test="string(cbc:CountrySubentity)"><cbc:CountrySubentity><xsl:value-of select="cbc:CountrySubentity"/></cbc:CountrySubentity></xsl:if>

		<xsl:if test="string(cac:Country/cbc:IdentificationCode)">
			<cac:Country>
				<cbc:IdentificationCode><xsl:value-of select="cac:Country/cbc:IdentificationCode"/></cbc:IdentificationCode>
			</cac:Country>
		</xsl:if>
	</xsl:template>

	<!--  SupplierPartyLegalEntity template -->
	<xsl:template name="SupplierPartyLegalEntityCreditNote">
		<xsl:variable name="OIOUBLids" select="',DK:CVR,DK:CPR,ZZZ,'"/>
		<xsl:variable name="t1" select="cac:Party/cac:PartyLegalEntity/cbc:CompanyID/@schemeID"/>
		<xsl:variable name="t2" select="cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/>
		<xsl:variable name="t3" select="cac:Party/cac:PartyLegalEntity/cbc:RegistrationName"/>
		<xsl:variable name="t4" select="cac:Party/cbc:EndpointID"/>
		<xsl:variable name="ft1">
			<xsl:choose>
				<xsl:when test="$t1 = '0184'">DK:CVR</xsl:when>
				<xsl:otherwise><xsl:value-of select="'ZZZ'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="ft2">
			<xsl:choose>
				<xsl:when test="string($t2)"><xsl:value-of select="$t2"/></xsl:when>
				<xsl:when test="string($t4)"><xsl:value-of select="$t4"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="'n/a'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="ft3">
			<xsl:choose>
				<xsl:when test="string($t3)"><xsl:value-of select="$t3"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="'n/a'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<cac:PartyLegalEntity>
			<cbc:RegistrationName><xsl:value-of select="$ft3"/></cbc:RegistrationName>
			<cbc:CompanyID schemeID="{$ft1}"><xsl:value-of select="$ft2"/></cbc:CompanyID>
		</cac:PartyLegalEntity>
	</xsl:template>

	<!--  CustomerContact template -->
	<xsl:template name="CustomerContactCreditNote">
		<xsl:variable name="t1">
			<xsl:choose>
				<xsl:when test="string(//cbc:BuyerReference)"><xsl:value-of select="//cbc:BuyerReference"/></xsl:when>
				<xsl:otherwise>n/a</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<cac:Contact>
			<cbc:ID><xsl:value-of select="$t1"/></cbc:ID>
			<xsl:if test="string(cac:Party/cac:Contact/cbc:Name)"><cbc:Name><xsl:value-of select="cac:Party/cac:Contact/cbc:Name"/></cbc:Name></xsl:if>
			<xsl:if test="string(cac:Party/cac:Contact/cbc:Telephone)"><cbc:Telephone><xsl:value-of select="cac:Party/cac:Contact/cbc:Telephone"/></cbc:Telephone></xsl:if>
			<xsl:if test="string(cac:Party/cac:Contact/cbc:ElectronicMail)"><cbc:ElectronicMail><xsl:value-of select="cac:Party/cac:Contact/cbc:ElectronicMail"/></cbc:ElectronicMail></xsl:if>
		</cac:Contact>
	</xsl:template>
	
	<xsl:template name="LineTaxTotalCreditNote">
<!--		<xsl:variable name="taxAmount"	select="format-number(cac:TaxTotal/cbc:TaxAmount, '0.00')"/>-->
		<!--<xsl:variable name="currencyID"	select="cac:TaxTotal/cbc:TaxAmount/@currencyID"/>-->
		<xsl:variable name="currencyID"	select="cbc:LineExtensionAmount/@currencyID"/>
		<xsl:variable name="classifiedTaxCategoryPercent" select="cac:Item/cac:ClassifiedTaxCategory/cbc:Percent"/>
		
		<xsl:variable name="TaxAmount">
			<xsl:choose>
<!--				<xsl:when test="string($taxAmount) != 'NaN'"><xsl:value-of select="$taxAmount"/></xsl:when>-->
				<xsl:when test="string($classifiedTaxCategoryPercent) !=''"><xsl:value-of select="format-number((cbc:LineExtensionAmount * cac:Item/cac:ClassifiedTaxCategory/cbc:Percent) div 100, '0.00')"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="'0.00'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="TaxAmount_raw" >
			<xsl:choose>
				<xsl:when test="number($TaxAmount) = 0">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="number($TaxAmount) * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="$TaxAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="taxAmount_raw"	select="format-number($TaxAmount_raw, '0.00')"/>
		<xsl:variable name="LineExtensionAmount_raw" >
			<xsl:choose>
				<xsl:when test="number(cbc:LineExtensionAmount) = 0">0</xsl:when>
				<xsl:when test="$Converted"><xsl:value-of select="cbc:LineExtensionAmount * -1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="cbc:LineExtensionAmount"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="lineExtensionAmount"	select="format-number($LineExtensionAmount_raw, '0.00')"/>

		<xsl:variable name="TaxCategoryID">
			<xsl:call-template name="TaxCategoryIDConvertCreditNote">
				<xsl:with-param name="p1"><xsl:value-of select="cac:Item/cac:ClassifiedTaxCategory/cbc:ID"/></xsl:with-param>
			</xsl:call-template>								
		</xsl:variable>
		
		<xsl:variable name="Percent">
			<xsl:choose>
				<xsl:when test="string(cac:Item/cac:ClassifiedTaxCategory/cbc:Percent)"><xsl:value-of select="cac:Item/cac:ClassifiedTaxCategory/cbc:Percent"/></xsl:when>
				<xsl:when test="$lineExtensionAmount = 0"><xsl:value-of select="'00'"/></xsl:when>
<!--				<xsl:when test="string($taxAmount) != 'NaN'"><xsl:value-of select="format-number(($taxAmount div $lineExtensionAmount) * 100, '##')"/></xsl:when>-->
				<xsl:otherwise><xsl:value-of select="'00'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="TaxSchemeID">
			<xsl:call-template name="TaxSchemeIDConvertCreditNote">
				<xsl:with-param name="p1"><xsl:value-of select="cac:Item/cac:ClassifiedTaxCategory/cac:TaxScheme/cbc:ID"/></xsl:with-param>
				<xsl:with-param name="SupplierParty"><xsl:value-of select="//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/></xsl:with-param>
			</xsl:call-template>								
		</xsl:variable>
		<xsl:variable name="TaxSchemeName">
			<xsl:call-template name="TaxSchemeNameConvertCreditNote">
				<xsl:with-param name="p1"><xsl:value-of select="$TaxSchemeID"/></xsl:with-param>
			</xsl:call-template>								
		</xsl:variable>
		<cac:TaxTotal>
			<cbc:TaxAmount currencyID="{$currencyID}"><xsl:value-of select="$taxAmount_raw"/></cbc:TaxAmount>
			<cac:TaxSubtotal>
				<cbc:TaxableAmount currencyID="{$currencyID}"><xsl:value-of select="$lineExtensionAmount"/></cbc:TaxableAmount>
				<cbc:TaxAmount currencyID="{$currencyID}"><xsl:value-of select="$taxAmount_raw"/></cbc:TaxAmount>
					<cac:TaxCategory>
						<cbc:ID schemeAgencyID="320" schemeID="urn:oioubl:id:taxcategoryid-1.2"><xsl:value-of select="$TaxCategoryID"/></cbc:ID>
						<cbc:Percent><xsl:value-of select="$Percent"/></cbc:Percent>
						<cac:TaxScheme>
							<cbc:ID schemeAgencyID="320" schemeID="urn:oioubl:id:taxschemeid-1.5"><xsl:value-of select="$TaxSchemeID"/></cbc:ID>
							<cbc:Name><xsl:value-of select="$TaxSchemeName"/></cbc:Name>
							<!--<xsl:if test="$TaxSchemeID != '63'"><cbc:TaxTypeCode listAgencyID="320" listID="urn:oioubl:codelist:taxtypecode-1.1"><xsl:value-of select="'StandardRated'"/></cbc:TaxTypeCode></xsl:if>-->
						</cac:TaxScheme>
					</cac:TaxCategory>
			</cac:TaxSubtotal>
		</cac:TaxTotal>
	</xsl:template>
	
	<xsl:template name="NoLineTaxTotalCreditNote">
		<xsl:variable name="taxAmount"	select="'0.00'"/>
		<xsl:variable name="taxAmount_currencyID">
			<xsl:choose>
				<xsl:when test="../cbc:DocumentCurrencyCode"><xsl:value-of select="../cbc:DocumentCurrencyCode"></xsl:value-of></xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="lineExtensionAmount_currencyID"	select="cbc:LineExtensionAmount/@currencyID"/>
		<xsl:variable name="lineExtensionAmount"	select="format-number(cbc:LineExtensionAmount, '0.00')"/>
		<xsl:variable name="Percent" select="00"></xsl:variable>
		<xsl:variable name="TaxSchemeID" select="'VAT'"></xsl:variable>
		<xsl:variable name="TaxCategoryID" select="'ZeroRated'"></xsl:variable>
		<xsl:variable name="TaxSchemeName" select="'VAT'"></xsl:variable>
		<cac:TaxTotal>
			<cbc:TaxAmount currencyID="{$taxAmount_currencyID}"><xsl:value-of select="$taxAmount"/></cbc:TaxAmount>
			<cac:TaxSubtotal>
				<cbc:TaxableAmount currencyID="{$lineExtensionAmount_currencyID}"><xsl:value-of select="$lineExtensionAmount"/></cbc:TaxableAmount>
				<cbc:TaxAmount currencyID="{$taxAmount_currencyID}"><xsl:value-of select="$taxAmount"/></cbc:TaxAmount>
					<cac:TaxCategory>
						<cbc:ID schemeAgencyID="320" schemeID="urn:oioubl:id:taxcategoryid-1.2"><xsl:value-of select="$TaxCategoryID"/></cbc:ID>
						<cbc:Percent><xsl:value-of select="$Percent"/></cbc:Percent>
						<cac:TaxScheme>
							<cbc:ID schemeAgencyID="320" schemeID="urn:oioubl:id:taxschemeid-1.5"><xsl:value-of select="$TaxSchemeID"/></cbc:ID>
							<cbc:Name><xsl:value-of select="$TaxSchemeName"/></cbc:Name>
						</cac:TaxScheme>
					</cac:TaxCategory>
			</cac:TaxSubtotal>
		</cac:TaxTotal>
	</xsl:template>

	<!-- TaxCategoryIDConvert -->
	<xsl:template name="TaxCategoryIDConvertCreditNote">
		<xsl:param name="p1"/>
		<xsl:variable name="OIOUBLids" select="',ZeroRated,StandardRated,ReverseCharge,Excise,3010,3020,3021,3022,3023,3024,3025,3030,3040,3041,3048,3049,3050,3051,3052,3053,3054,3055,3056,3057,3058,3059,3060,3061,3062,3063,3064,3065,3066,3067,3068,3070,3071,3072,3073,3075,3077,3080,3081,3082,3083,3084,3085,3086,3090,3091,3092,3093,3094,3095,3096,3100,3101,3102,3104,3120,3121,3122,3123,3130,3140,3141,3160,3161,3162,3163,3170,3171,3240,3241,3242,3245,3246,3247,3250,3251,3260,3271,3272,3273,3276,3277,3280,3281,3282,3283,3290,3291,3292,3293,3294,3295,3296,3297,3300,3301,3302,3303,3304,3305,3310,3311,3320,3321,3330,3331,3340,3341,3350,3351,3360,3370,3380,3400,3403,3404,3405,3406,3410,3420,3430,3440,3441,3451,3452,3453,3500,3501,3502,3503,3600,3620,3621,3622,3623,3624,3630,3631,3632,3633,3634,3635,3636,3637,3638,3640,3641,3645,3650,3660,3661,3670,3671,310301,310302,310303,310304,310305,310306,310307,'"/>
		<xsl:variable name="ft1">
			<xsl:choose>
				<xsl:when test="$p1 = 'AE'"><xsl:value-of select="'ReverseCharge'"/></xsl:when> <!--  VAT Reverse Charge -->
				<xsl:when test="$p1 = 'E'"><xsl:value-of select="'ZeroRated'"/></xsl:when>           <!--  Exempt from tax -->
				<xsl:when test="$p1 = 'S'"><xsl:value-of select="'StandardRated'"/></xsl:when>  <!--  Standardrated -->
				<xsl:when test="$p1 = 'Z'"><xsl:value-of select="'ZeroRated'"/></xsl:when>      <!--  Zerorated -->
				<xsl:when test="$p1 = 'K'"><xsl:value-of select="'ZeroRated'"/></xsl:when>		<!--Intera-Community Supply-->
				<xsl:when test="$p1 = 'G'"><xsl:value-of select="'ZeroRated'"/></xsl:when>		<!--Export outside the EU-->
				<xsl:when test="$p1 = 'O'"><xsl:value-of select="'ZeroRated'"/></xsl:when>		<!--not subject to VAT-->
				<xsl:when test="$p1 = 'L'"><xsl:value-of select="'StandardRated'"/></xsl:when>           <!--  Canary Island, Ceuta or Melilla -->
				<xsl:when test="$p1 = 'M'"><xsl:value-of select="'StandardRated'"/></xsl:when> 
				<!--<xsl:when test="$p1 = 'H'"><xsl:value-of select="'StandardRated'"/></xsl:when>           --><!--  Higher rate --><!--
				<xsl:when test="$p1 = 'AA'"><xsl:value-of select="'StandardRated'"/></xsl:when>          --><!--  Lower rate -->
				<xsl:when test="contains($OIOUBLids, concat(',',$p1,','))"><xsl:value-of select="$p1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="'StandardRated'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="$ft1"/>
	</xsl:template>

	<!-- TaxSchemeIDConvert -->
	<xsl:template name="TaxSchemeIDConvertCreditNote">
		<xsl:param name="p1"/>
		<xsl:param name="SupplierParty"/>
		<xsl:variable name="OIOUBLids" select="',9,10,11,16,17,18,19,21,21a,21b,21c,21d,21e,21f,24,25,27,28,30,31,32,33,39,40,41,53,54,56,57,61,61a,62,63,69,70,71,72,75,76,77,79,85,86,87,91,94,94a,95,97,98,99,99a,100,108,109,110,110a,110b,110c,111,112,112a,112b,112c,112d,112e,112f,127,127a,127b,127c,128,130,133,134,135,136,137,138,139,140,142,146,151,152,VAT,0,'"/>
		<xsl:variable name="ft1">
			<xsl:choose>
			
				<!--<xsl:when test="$p1 = 'AAA'"><xsl:value-of select="'97'"/></xsl:when>  --><!--  Petroleum tax -->
				<xsl:when test="$p1 = 'VAT' and $SupplierParty = 'DK'"><xsl:value-of select="'63'"/></xsl:when><!--  VAT -->
				<xsl:when test="$p1 = 'VAT'"><xsl:value-of select="'VAT'"/></xsl:when> <!--  As an alternative to 63 -->
				<xsl:when test="contains($OIOUBLids, concat(',',$p1,','))"><xsl:value-of select="$p1"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="'VAT'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="$ft1"/>
	</xsl:template>


	<!-- TaxSchemeNameConvert -->
	<xsl:template name="TaxSchemeNameConvertCreditNote">
		<xsl:param name="p1"/>
		<xsl:variable name="defualtVAT" select="'Moms'"/>
		<xsl:variable name="ft1">
			<xsl:choose>
				<xsl:when test="$p1 = '63'"><xsl:value-of select="'Moms'"/></xsl:when>
				<xsl:when test="$p1 = 'VAT'"><xsl:value-of select="'VAT'"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="$defualtVAT"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="$ft1"/>
	</xsl:template>
</xsl:stylesheet>
