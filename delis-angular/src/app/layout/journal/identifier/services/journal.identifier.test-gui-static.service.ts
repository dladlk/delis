import {Injectable} from "@angular/core";
import {JournalIdentifierFilterProcessResultModel} from "../models/journal.identifier.filter.process.result.model";

@Injectable()
export class JournalIdentifierTestGuiStaticService {

    filterProcess(param: { filter: JournalIdentifierFilterProcessResultModel}) {
        return [];
    }

    getOneJournalIdentifierById(id: number) {
        return undefined;
    }
}