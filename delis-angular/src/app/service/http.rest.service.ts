import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable()
export class HttpRestService {

    constructor(private http: HttpClient) {
    }

    private headers: HttpHeaders;

    private static extractData(res: Response) {
        return res || {};
    }

    methodGet(url: string, params: any, token: any): Observable<any> {
        this.headers = new HttpHeaders({
            'Authorization': 'Bearer ' + token
        });
        if (params !== null) {
            return this.http.get(url, {
                headers: this.headers,
                params: params
            })
                .pipe(map(HttpRestService.extractData));
        } else {
            return this.http.get(url, {headers: this.headers}).pipe(map(HttpRestService.extractData));
        }
    }

    methodGetOneById(url: string, params: any, token: any, id: number): Observable<any> {
        this.headers = new HttpHeaders({
            'Authorization': 'Bearer ' + token
        });
        if (params !== null) {
            return this.http.get(url + '/' + id, {
                headers: this.headers,
                params: params
            })
                .pipe(map(HttpRestService.extractData));
        } else {
            return this.http.get(url + '/' + id, {headers: this.headers}).pipe(map(HttpRestService.extractData));
        }
    }

    methodOpenGet(url: string): Observable<any> {
        return this.http.get(url).pipe(map(HttpRestService.extractData));
    }

    methodGetOne(url: string, id: any, token: any): Observable<any> {
        this.headers = new HttpHeaders({
            'Authorization': 'Bearer ' + token
        });
        return this.http.get(url + '/' + id, {headers: this.headers}).pipe(map(HttpRestService.extractData));
    }

    methodInnerGet(url: string): Observable<any> {
        return this.http.get(url).pipe(map(HttpRestService.extractData));
    }

    methodPost(url: string): Observable<any> {
        this.headers = new HttpHeaders({
            'Content-Type': `application/json`,
            'Authorization': `Basic dGVzdDp0ZXN0`
        });
        return this.http.post(url, null, {
            headers: this.headers
        }).pipe(map(HttpRestService.extractData));
    }

    methodPostModel(url: string, model: any, token: any): Observable<any> {
        this.headers = new HttpHeaders({
            'Content-Type': `application/json`,
            'Authorization': 'Bearer ' + token
        });
        return this.http.post(url, model, {
            headers: this.headers
        }).pipe(map(HttpRestService.extractData));
    }

    methodDelete(url: string, token: any): Observable<any> {
        this.headers = new HttpHeaders({
            'Authorization': 'Bearer ' + token
        });
        return this.http.delete(url, {
            headers: this.headers
        }).pipe(map(HttpRestService.extractData));
    }

    downloadFileByDocumentAndDocumentBytes(url: string, token: any): Observable<any> {
        this.headers = new HttpHeaders({
            'Authorization': 'Bearer ' + token
        });
        return this.http.get(url, {
            headers: this.headers,
            observe: 'response',
            responseType: 'arraybuffer'
        });
    }

    generateAndDownloadFileByDocument(url: string, body: any, token: any): Observable<any> {
        this.headers = new HttpHeaders({
            'Authorization': 'Bearer ' + token
        });
        return this.http.post(url, body, {
            headers: this.headers,
            observe: 'response',
            responseType: 'arraybuffer'
        });
    }
}
