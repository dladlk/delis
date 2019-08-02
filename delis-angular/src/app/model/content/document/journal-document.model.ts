import { DocumentModel } from './document.model';
import { OrganisationModel } from '../organisation/organisation.model';

export class JournalDocumentModel {

  id: number;
  createTime: string;
  organisation: OrganisationModel = new OrganisationModel();
  document: DocumentModel = new DocumentModel();
  success: boolean;
  type: string;
  message: string;
  durationMs: number;
}
