import { Injectable } from "@angular/core";
import { JournalDocumentModel } from "../models/journal.document.model";
import { EnumFieldModel } from "../models/enum.field.model";
import * as data from "../journal_document.json";
import * as dataEnum from '../enum.json';

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

    generateEnumFields() : EnumFieldModel {
        let enumFields: EnumFieldModel = dataEnum.entityEnumInfo;
        return enumFields;
    }
}
