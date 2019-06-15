import { OrganisationModel } from "../../../organisation/models/organisation.model";

export class JournalOrganisationModel {

    id: number;
    createTime: string;
    updateTime: string;
    organisation: OrganisationModel = new OrganisationModel();
    message: string;
    durationMs: number;
}
