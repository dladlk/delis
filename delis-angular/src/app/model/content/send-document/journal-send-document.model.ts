import { OrganisationModel } from '../organisation/organisation.model';
import { SendDocumentModel } from './send-document.model';

export class JournalSendDocumentModel {

  id: number;
  createTime: string;
  success: boolean;
  type: string;
  message: string;
  durationMs: number;
  organisation: OrganisationModel = new OrganisationModel();
  sendDocument: SendDocumentModel = new SendDocumentModel();
}
