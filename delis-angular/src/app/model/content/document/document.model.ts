import { OrganisationModel } from '../organisation/organisation.model';
import { IdentifierModel } from '../identifier/identifier.model';
import { AbstractEntityModel } from "../abstract-entity.model";

export class DocumentModel extends AbstractEntityModel {

  updateTime: string;
  name: string;
  documentStatus: string;
  documentType: string;
  receiverIdRaw: string;
  receiverName: string;
  receiverCountry: string;
  senderIdRaw: string;
  senderName: string;
  senderCountry: string;
  ingoingDocumentFormat: string;
  documentId: string;
  documentDate: string;
  messageId: string;
  lastError: string;

  organisation: OrganisationModel = new OrganisationModel();
  receiverIdentifier: IdentifierModel = new IdentifierModel();
}
