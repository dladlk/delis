package dk.erst.delis.xml.builder;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.io.input.CloseShieldInputStream;

import com.helger.ubl21.UBL21Reader;
import com.helger.ubl21.UBL21Writer;

import dk.erst.delis.task.document.parse.XSLTUtil;
import dk.erst.delis.xml.builder.data.ApplicationResponseData;
import dk.erst.delis.xml.builder.data.Contact;
import dk.erst.delis.xml.builder.data.Contact.ContactBuilder;
import dk.erst.delis.xml.builder.data.DocumentReference;
import dk.erst.delis.xml.builder.data.DocumentReference.DocumentReferenceBuilder;
import dk.erst.delis.xml.builder.data.DocumentResponse;
import dk.erst.delis.xml.builder.data.DocumentResponse.DocumentResponseBuilder;
import dk.erst.delis.xml.builder.data.EndpointID;
import dk.erst.delis.xml.builder.data.EndpointID.EndpointIDBuilder;
import dk.erst.delis.xml.builder.data.ID;
import dk.erst.delis.xml.builder.data.LineResponse;
import dk.erst.delis.xml.builder.data.LineResponse.LineResponseBuilder;
import dk.erst.delis.xml.builder.data.Party;
import dk.erst.delis.xml.builder.data.Party.PartyBuilder;
import dk.erst.delis.xml.builder.data.PartyIdentification;
import dk.erst.delis.xml.builder.data.PartyIdentification.PartyIdentificationBuilder;
import dk.erst.delis.xml.builder.data.PartyLegalEntity;
import dk.erst.delis.xml.builder.data.Response;
import dk.erst.delis.xml.builder.data.Response.ResponseBuilder;
import dk.erst.delis.xml.builder.data.ResponseStatus;
import dk.erst.delis.xml.builder.data.ResponseStatus.ResponseStatusBuilder;
import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ConditionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ContactType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.LineResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyLegalEntityType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.StatusType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CompanyIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CompanyLegalFormType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.StatusReasonType;

public class ApplicationResponseBuilder {

	private static String INVOCIE_RESPONSE_TEMPLATE_XSLT = "invoice-response.xslt";
	private oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ObjectFactory arFactory;
	private oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ObjectFactory cbcFactory;
	private oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ObjectFactory cacFactory;
//	private JAXBContext jaxbContext;
	private DatatypeFactory datatypeFactory;

	public ApplicationResponseBuilder() {
		arFactory = new oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ObjectFactory();
		cbcFactory = new oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ObjectFactory();
		cacFactory = new oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ObjectFactory();
//
		try {
//			jaxbContext = JAXBContext.newInstance(ApplicationResponseType.class.getPackage().getName());
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to build JAXBContext or DatatypeFactory: " + e.getMessage(), e);
		}
	}

	public void extractBasicData(InputStream rejectedXmlInput, OutputStream out) throws Exception {
		XSLTUtil.apply(ApplicationResponseBuilder.class.getResourceAsStream(INVOCIE_RESPONSE_TEMPLATE_XSLT), Paths.get(INVOCIE_RESPONSE_TEMPLATE_XSLT), new CloseShieldInputStream(rejectedXmlInput), out);
	}

//	@SuppressWarnings("unchecked")
	public ApplicationResponseType parse(InputStream is) throws Exception {
		return UBL21Reader.applicationResponse().setUseSchema(false).read(is);
//		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//		JAXBElement<ApplicationResponseType> jaxbElement = (JAXBElement<ApplicationResponseType>) unmarshaller.unmarshal(is);
//		return jaxbElement.getValue();
	}

	public void parseAndEnrich(InputStream is, ApplicationResponseData d, OutputStream out) throws Exception {
		ApplicationResponseType ar = parse(is);
		copyDataToType(d, ar);
		
		boolean isMLR = d.getCustomizationID() != null && d.getCustomizationID().startsWith("urn:fdc:peppol.eu:poacc:trns:mlr");

		removeElementsAbsentInModel(ar, isMLR);

		serializeType(ar, out);
	}

	private void removeElementsAbsentInModel(ApplicationResponseType ar, boolean isMLR) {
		cleanupPartyLegalEntity(ar.getSenderParty(), isMLR);
		cleanupPartyLegalEntity(ar.getReceiverParty(), isMLR);
		if (isMLR) {
			List<DocumentResponseType> documentResponse = ar.getDocumentResponse();
			for (DocumentResponseType documentResponseType : documentResponse) {
				documentResponseType.setIssuerParty(null);
				List<DocumentReferenceType> documentReferenceList = documentResponseType.getDocumentReference();
				for (DocumentReferenceType documentReferenceType : documentReferenceList) {
					IssueDateType issueDate = null;
					documentReferenceType.setIssueDate(issueDate);
				}
			}
		}
	}

	private void cleanupPartyLegalEntity(PartyType party, boolean isMLR) {
		if (party == null) {
			return;
		}
		if (isMLR) {
			party.setPartyLegalEntity(null);
			party.setPartyIdentification(null);
		}
		if (party.getPartyLegalEntity().isEmpty()) {
			return;
		}
		PartyLegalEntityType partyLegalEntity = party.getPartyLegalEntity().get(0);
		partyLegalEntity.setCompanyID((CompanyIDType)null);
		partyLegalEntity.setCompanyLegalForm((CompanyLegalFormType)null);
	}

	public void build(ApplicationResponseData d, OutputStream out) throws Exception {
		ApplicationResponseType ar = arFactory.createApplicationResponseType();
		copyDataToType(d, ar);
		serializeType(ar, out);
	}

	public void serializeType(ApplicationResponseType ar, OutputStream out) throws JAXBException, PropertyException {
		UBL21Writer.applicationResponse().setFormattedOutput(true).setUseSchema(false).write(ar, out);
//		Marshaller marshaller = jaxbContext.createMarshaller();
//		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//		marshaller.marshal(arFactory.createApplicationResponse(ar), out);
	}

	public ApplicationResponseData extractDataFromType(ApplicationResponseType ar) {
		ApplicationResponseData d = new ApplicationResponseData();
		if (ar.getUBLVersionID() != null) {
			d.setUblVersionID(ar.getUBLVersionID().getValue());
		}
		if (ar.getCustomizationID() != null) {
			d.setCustomizationID(ar.getCustomizationID().getValue());
		}
		if (ar.getProfileID() != null) {
			d.setProfileID(ar.getProfileID().getValue());
		}
		if (ar.getID() != null) {
			d.setId(ar.getID().getValue());
		}
		if (ar.getIssueDate() != null) {
			d.setIssueDate(ar.getIssueDate().getValue().toXMLFormat());
		}
		if (ar.getIssueTime() != null) {
			d.setIssueTime(ar.getIssueTime().getValue().toXMLFormat());
		}
		if (ar.getNote() != null && !ar.getNote().isEmpty()) {
			d.setNote(ar.getNote().get(0).getValue());
		}
		if (ar.getSenderParty() != null) {
			d.setSenderParty(extractParty(ar.getSenderParty()));
		}
		if (ar.getReceiverParty() != null) {
			d.setReceiverParty(extractParty(ar.getReceiverParty()));
		}
		if (ar.getDocumentResponse() != null && !ar.getDocumentResponse().isEmpty()) {
			DocumentResponseType drt = ar.getDocumentResponse().get(0);
			DocumentResponseBuilder drb = DocumentResponse.builder();

			ResponseType responseType = drt.getResponse();
			if (responseType != null) {
				drb.response(extractResponse(responseType));
			}

			if (drt.getDocumentReference() != null && !drt.getDocumentReference().isEmpty()) {
				DocumentReferenceBuilder rb = DocumentReference.builder();

				DocumentReferenceType rt = drt.getDocumentReference().get(0);
				if (rt.getID() != null) {
					rb.id(rt.getID().getValue());
				}
				if (rt.getIssueDate() != null) {
					rb.issueDate(rt.getIssueDate().getValue().toXMLFormat());
				}
				if (rt.getDocumentTypeCode() != null) {
					rb.documentTypeCode(rt.getDocumentTypeCode().getValue());
					rb.documentTypeCodeListId(rt.getDocumentTypeCode().getListID());
				}
				if (rt.getVersionID() != null) {
					rb.versionId(rt.getVersionID().getValue());
				}
				drb.documentReference(rb.build());
			}

			if (drt.getLineResponse() != null && !drt.getLineResponse().isEmpty()) {
				List<LineResponse> lineResponseList = new ArrayList<>();
				List<LineResponseType> lineResponse = drt.getLineResponse();
				for (LineResponseType lineResponseType : lineResponse) {
					LineResponseBuilder lrb = LineResponse.builder();

					if (lineResponseType.getLineReference() != null && lineResponseType.getLineReference().getLineID() != null) {
						lrb.lineId(lineResponseType.getLineReference().getLineID().getValue());
					}

					if (lineResponseType.getResponse() != null && !lineResponseType.getResponse().isEmpty()) {
						lrb.response(extractResponse(lineResponseType.getResponse().get(0)));
					}

					lineResponseList.add(lrb.build());
				}
				drb.lineResponse(lineResponseList);
			}

			if (drt.getIssuerParty() != null) {
				drb.issuerParty(extractParty(drt.getIssuerParty()));
			}
			if (drt.getRecipientParty() != null) {
				drb.recipientParty(extractParty(drt.getRecipientParty()));
			}

			d.setDocumentResponse(drb.build());
		}
		return d;
	}

	public Response extractResponse(ResponseType responseType) {
		ResponseBuilder rb = Response.builder();
		if (responseType.getResponseCode() != null) {
			rb.responseCode(responseType.getResponseCode().getValue());
			if (responseType.getResponseCode().getListID() != null) {
				rb.responseCodeListId(responseType.getResponseCode().getListID());
			}
		}
		if (responseType.getDescription() != null && !responseType.getDescription().isEmpty()) {
			rb.responseDescription(responseType.getDescription().get(0).getValue());
		}
		if (responseType.getEffectiveDate() != null) {
			rb.effectiveDate(responseType.getEffectiveDate().getValue().toXMLFormat());
		}
		if (responseType.getStatus() != null && !responseType.getStatus().isEmpty()) {
			List<StatusType> list = responseType.getStatus();

			ArrayList<ResponseStatus> rs = new ArrayList<ResponseStatus>();
			for (StatusType statusType : list) {
				ResponseStatusBuilder rsb = ResponseStatus.builder();

				if (statusType.getStatusReasonCode() != null) {
					rsb.statusReasonCode(statusType.getStatusReasonCode().getValue());
					rsb.statusReasonCodeListId(statusType.getStatusReasonCode().getListID());
				}
				if (statusType.getStatusReason() != null && !statusType.getStatusReason().isEmpty()) {
					rsb.statusReason(statusType.getStatusReason().get(0).getValue());
				}

				if (statusType.getCondition() != null && !statusType.getCondition().isEmpty()) {
					ConditionType ct = statusType.getCondition().get(0);
					rsb.conditionAttributeID(ct.getAttributeID().getValue());
					if (ct.getDescription() != null && !ct.getDescription().isEmpty()) {
						rsb.conditionDescription(ct.getDescription().get(0).getValue());
					}
				}

				rs.add(rsb.build());
			}
			rb.status(rs);
		}
		Response response = rb.build();
		return response;
	}

	private Party extractParty(PartyType t) {
		PartyBuilder b = Party.builder();
		if (t.getEndpointID() != null) {
			EndpointIDBuilder eb = EndpointID.builder();
			eb.value(t.getEndpointID().getValue());
			eb.schemeID(t.getEndpointID().getSchemeID());
			b.endpointID(eb.build());
		}
		if (t.getPartyIdentification() != null && !t.getPartyIdentification().isEmpty()) {
			PartyIdentificationType pit = t.getPartyIdentification().get(0);
			PartyIdentificationBuilder pib = PartyIdentification.builder();

			if (pit.getID() != null) {
				pib.id(ID.builder().value(pit.getID().getValue()).schemeID(pit.getID().getSchemeID()).build());
			}
			b.partyIdentification(pib.build());
		}
		if (t.getPartyName() != null && !t.getPartyName().isEmpty()) {
			b.partyName(t.getPartyName().get(0).getName().getValue());
		}
		if (t.getPartyLegalEntity() != null && !t.getPartyLegalEntity().isEmpty()) {
			PartyLegalEntityType plet = t.getPartyLegalEntity().get(0);
			b.partyLegalEntity(PartyLegalEntity.builder().registrationName(plet.getRegistrationName().getValue()).build());
		}
		ContactType ct = t.getContact();
		if (ct != null) {
			ContactBuilder cb = Contact.builder();
			if (ct.getName() != null) {
				cb.name(ct.getName().getValue());
			}
			if (ct.getTelephone() != null) {
				cb.telephone(ct.getTelephone().getValue());
			}
			if (ct.getElectronicMail() != null) {
				cb.electronicMail(ct.getElectronicMail().getValue());
			}
			b.contact(cb.build());
		}
		return b.build();
	}

	private void copyDataToType(ApplicationResponseData d, ApplicationResponseType ar) {
		if (d.getUblVersionID() != null) {
			if (ar.getUBLVersionID() == null)
				ar.setUBLVersionID(cbcFactory.createUBLVersionIDType());
			ar.getUBLVersionID().setValue(d.getUblVersionID());
		}

		if (ar.getCustomizationID() == null)
			ar.setCustomizationID(cbcFactory.createCustomizationIDType());
		ar.getCustomizationID().setValue(d.getCustomizationID());

		if (ar.getProfileID() == null)
			ar.setProfileID(cbcFactory.createProfileIDType());
		ar.getProfileID().setValue(d.getProfileID());

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
		Response response = dr.getResponse();

		if (response != null) {
			drType.setResponse(fillResponse(drType.getResponse(), response));
		}

		if (dr.getIssuerParty() != null) {
			if (drType.getIssuerParty() == null) {
				drType.setIssuerParty(cacFactory.createPartyType());
			}
			fillPartyType(drType.getIssuerParty(), dr.getIssuerParty());
		}
		if (dr.getRecipientParty() != null) {
			if (drType.getRecipientParty() == null) {
				drType.setRecipientParty(cacFactory.createPartyType());
			}
			fillPartyType(drType.getRecipientParty(), dr.getRecipientParty());
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
			refType.getDocumentTypeCode().setListID(dr.getDocumentReference().getDocumentTypeCodeListId());

			if (dr.getDocumentReference().getVersionId() != null) {
				if (refType.getVersionID() == null) {
					refType.setVersionID(cbcFactory.createVersionIDType());
				}
				refType.getVersionID().setValue(dr.getDocumentReference().getVersionId());
			}
		}

		if (dr.getLineResponse() != null) {
			List<LineResponse> lineResponseList = dr.getLineResponse();
			for (int i = 0; i < lineResponseList.size(); i++) {
				LineResponse lineResponse = lineResponseList.get(i);
				if (drType.getLineResponse().size() <= i) {
					drType.getLineResponse().add(cacFactory.createLineResponseType());
				}
				LineResponseType lrt = drType.getLineResponse().get(i);
				if (lrt.getLineReference() == null) {
					lrt.setLineReference(cacFactory.createLineReferenceType());
				}
				if (lrt.getLineReference().getLineID() == null) {
					lrt.getLineReference().setLineID(cbcFactory.createLineIDType());
				}
				lrt.getLineReference().getLineID().setValue(lineResponse.getLineId());

				if (lineResponse.getResponse() != null) {
					if (lrt.getResponse().isEmpty()) {
						lrt.getResponse().add(cacFactory.createResponseType());
					}
					fillResponse(lrt.getResponse().get(0), lineResponse.getResponse());
				}
			}
		}
	}

	public ResponseType fillResponse(ResponseType responseType, Response response) {
		if (responseType == null) {
			responseType = cacFactory.createResponseType();
		}
		if (response != null) {
			if (response.getResponseCode() != null) {
				if (responseType.getResponseCode() == null) {
					responseType.setResponseCode(cbcFactory.createResponseCodeType());
				}
				responseType.getResponseCode().setValue(response.getResponseCode());
				responseType.getResponseCode().setListID(response.getResponseCodeListId());
			}
			if (response.getResponseDescription() != null) {
				if (responseType.getDescription().isEmpty()) {
					responseType.getDescription().add(cbcFactory.createDescriptionType());
				}
				responseType.getDescription().get(0).setValue(response.getResponseDescription());
			}

			if (response.getEffectiveDate() != null) {
				if (responseType.getEffectiveDate() == null) {
					responseType.setEffectiveDate(cbcFactory.createEffectiveDateType());
				}
				responseType.getEffectiveDate().setValue(datatypeFactory.newXMLGregorianCalendar(response.getEffectiveDate()));
			}

			ArrayList<ResponseStatus> statusList = response.getStatus();
			if (statusList != null) {
				for (int i = 0; i < statusList.size(); i++) {
					ResponseStatus responseStatus = statusList.get(i);
					ResponseStatus status = responseStatus;
					StatusType statusType;
					if (responseType.getStatus().isEmpty() || responseType.getStatus().size() <= i) {
						statusType = cacFactory.createStatusType();
						responseType.getStatus().add(statusType);
					} else {
						statusType = responseType.getStatus().get(i);
					}

					if (status.getStatusReasonCode() != null) {
						if (statusType.getStatusReasonCode() == null) {
							statusType.setStatusReasonCode(cbcFactory.createStatusReasonCodeType());
						}
						statusType.getStatusReasonCode().setValue(status.getStatusReasonCode());
						statusType.getStatusReasonCode().setListID(status.getStatusReasonCodeListId());
					}

					if (status.getStatusReason() != null) {
						if (statusType.getStatusReason().isEmpty()) {
							StatusReasonType statusReasonType = cbcFactory.createStatusReasonType();
							statusType.getStatusReason().add(statusReasonType);
						}
						statusType.getStatusReason().get(0).setValue(status.getStatusReason());
					}

					if (status.isFilledCondition()) {
						ConditionType conditionType;
						if (statusType.getCondition().isEmpty()) {
							conditionType = cacFactory.createConditionType();
							statusType.getCondition().add(conditionType);
						} else {
							conditionType = statusType.getCondition().get(i);
						}

						if (conditionType.getAttributeID() == null) {
							conditionType.setAttributeID(cbcFactory.createAttributeIDType());
						}
						conditionType.getAttributeID().setValue(status.getConditionAttributeID());

						if (conditionType.getDescription().isEmpty()) {
							DescriptionType descriptionType = cbcFactory.createDescriptionType();
							conditionType.getDescription().add(descriptionType);
						}
						conditionType.getDescription().get(0).setValue(status.getConditionDescription());
					}
				}
			}
		}
		return responseType;
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
