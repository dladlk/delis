import { OrganisationModel } from "../../organisation/models/organisation.model";
import { IdentifierGroupModel } from "./identifier.group.model";

export class IdentifierModel {

    id: number;
    createTime: string;
    updateTime: string;
    value: string;
    type: string;
    uniqueValueType: string;
    status: string;
    publishingStatus: string;
    name: string;
    lastSyncOrganisationFactId: number;
    organisation: OrganisationModel;
    identifierGroup: IdentifierGroupModel;
}
