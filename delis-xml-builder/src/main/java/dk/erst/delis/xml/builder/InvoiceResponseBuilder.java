package dk.erst.delis.xml.builder;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;

import dk.erst.delis.task.document.parse.XSLTUtil;
import dk.erst.delis.xml.builder.data.DocumentResponse;
import dk.erst.delis.xml.builder.data.InvoiceResponseData;
import dk.erst.delis.xml.builder.data.Party;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.applicationresponse_2.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ConditionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyLegalEntityType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.StatusType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StatusReasonType;

@Slf4j
public class InvoiceResponseBuilder {

	private static String INVOCIE_RESPONSE_TEMPLATE_XSLT = "invoice-response.xslt";
	private oasis.names.specification.ubl.schema.xsd.applicationresponse_2.ObjectFactory arFactory;
	private oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ObjectFactory cbcFactory;
	private oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ObjectFactory cacFactory;
	private JAXBContext jaxbContext;
	private DatatypeFactory datatypeFactory;

	public InvoiceResponseBuilder() {
		arFactory = new oasis.names.specification.ubl.schema.xsd.applicationresponse_2.ObjectFactory();
		cbcFactory = new oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ObjectFactory();
		cacFactory = new oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ObjectFactory();

		try {
			jaxbContext = JAXBContext.newInstance(ApplicationResponseType.class.getPackage().getName());
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to build JAXBContext or DatatypeFactory: " + e.getMessage(), e);
		}
	}

	public void extractBasicData(InputStream rejectedXmlInput, OutputStream out) throws Exception {
		XSLTUtil.apply(InvoiceResponseBuilder.class.getResourceAsStream(INVOCIE_RESPONSE_TEMPLATE_XSLT), Paths.get(INVOCIE_RESPONSE_TEMPLATE_XSLT), rejectedXmlInput, out);
	}

	@SuppressWarnings("unchecked")
	public ApplicationResponseType parse(InputStream is) throws Exception {
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<ApplicationResponseType> jaxbElement = (JAXBElement<ApplicationResponseType>) unmarshaller.unmarshal(is);
		return jaxbElement.getValue();
	}

	public void parseAndEnrich(InputStream is, InvoiceResponseData d, OutputStream out) throws Exception {
		log.info("parseAndEnrich for " + d);
		ApplicationResponseType ar = parse(is);
		copyDataToType(d, ar);

		removeElementsAbsentInModel(ar);

		serialize(ar, out);
	}

	private void removeElementsAbsentInModel(ApplicationResponseType ar) {
		if (ar.getSenderParty() != null && !ar.getSenderParty().getPartyLegalEntity().isEmpty()) {
			cleanupPartyLegalEntity(ar.getSenderParty().getPartyLegalEntity().get(0));
		}
		if (ar.getSenderParty() != null && !ar.getSenderParty().getPartyLegalEntity().isEmpty()) {
			cleanupPartyLegalEntity(ar.getSenderParty().getPartyLegalEntity().get(0));
		}
	}

	private void cleanupPartyLegalEntity(PartyLegalEntityType partyLegalEntity) {
		partyLegalEntity.setCompanyID(null);
		partyLegalEntity.setCompanyLegalForm(null);
	}

	public void build(InvoiceResponseData d, OutputStream out) throws Exception {
		ApplicationResponseType ar = arFactory.createApplicationResponseType();
		copyDataToType(d, ar);
		serialize(ar, out);
	}

	private void serialize(ApplicationResponseType ar, OutputStream out) throws JAXBException, PropertyException {
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(arFactory.createApplicationResponse(ar), out);
	}

	private void copyDataToType(InvoiceResponseData d, ApplicationResponseType ar) {
		if (ar.getCustomizationID() == null)
			ar.setCustomizationID(cbcFactory.createCustomizationIDType());
		ar.getCustomizationID().setValue("urn:fdc:peppol.eu:poacc:trns:invoice_response:3");

		if (ar.getProfileID() == null)
			ar.setProfileID(cbcFactory.createProfileIDType());
		ar.getProfileID().setValue("urn:fdc:peppol.eu:poacc:bis:invoice_response:3");

		if (d.getId() != null) {
			if (ar.getID() == null)
				ar.setID(cbcFactory.createIDType());
			ar.getID().setValue(d.getId());
		}

		if (d.getIssueDate() != null) {
			if (ar.getIssueDate() == null)
				ar.setIssueDate(cbcFactory.createIssueDateType());
			ar.getIssueDate().setValue(datatypeFactory.newXMLGregorianCalendar(d.getIssueDate()));
		}

		if (d.getIssueTime() != null) {
			if (ar.getIssueTime() == null)
				ar.setIssueTime(cbcFactory.createIssueTimeType());
			ar.getIssueTime().setValue(datatypeFactory.newXMLGregorianCalendar(d.getIssueTime()));
		}

		if (d.getNote() != null) {
			if (ar.getNote().isEmpty()) {
				ar.getNote().add(cbcFactory.createNoteType());
			}
			ar.getNote().get(0).setValue(d.getNote());
		}

		if (d.getSenderParty() != null) {
			if (ar.getSenderParty() == null) {
				ar.setSenderParty(cacFactory.createPartyType());
			}
			fillPartyType(ar.getSenderParty(), d.getSenderParty());
		}
		if (d.getReceiverParty() != null) {
			if (ar.getReceiverParty() == null) {
				ar.setReceiverParty(cacFactory.createPartyType());
			}
			fillPartyType(ar.getReceiverParty(), d.getReceiverParty());
		}

		if (d.getDocumentResponse() != null) {
			if (ar.getDocumentResponse().isEmpty()) {
				ar.getDocumentResponse().add(cacFactory.createDocumentResponseType());
			}
			fillDocumentResponse(ar.getDocumentResponse().get(0), d.getDocumentResponse());
		}
	}

	private void fillDocumentResponse(DocumentResponseType drType, DocumentResponse dr) {
		if (drType.getResponse() == null) {
			drType.setResponse(cacFactory.createResponseType());
		}

		if (dr.getResponse() != null) {
			if (dr.getResponse().getResponseCode() != null) {
				if (drType.getResponse().getResponseCode() == null) {
					drType.getResponse().setResponseCode(cbcFactory.createResponseCodeType());
				}
				drType.getResponse().getResponseCode().setValue(dr.getResponse().getResponseCode());
			}

			if (dr.getResponse().getEffectiveDate() != null) {
				if (drType.getResponse().getEffectiveDate() == null) {
					drType.getResponse().setEffectiveDate(cbcFactory.createEffectiveDateType());
				}
				drType.getResponse().getEffectiveDate().setValue(datatypeFactory.newXMLGregorianCalendar(dr.getResponse().getEffectiveDate()));
			}

			if (dr.getResponse().getStatus() != null) {
				StatusType statusType;
				if (drType.getResponse().getStatus().isEmpty()) {
					statusType = cacFactory.createStatusType();
					drType.getResponse().getStatus().add(statusType);
				} else {
					statusType = drType.getResponse().getStatus().get(0);
				}

				if (statusType.getStatusReasonCode() == null) {
					statusType.setStatusReasonCode(cbcFactory.createStatusReasonCodeType());
				}
				statusType.getStatusReasonCode().setValue(dr.getResponse().getStatus().getStatusReasonCode());

				if (statusType.getStatusReason().isEmpty()) {
					StatusReasonType statusReasonType = cbcFactory.createStatusReasonType();
					statusType.getStatusReason().add(statusReasonType);
				}
				statusType.getStatusReason().get(0).setValue(dr.getResponse().getStatus().getStatusReason());

				ConditionType conditionType;
				if (statusType.getCondition().isEmpty()) {
					conditionType = cacFactory.createConditionType();
					statusType.getCondition().add(conditionType);
				} else {
					conditionType = statusType.getCondition().get(0);
				}

				if (conditionType.getAttributeID() == null) {
					conditionType.setAttributeID(cbcFactory.createAttributeIDType());
				}
				conditionType.getAttributeID().setValue(dr.getResponse().getStatus().getConditionAttributeID());

				if (conditionType.getDescription().isEmpty()) {
					DescriptionType descriptionType = cbcFactory.createDescriptionType();
					conditionType.getDescription().add(descriptionType);
				}
				conditionType.getDescription().get(0).setValue(dr.getResponse().getStatus().getConditionDescription());
			}
		}

		if (dr.getIssuerParty() != null) {
			if (drType.getIssuerParty() == null) {
				drType.setIssuerParty(cacFactory.createPartyType());
			}
			fillPartyType(drType.getIssuerParty(), dr.getIssuerParty());
		}

		if (dr.getDocumentReference() != null) {
			DocumentReferenceType refType;
			if (drType.getDocumentReference().isEmpty()) {
				refType = cacFactory.createDocumentReferenceType();
				drType.getDocumentReference().add(refType);
			} else {
				refType = drType.getDocumentReference().get(0);
			}

			if (dr.getDocumentReference().getId() != null) {
				if (refType.getID() == null) {
					refType.setID(cbcFactory.createIDType());
				}
				refType.getID().setValue(dr.getDocumentReference().getId());
			}

			if (dr.getDocumentReference().getIssueDate() != null) {
				if (refType.getIssueDate() == null) {
					refType.setIssueDate(cbcFactory.createIssueDateType());
				}
				refType.getIssueDate().setValue(datatypeFactory.newXMLGregorianCalendar(dr.getDocumentReference().getIssueDate()));
			}

			refType.setDocumentTypeCode(cbcFactory.createDocumentTypeCodeType());
			refType.getDocumentTypeCode().setValue(dr.getDocumentReference().getDocumentTypeCode());
		}
	}

	private void fillPartyType(PartyType partyType, Party party) {
		if (party.getEndpointID() != null) {
			partyType.setEndpointID(cbcFactory.createEndpointIDType());
			partyType.getEndpointID().setSchemeID(party.getEndpointID().getSchemeID());
			partyType.getEndpointID().setValue(party.getEndpointID().getValue());
		}
		if (party.getPartyIdentification() != null) {
			PartyIdentificationType partyIdentificationType = cacFactory.createPartyIdentificationType();
			partyType.getPartyIdentification().add(partyIdentificationType);
			partyIdentificationType.setID(cbcFactory.createIDType());
			partyIdentificationType.getID().setValue(party.getPartyIdentification().getId().getValue());
			partyIdentificationType.getID().setSchemeID(party.getPartyIdentification().getId().getSchemeID());
		}
		if (party.getPartyName() != null) {
			PartyNameType partyNameType = cacFactory.createPartyNameType();
			partyType.getPartyName().add(partyNameType);

			partyNameType.setName(cbcFactory.createNameType());
			partyNameType.getName().setValue(party.getPartyName());
		}

		if (party.getContact() != null) {
			partyType.setContact(cacFactory.createContactType());
			if (party.getContact().getName() != null) {
				partyType.getContact().setName(cbcFactory.createNameType());
				partyType.getContact().getName().setValue(party.getContact().getName());
			}

			if (party.getContact().getElectronicMail() != null) {
				partyType.getContact().setElectronicMail(cbcFactory.createElectronicMailType());
				partyType.getContact().getElectronicMail().setValue(party.getContact().getElectronicMail());
			}

			if (party.getContact().getTelephone() != null) {
				partyType.getContact().setTelephone(cbcFactory.createTelephoneType());
				partyType.getContact().getTelephone().setValue(party.getContact().getTelephone());
			}
		}

		if (party.getPartyLegalEntity() != null) {
			PartyLegalEntityType partyLegalEntityType = cacFactory.createPartyLegalEntityType();
			partyType.getPartyLegalEntity().add(partyLegalEntityType);
			partyLegalEntityType.setRegistrationName(cbcFactory.createRegistrationNameType());
			partyLegalEntityType.getRegistrationName().setValue(party.getPartyLegalEntity().getRegistrationName());
		}

	}
}
