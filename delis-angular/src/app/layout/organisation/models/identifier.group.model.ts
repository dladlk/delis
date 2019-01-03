import { OrganisationModel } from "./organisation.model";

export class IdentifierGroupModel {

    id: number;
    name: string;
    code: string;
    organisation: OrganisationModel;
}
