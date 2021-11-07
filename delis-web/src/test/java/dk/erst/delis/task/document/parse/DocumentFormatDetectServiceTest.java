package dk.erst.delis.task.document.parse;

import static org.junit.Assert.assertEquals;

import dk.erst.delis.data.constants.DocumentFormatConst;
import dk.erst.delis.data.enums.document.DocumentFormat;
import org.junit.Test;

import dk.erst.delis.task.document.parse.data.DocumentInfo;
import dk.erst.delis.task.document.parse.data.DocumentInfoRootTag;
import dk.erst.delis.task.document.parse.data.DocumentProfile;

public class DocumentFormatDetectServiceTest {

	@Test
	public void testDefineDocumentFormat() {
		DocumentFormatDetectService s = new DocumentFormatDetectService();
		assertEquals(DocumentFormat.UNSUPPORTED, s.defineDocumentFormat(null));
		assertEquals(DocumentFormat.UNSUPPORTED, s.defineDocumentFormat(new DocumentInfo()));
	
		assertEquals(DocumentFormat.UNSUPPORTED, s.defineDocumentFormat(tc("CrossIndustryInvoice", "wrong", null)));
		assertEquals(DocumentFormat.UNSUPPORTED, s.defineDocumentFormat(tc("wrong", "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100", null)));
		assertEquals(DocumentFormat.UNSUPPORTED, s.defineDocumentFormat(tc("Invoice", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", null)));
		assertEquals(DocumentFormat.UNSUPPORTED, s.defineDocumentFormat(tc("Wrong", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "OIOUBL-2.02")));

		assertEquals(DocumentFormat.UNSUPPORTED, s.defineDocumentFormat(tc("Invoice", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "urn:cen.eu:en16931:2016")));
		assertEquals(DocumentFormat.BIS3_INVOICE, s.defineDocumentFormat(tc("Invoice", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "urn:cen.eu:en16931:2017")));

		assertEquals(DocumentFormat.UNSUPPORTED, s.defineDocumentFormat(tc("CreditNote", "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2", "urn:cen.eu:en16931:2016")));
		assertEquals(DocumentFormat.BIS3_CREDITNOTE, s.defineDocumentFormat(tc("CreditNote", "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2", "urn:cen.eu:en16931:2017")));
		
		assertEquals(DocumentFormat.CII, s.defineDocumentFormat(tc("CrossIndustryInvoice", "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100", null)));
		
		assertEquals(DocumentFormat.UNSUPPORTED, s.defineDocumentFormat(tc("CreditNote", "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2", "anything")));
		assertEquals(DocumentFormat.UNSUPPORTED, s.defineDocumentFormat(tc("Invoice", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "anything")));
		assertEquals(DocumentFormat.BIS3_INVOICE, s.defineDocumentFormat(tc("Invoice", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0")));
		assertEquals(DocumentFormat.BIS3_INVOICE, s.defineDocumentFormat(tc("Invoice", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0.1")));
		assertEquals(DocumentFormat.BIS3_INVOICE, s.defineDocumentFormat(tc("Invoice", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3anything")));
		assertEquals(DocumentFormat.BIS3_CREDITNOTE, s.defineDocumentFormat(tc("CreditNote", "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2", "urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0")));

		assertEquals(DocumentFormat.OIOUBL_CREDITNOTE, s.defineDocumentFormat(tc("CreditNote", "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2", "OIOUBL-2.02")));
		assertEquals(DocumentFormat.OIOUBL_INVOICE, s.defineDocumentFormat(tc("Invoice", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "OIOUBL-2.02")));

		assertEquals(DocumentFormat.BIS3_INVOICE_RESPONSE, s.defineDocumentFormat(tc("ApplicationResponse", "urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2", DocumentFormatConst.CUSTOMIZATION_BIS3_IR_STARTS_WITH)));
		assertEquals(DocumentFormat.BIS3_MESSAGE_LEVEL_RESPONSE, s.defineDocumentFormat(tc("ApplicationResponse", "urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2", DocumentFormatConst.CUSTOMIZATION_BIS3_MLR_STARTS_WITH)));

		assertEquals(DocumentFormat.BIS3_ORDER_ONLY, s.defineDocumentFormat(tc("Order", "urn:oasis:names:specification:ubl:schema:xsd:Order-2", DocumentFormatConst.CUSTOMIZATION_BIS3_ORDER_STARTS_WITH, DocumentFormatConst.PROFILE_BIS3_ORDER_ONLY)));
		assertEquals(DocumentFormat.BIS3_ORDER, s.defineDocumentFormat(tc("Order", "urn:oasis:names:specification:ubl:schema:xsd:Order-2", DocumentFormatConst.CUSTOMIZATION_BIS3_ORDER_STARTS_WITH, DocumentFormatConst.PROFILE_BIS3_ORDERING)));
		assertEquals(DocumentFormat.BIS3_ORDER_RESPONSE, s.defineDocumentFormat(tc("OrderResponse", "urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2", DocumentFormatConst.CUSTOMIZATION_BIS3_ORDER_RESPONSE_STARTS_WITH, DocumentFormatConst.PROFILE_BIS3_ORDERING)));
		assertEquals(DocumentFormat.BIS3_CATALOGUE_ONLY, s.defineDocumentFormat(tc("Catalogue", "urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2", DocumentFormatConst.CUSTOMIZATION_BIS3_CATALOGUE_STARTS_WITH, DocumentFormatConst.PROFILE_BIS3_CATALOGUE_ONLY)));
		assertEquals(DocumentFormat.BIS3_CATALOGUE_WITHOUT_RESPONSE, s.defineDocumentFormat(tc("Catalogue", "urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2", DocumentFormatConst.CUSTOMIZATION_BIS3_CATALOGUE_STARTS_WITH, DocumentFormatConst.PROFILE_BIS3_CATALOGUE_WITHOUT_RESPONSE)));
		assertEquals(DocumentFormat.BIS3_CATALOGUE_RESPONSE, s.defineDocumentFormat(tc("ApplicationResponse", "urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2", DocumentFormatConst.CUSTOMIZATION_BIS3_CATALOGUE_RESPONSE_STARTS_WITH)));
	}

	private DocumentInfo tc(String rootTag, String namespace, String customizationId, String profile) {
		DocumentInfo di = tc(rootTag, namespace, customizationId);
		if (profile != null) {
			DocumentProfile dp = new DocumentProfile();
			dp.setId(profile);
			di.setProfile(dp);
		}
		return di;
	}
	
	private DocumentInfo tc(String rootTag, String namespace, String customizationId) {
		DocumentInfo di;
		DocumentInfoRootTag root;
		di = new DocumentInfo();
		root = new DocumentInfoRootTag();
		di.setRoot(root);
		root.setRootTag(rootTag);
		root.setNamespace(namespace);
		
		di.setCustomizationID(customizationId);
		
		return di;
	}

}
