import { IdentifierModel } from './identifier.model';
import { OrganisationModel } from '../organisation/organisation.model';

export class JournalIdentifierModel {

  id: number;
  createTime: string;
  updateTime: string;
  organisation: OrganisationModel = new OrganisationModel();
  identifier: IdentifierModel = new IdentifierModel();
  message: string;
  durationMs: number;
}
