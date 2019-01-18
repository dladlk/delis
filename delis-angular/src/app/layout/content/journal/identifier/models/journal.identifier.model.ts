import { OrganisationModel } from "../../../organisation/models/organisation.model";
import { IdentifierModel } from "../../../identifier/models/identifier.model";

export class JournalIdentifierModel {

    id: number;
    createTime: string;
    updateTime: string;
    organisation: OrganisationModel;
    identifier: IdentifierModel;
    message: string;
    durationMs: number;

    constructor(model ?: any) {
        if (model) {
            this.id = model.id;
            this.createTime = model.createTime;
            this.updateTime = model.updateTime;
            this.organisation = model.organisation;
            this.identifier = model.identifier;
            this.message = model.message;
            this.durationMs = model.durationMs;
        }
    }
}
