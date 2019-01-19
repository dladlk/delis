import { OrganisationModel } from "../../../organisation/models/organisation.model";

export class JournalOrganisationModel {

    id: number;
    createTime: string;
    updateTime: string;
    organisation: OrganisationModel;
    message: string;
    durationMs: number;

    constructor(model ?: any) {
        if (model) {
            this.id = model.id;
            this.createTime = model.createTime;
            this.updateTime = model.updateTime;
            this.organisation = model.organisation;
            this.message = model.message;
            this.durationMs = model.durationMs;
        }
    }
}
