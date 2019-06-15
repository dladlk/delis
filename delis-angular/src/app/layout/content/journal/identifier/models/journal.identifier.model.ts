import { OrganisationModel } from "../../../organisation/models/organisation.model";
import { IdentifierModel } from "../../../identifier/models/identifier.model";

export class JournalIdentifierModel {

    id: number;
    createTime: string;
    updateTime: string;
    organisation: OrganisationModel = new OrganisationModel();
    identifier: IdentifierModel = new IdentifierModel();
    message: string;
    durationMs: number;
}
