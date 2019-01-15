import { Injectable } from "@angular/core";
import { JournalDocumentModel } from "../models/journal.document.model";
// import * as data from "../journal_document.json";

@Injectable()
export class JournalDocumentTestGuiStaticService {

    constructor() {}

    filterProcess(param: { filter: any }) : JournalDocumentModel[] {

        // let documents = Object.assign([], data.docs);
        let documents = JournalDocumentModel[5];
        return documents;
    }

    getOneJournalDocumentById(id: number) {
        return undefined;
    }
}
