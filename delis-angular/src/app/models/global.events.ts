import { EventEmitter } from '@angular/core';

let instance: GlobalEvents = null;

export class GlobalEvents {

  public onRequestEnd: EventEmitter<any> = new EventEmitter<any>();

  constructor() {

    if (!instance) {
      instance = this;
    }

    return instance;
  }
}
