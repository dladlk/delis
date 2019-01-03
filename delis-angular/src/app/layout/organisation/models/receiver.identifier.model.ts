import { OrganisationModel } from "./organisation.model";
import { IdentifierGroupModel } from "./identifier.group.model";

export class ReceiverIdentifierModel {

    id: number;
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