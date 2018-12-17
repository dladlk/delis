import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpHandler, HttpHeaders, HttpResponse } from '@angular/common/http';
import { catchError, map } from 'rxjs/operators';
import { ObservableInput } from 'rxjs';
import { TokenService } from './token.service';
import { GlobalEvents } from '../models/global.events';
import { HttpErrorService } from './http.error.service';

@Injectable()
export class HttpService extends HttpClient {

  protected token: TokenService;
  protected globalEvents: GlobalEvents;

  constructor(
    private httpHandler: HttpHandler,
    private httpErrorService: HttpErrorService) {
    super(httpHandler);

    this.globalEvents = new GlobalEvents();
    this.token = new TokenService();
  }

  private modifyHeaders(options: any = {}) {
    const headers = {
      'X-Requested-With' : 'XMLHttpRequest'
    };
    if (options.headers) {
      if (options.headers.get('Authorization')) {
        headers['Authorization'] = options.headers.get('Authorization');
      } else {
        if (this.token.getToken()) {
          headers['Authorization'] = 'Bearer ' + this.token.getToken();
        }
      }
    }
    options.headers = new HttpHeaders(headers);
  }

  get(url: string, options?: any): Observable<any> {
    this.modifyHeaders(options);
    return super.get(url, options).pipe(
      map<any, any>(res => this.returnRes(res)),
      catchError<any, any>((e) => this.errorHandler(e))
    );
  }

  post(url: string, body: any, options?: any): Observable<any> {
    this.modifyHeaders(options);
    return super.post(url, body, options).pipe(
      map<any, any>(res => this.returnRes(res)),
      catchError<any, any>((e) => this.errorHandler(e))
    );
  }

  put(url: string, body: any, options?: any): Observable<any> {
    this.modifyHeaders(options);
    return super.put(url, body, options).pipe(
      map<any, any>(res => this.returnRes(res)),
      catchError<any, any>((e) => this.errorHandler(e))
    );
  }

  delete(url: string, options?: any): Observable<any> {
    this.modifyHeaders(options);
    return super.delete(url, options).pipe(
      map<any, any>(res => this.returnRes(res)),
      catchError<any, any>((e) => this.errorHandler(e))
    );
  }

  private returnRes(res: HttpResponse<any>) {
    this.globalEvents.onRequestEnd.emit();
    return res;
  }

  private errorHandler(error: HttpErrorResponse): ObservableInput<{}> {
    this.catchErrors(error);
    throw(error.error);
  }

  catchErrors(error: HttpErrorResponse) {
    this.httpErrorService.setError(error);
    this.globalEvents.onRequestEnd.emit(true);
  }
}
