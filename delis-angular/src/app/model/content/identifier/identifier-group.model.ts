import { OrganisationModel } from '../organisation/organisation.model';

export class IdentifierGroupModel {

  id: number;
  createTime: string;
  updateTime: string;
  name: string;
  code: string;
  organisation: OrganisationModel = new OrganisationModel();
}
