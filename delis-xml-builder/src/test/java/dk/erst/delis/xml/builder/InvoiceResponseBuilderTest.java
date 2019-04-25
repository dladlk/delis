package dk.erst.delis.xml.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import dk.erst.delis.xml.builder.data.Contact;
import dk.erst.delis.xml.builder.data.DocumentReference;
import dk.erst.delis.xml.builder.data.DocumentResponse;
import dk.erst.delis.xml.builder.data.EndpointID;
import dk.erst.delis.xml.builder.data.ID;
import dk.erst.delis.xml.builder.data.InvoiceResponseData;
import dk.erst.delis.xml.builder.data.Party;
import dk.erst.delis.xml.builder.data.PartyIdentification;
import dk.erst.delis.xml.builder.data.PartyLegalEntity;
import dk.erst.delis.xml.builder.data.Response;
import dk.erst.delis.xml.builder.data.ResponseStatus;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.applicationresponse_2.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;

@Slf4j
public class InvoiceResponseBuilderTest {

	private static InvoiceResponseBuilder builder;

	@BeforeClass
	public static void before() {
		long start = System.currentTimeMillis();
		builder = new InvoiceResponseBuilder();
		log.info("Initialized InvoiceResponseBuilder in " + (System.currentTimeMillis() - start) + " ms");
	}

	@Test
	public void testParse() throws Exception {
		ApplicationResponseType r;
		r = builder.parse(new FileInputStream("../delis-resources/examples/xml/BIS3_InvoiceResponse_Example.xml"));
		assertEquals("380", r.getDocumentResponse().get(0).getDocumentReference().get(0).getDocumentTypeCode().getValue());
	}

	@Test
	public void testExtract() throws Exception {

		String[] bis3Examples = new String[] { "BIS3_Invoice_Example_DK_Supplier_Master.xml", "BIS3_CreditNote_Example_DK_Supplier_NoErrors.xml" };
		for (int i = 0; i < bis3Examples.length; i++) {
			String example = bis3Examples[i];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			builder.extractBasicData(new FileInputStream("../delis-resources/examples/xml/" + example), baos);

			byte[] bytes = baos.toByteArray();

			ApplicationResponseType response = builder.parse(new ByteArrayInputStream(bytes));
			assertNotNull(response);

			DocumentReferenceType documentReferenceType = response.getDocumentResponse().get(0).getDocumentReference().get(0);
			if (i == 0) {
				assertEquals("20150483", documentReferenceType.getID().getValue());
				assertEquals("2018-04-01", documentReferenceType.getIssueDate().getValue().toXMLFormat());
				assertEquals("380", documentReferenceType.getDocumentTypeCode().getValue());
			} else {
				assertEquals("20150483", documentReferenceType.getID().getValue());
				assertEquals("2017-10-01", documentReferenceType.getIssueDate().getValue().toXMLFormat());
				assertEquals("381", documentReferenceType.getDocumentTypeCode().getValue());
			}
		}

	}

	@Test
	public void testBuild() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InvoiceResponseData d = generateFullData();

		builder.build(d, out);

		byte[] bytes = out.toByteArray();
		assertNotNull(bytes);

		String res = new String(bytes, StandardCharsets.UTF_8);
		assertNotNull(res);

		ApplicationResponseType r = builder.parse(new ByteArrayInputStream(bytes));
		assertEquals(d.getIssueDate(), r.getDocumentResponse().get(0).getDocumentReference().get(0).getIssueDate().getValue().toXMLFormat());
		
		/*
		 * Check that condition is not created for empty attributes
		 */
		d.getDocumentResponse().getResponse().getStatus().setConditionAttributeID("");
		d.getDocumentResponse().getResponse().getStatus().setConditionDescription("");
		out = new ByteArrayOutputStream();
		builder.build(d, out);
		r = builder.parse(new ByteArrayInputStream(out.toByteArray()));
		assertEquals(0, r.getDocumentResponse().get(0).getResponse().getStatus().get(0).getCondition().size());
	}

	private InvoiceResponseData generateFullData() {
		InvoiceResponseData d = new InvoiceResponseData();
		d.setId("imrid001");
		String issueDate = "2019-03-27";
		d.setIssueDate(issueDate);
		d.setIssueTime("12:00:00");
		d.setNote("Invoice is rejected");

		Party sender = Party.builder().build();
		sender.setEndpointID(EndpointID.builder().schemeID("0088").value("5790000012348").build());
		sender.setPartyIdentification(PartyIdentification.builder().id(ID.builder().schemeID("0184").value("DK88776655").build()).build());
		sender.setPartyLegalEntity(PartyLegalEntity.builder().registrationName("Test company").build());
		sender.setContact(Contact.builder().name("USER").electronicMail("user@mail.dk").telephone("+4512345678").build());
		d.setSenderParty(sender);

		Party receiver = Party.builder().build();
		receiver.setEndpointID(EndpointID.builder().schemeID("0088").value("5790000012348").build());
		receiver.setPartyIdentification(PartyIdentification.builder().id(ID.builder().schemeID("0184").value("DK48464114").build()).build());
		receiver.setPartyLegalEntity(PartyLegalEntity.builder().registrationName("Test company").build());
		d.setReceiverParty(receiver);

		ResponseStatus responseStatus = ResponseStatus.builder().statusReasonCode("REF").statusReason("VAT Reference not found").conditionAttributeID("BT-48").conditionDescription("EU123456789").build();
		Response response = Response.builder().effectiveDate("2019-01-01").responseCode("RE").status(responseStatus).build();
		DocumentReference documentReference = DocumentReference.builder().id("inv060").issueDate(issueDate).documentTypeCode("380").build();
		Party issuerParty = Party.builder().build();
		issuerParty.setPartyIdentification(PartyIdentification.builder().id(ID.builder().schemeID("0184").value("DK88776655").build()).build());
		issuerParty.setPartyName("TEST");
		d.setDocumentResponse(DocumentResponse.builder().response(response).documentReference(documentReference).issuerParty(issuerParty).build());
		return d;
	}

	@Test
	public void testBuildEmpty() throws Exception {
		InvoiceResponseData d = new InvoiceResponseData();
		OutputStream out = new ByteArrayOutputStream();
		builder.build(d, out);
	}

	@Test
	public void testParseAndEnrich() throws Exception {
		System.out.println("Check on empty initial ApplicationResponse and empty data:");
		
		String xmlString = "<ApplicationResponse xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2\"/>";
		InvoiceResponseData data = new InvoiceResponseData();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		builder.parseAndEnrich(new ByteArrayInputStream(xmlString.getBytes()), data, out);
		System.out.println("On ");
		System.out.println(new String(out.toByteArray(), StandardCharsets.UTF_8));
		assertTrue(out.toByteArray().length > 100);
		
		System.out.println("Check on empty AR but full data");
		data = generateFullData();
		out = new ByteArrayOutputStream();
		builder.parseAndEnrich(new ByteArrayInputStream(xmlString.getBytes()), data, out);
		System.out.println(new String(out.toByteArray(), StandardCharsets.UTF_8));
		assertTrue(out.toByteArray().length > 500);
		
		System.out.println("Check on both full data and AR");
		byte[] fullAR = out.toByteArray();
		builder.parseAndEnrich(new ByteArrayInputStream(fullAR), data, out);
		System.out.println(new String(out.toByteArray(), StandardCharsets.UTF_8));
		assertTrue(out.toByteArray().length > 500);
		
		System.out.println("Check on partly data and empty AR");
		
		InvoiceResponseData partlyData = new InvoiceResponseData();
		partlyData.setReceiverParty(Party.builder().build());
		partlyData.setSenderParty(Party.builder().build());
		partlyData.setDocumentResponse(DocumentResponse.builder().build());
		
		builder.parseAndEnrich(new ByteArrayInputStream(xmlString.getBytes()), partlyData, out);
		System.out.println(new String(out.toByteArray(), StandardCharsets.UTF_8));
		assertTrue(out.toByteArray().length > 500);
		
		partlyData.getSenderParty().setContact(Contact.builder().build());
		partlyData.getDocumentResponse().setResponse(Response.builder().build());
		partlyData.getDocumentResponse().setDocumentReference(DocumentReference.builder().build());
		builder.parseAndEnrich(new ByteArrayInputStream(xmlString.getBytes()), partlyData, out);
		System.out.println(new String(out.toByteArray(), StandardCharsets.UTF_8));
		assertTrue(out.toByteArray().length > 500);
	}
}
