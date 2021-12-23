import { OrganisationModel } from '../organisation/organisation.model';
import { AbstractEntityModel } from '../abstract-entity.model';

export class IdentifierGroupModel extends AbstractEntityModel {

  id: number;
  createTime: string;
  updateTime: string;
  name: string;
  code: string;
  organisation: OrganisationModel = new OrganisationModel();
}
