import { Injectable } from '@angular/core';
import { ErrorsModel } from '../models/ErrorsModel';

@Injectable()
export class ErrorsBis3UblService {

  errors: Array<ErrorsModel> = [];

  constructor() {
    this.errors.push(
      {
        statusNumber: 11, statusMessage: 'Received BIS3 UBL with validation OK'
      },
      {
        statusNumber: 12, statusMessage: 'Received BIS3 UBL with validation Error'
      },
      {
        statusNumber: 13, statusMessage: 'BIS3 UBL to OIOUBL validation OK'
      },
      {
        statusNumber: 14, statusMessage: 'BIS3 UBL to OIOUBL validation Error'
      }
    );
  }

  getErrors() {
    return this.errors;
  }
}
