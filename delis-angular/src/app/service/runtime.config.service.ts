import { Injectable } from "@angular/core";
import { HttpRestService } from "./http.rest.service";
import { environment } from "../../environments/environment";

@Injectable()
export class RuntimeConfigService {

    private env = environment;
    private url = this.env.api_url;
    private username: string;
    private config: string;
    private LOCALE_URL = "url";
    private LOCALE_USERNAME = "current_user";

    constructor( private http: HttpRestService ) {
    }

    getUrl() {
        this.http.methodInnerGet('assets/config/runtime.json').subscribe(
            (data: {}) => {
                localStorage.setItem(this.LOCALE_URL, data["PARAM_API_URL"]);
                localStorage.setItem(this.LOCALE_USERNAME, data["PARAM_USERNAME"]);
            }
        );
    }

    getConfigUrl() : string {
        this.config = localStorage.getItem(this.LOCALE_URL);
        if (this.config !== '${API_URL}') {
            return this.config;
        } else {
            return this.url;
        }
    }

    getCurrentUser() : string {
        this.username = localStorage.getItem(this.LOCALE_USERNAME);
        if (this.username !== '${USERNAME}') {
            return this.username;
        } else {
            this.username = localStorage.getItem("username");
            if (this.username) {
                return this.username;
            } else {
                return "User";
            }
        }
    }

    resetConfigUrl() {
        localStorage.removeItem(this.LOCALE_URL);
    }

    resetCurrentUser() {
        localStorage.removeItem(this.LOCALE_USERNAME);
        localStorage.removeItem("username");
    }
}
