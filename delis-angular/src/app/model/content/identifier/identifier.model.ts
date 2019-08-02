import { OrganisationModel } from '../organisation/organisation.model';
import { IdentifierGroupModel } from './identifier-group.model';
import { AbstractEntityModel } from "../abstract-entity.model";

export class IdentifierModel extends AbstractEntityModel {

  updateTime: string;
  value: string;
  type: string;
  uniqueValueType: string;
  status: string;
  publishingStatus: string;
  name: string;
  lastSyncOrganisationFactId: number;
  organisation: OrganisationModel = new OrganisationModel();
  identifierGroup: IdentifierGroupModel = new IdentifierGroupModel();
}
