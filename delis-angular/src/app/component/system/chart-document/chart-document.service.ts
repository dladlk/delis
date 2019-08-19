import { Injectable } from "@angular/core";
import { Range } from "../date-range/model/model";

@Injectable({
    providedIn: 'root'
})
export class ChartDocumentService {

    get range(): Range {
        return this._range;
    }

    updateRange(value: Range) {
        this._range = value;
    }

    private _range: Range;
}
