import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";

import { TokenService } from "./token.service";
import { RuntimeConfigService } from "./runtime.config.service";

@Injectable()
export class RefreshTokenService {

    private headers: HttpHeaders;
    private url: string;

    constructor( private http: HttpClient, private tokenService: TokenService, private configService: RuntimeConfigService ) {
        this.url = this.configService.getConfigUrl();
    }

    refreshTokenInit(token: string) :  Observable<any> {

        this.headers = new HttpHeaders({
            'Content-Type' : `application/json`,
            'Authorization' : `Basic dGVzdDp0ZXN0`
        });
        return this.http.post(this.url + "/oauth/token?grant_type=refresh_token&refresh_token=" + token, null, {
            headers : this.headers
        }).pipe(map(RefreshTokenService.extractData));

    }

    private static extractData(res: Response) {
        return res || { };
    }
}
