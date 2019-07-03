import { OrganisationModel } from '../organisation/organisation.model';
import { AbstractEntityModel } from '../abstract-entity.model';

export class SendDocumentModel implements AbstractEntityModel {

  id: number;
  createTime: string;
  updateTime: string;
  documentStatus: string;
  documentType: string;
  receiverIdRaw: string;
  senderIdRaw: string;
  documentId: string;
  documentDate: string;
  sentMessageId: string;
  deliveredTime: Date;
  locked: boolean;

  organisation: OrganisationModel = new OrganisationModel();
}
