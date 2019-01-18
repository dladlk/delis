import { JournalOrganisationFilterProcessResult } from "../models/journal.organisation.filter.process.result";
import * as data from "../journal_organisation.json";
import { JournalOrganisationModel } from "../models/journal.organisation.model";

export class JournalOrganisationTestGuiStaticService {

    filterProcess(param: { filter: JournalOrganisationFilterProcessResult }) :JournalOrganisationModel[] {
        let journals = Object.assign([], data.docs);
        return journals;
    }

    getOneJournalOrganisationById(id: number) :JournalOrganisationModel {
        let doc: JournalOrganisationModel = data.docs.find(k => k.id === id);
        if (doc != null) {
            return doc;
        } else {
            return null;
        }
    }
}