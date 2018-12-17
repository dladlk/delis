import { Injectable } from '@angular/core';
import { Notification } from '../models/notification.model';

let instance = null;

@Injectable()
export class NotificationService {

  notificationList: Notification[] = [];

  constructor() {
    if (instance) {
      return instance;
    }
    instance = this;
  }

  push(notification: Notification) {
    this.notificationList.push(notification);
  }

  removeByIndex(index: number) {
    this.notificationList.splice(index, 1);
  }
}
