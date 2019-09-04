import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { LogoutService } from '../../service/system/logout.service';
import { SpinnerService } from '../../service/system/spinner.service';

@Injectable()
export class HttpEventInterceptor implements HttpInterceptor {

  constructor(private logoutService: LogoutService, private spinnerService: SpinnerService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.spinnerService.show();
    request = request.clone({params: request.params.set('locale_lang', localStorage.getItem('locale_lang'))});
    return next.handle(request).pipe(
      tap(
        event => {
          if (event instanceof HttpResponse) {
            this.spinnerService.hide();
          }
        },
        error => {
          this.spinnerService.hide();
          if (error instanceof HttpErrorResponse) {
            if (String(error.status) === '401') {
              this.logoutService.logout();
            }
          }
        }
      )
    );
  }
}
