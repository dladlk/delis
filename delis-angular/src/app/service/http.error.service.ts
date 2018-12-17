import { Injectable, Injector } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationService } from './notification.service';
import { TokenService } from './token.service';
import { Notification } from '../models/notification.model';
import { NOTIFICATION_TYPE } from '../models/notification.const';

@Injectable()
export class HttpErrorService {

  constructor(
    private notificationsService: NotificationService,
    private token: TokenService,
    private injector: Injector) {
  }

  setError(error: any) {
    console.log(error);
    const router = this.injector.get(Router);
    switch (error.status) {
      case 401 :
        this.token.resetToken();
        router.navigate(['login']);
        break;
      case 404 :
        this.pushNotification({
          title : 404,
          statusText : 'Запрашиваемый адрес не найден'
        });
        break;
      case 400 :
        this.pushNotification({
          title : 400,
          statusText : 'Неверно заполненые поля'
        });
        break;
      case 409 :
        this.pushNotification({
          title : 409,
          statusText : error.error.message
        });
        break;
      case 403 :
        this.pushNotification({
          title : 403,
          statusText : 'Действие запрещено ' + error.url
        });
        break;
      case 405 :
        this.pushNotification({
          title : 405,
          statusText : error.error.message + ' ' + error.error.path
        });
        break;
      case 500 :
        this.pushNotification({
          title : error.status,
          statusText : error.message
        });
        break;
      case 502  :
        this.pushNotification({
          title : 502,
          statusText : 'Время ожидания по запросу истекло, сервер недоступен'
        });
        break;
      case -1  :
        this.pushNotification({
          title : 408,
          statusText : 'Время ожидания по запросу истекло, сервер недоступен'
        });
        break;
    }
  }

  private pushNotification(error: any) {
    const title: string = error.title;
    const message: string = error.statusText ? error.statusText : 'Server error';
    this.notificationsService.push(
      new Notification({
        message: message,
        title: title,
        time: 10000,
        type: NOTIFICATION_TYPE.ERROR
      })
    );
  }
}
