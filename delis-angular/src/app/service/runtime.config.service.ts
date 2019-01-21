import { Injectable } from "@angular/core";
import { HttpRestService } from "./http.rest.service";
import { environment } from "../../environments/environment";

@Injectable()
export class RuntimeConfigService {

    private env = environment;
    private url = this.env.api_url;
    private config: string;

    constructor( private http: HttpRestService ) {
    }

    getUrl() {
        this.http.methodInnerGet('assets/config/runtime.json').subscribe(
            (data: {}) => {
                localStorage.setItem('url', data["PARAM_API_URL"]);
            }
        );
    }

    getConfigUrl() : string {
        this.config = localStorage.getItem('url');
        if (this.config !== '${API_URL}') {
            return this.config;
        } else {
            return this.url;
        }
    }
}
