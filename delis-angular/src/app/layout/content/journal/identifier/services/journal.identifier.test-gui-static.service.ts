import { Injectable } from "@angular/core";
import { JournalIdentifierFilterProcessResultModel } from "../models/journal.identifier.filter.process.result.model";
import { JournalIdentifierModel } from "../models/journal.identifier.model";
import * as data from "../journal_identifier.json";

@Injectable()
export class JournalIdentifierTestGuiStaticService {

    constructor() {}

    filterProcess(param: { filter: JournalIdentifierFilterProcessResultModel}) : JournalIdentifierModel[] {
        let journals = Object.assign([], data.data);
        return journals;
    }

    getOneJournalIdentifierById(id: number) : JournalIdentifierModel {
        let doc: JournalIdentifierModel = data.data.find(k => k.id === id);
        if (doc != null) {
            return doc;
        } else {
            return null;
        }
    }
}
