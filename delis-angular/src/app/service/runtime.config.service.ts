import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";

@Injectable()
export class RuntimeConfigService {

    constructor( private http: HttpClient ) {
    }

    getUrl() {
        this.getBaseUrl().subscribe(
            (data: {}) => {
                localStorage.setItem('url', data["PARAM_API_URL"]);
            }
        );
    }

    getBaseUrl() : Observable<any> {
        return this.http.get('assets/config/runtime.json').pipe(map(RuntimeConfigService.extractData));
    }

    private static extractData(res: Response) {
        return res || { };
    }
}
