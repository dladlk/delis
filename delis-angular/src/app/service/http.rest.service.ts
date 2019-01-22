import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";

@Injectable()
export class HttpRestService {

    private headers: HttpHeaders;

    constructor( private http: HttpClient ) {}

    methodGet(url : string, params: any, token: any) : Observable<any> {
        this.headers = new HttpHeaders({
            'Authorization' : token
        });
        if (params !== null) {
            return this.http.get(url, {
                headers : this.headers,
                params: params})
                .pipe(map(HttpRestService.extractData));
        } else {
            return this.http.get(url, {headers : this.headers}).pipe(map(HttpRestService.extractData));
        }
    }

    methodOpenGet(url : string) : Observable<any> {
        return this.http.get(url).pipe(map(HttpRestService.extractData));
    }

    methodGetOne(url : string, id: any, token: any) : Observable<any> {
        this.headers = new HttpHeaders({
            'Authorization' : token
        });
        return this.http.get(url + '/' + id, {headers : this.headers}).pipe(map(HttpRestService.extractData));
    }

    methodInnerGet(url : string) : Observable<any> {
        return this.http.get(url).pipe(map(HttpRestService.extractData));
    }

    methodPost(url: string, body: any) : Observable<any> {
        this.headers = new HttpHeaders({
            'Content-Type' : `application/json`
        });
        return this.http.post(url, body, {
            headers : this.headers
        }).pipe(map(HttpRestService.extractData));
    }

    private static extractData(res: Response) {
        return res || { };
    }
}
