import { Injectable } from "@angular/core";
import { JournalDocumentModel } from "../models/journal.document.model";
import * as data from "../journal_document.json";

@Injectable()
export class JournalDocumentTestGuiStaticService {

    constructor() {}

    filterProcess(param: { filter: any }) : JournalDocumentModel[] {

        let journals = Object.assign([], data.docs);



        return journals;
    }

    getOneJournalDocumentById(id: number) : JournalDocumentModel {
        let doc: JournalDocumentModel = data.docs.find(k => k.id === id);
        if (doc != null) {
            return doc;
        } else {
            return null;
        }
    }
}
