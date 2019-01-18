import { Injectable } from '@angular/core';
import { ErrorsModel } from '../models/ErrorsModel';

@Injectable()
export class ErrorsCiiService {

  errors: Array<ErrorsModel> = [];

  constructor() {
    this.errors.push(
      {
        statusNumber: 21, statusMessage: 'Received CII with validation OK'
      },
      {
        statusNumber: 22, statusMessage: 'Received CII with validation Error'
      },
      {
        statusNumber: 23, statusMessage: 'Received CII converted BIS3 UBL'
      },
      {
        statusNumber: 24, statusMessage: 'Converted BIS3 UBL validation OK'
      },
      {
        statusNumber: 25, statusMessage: 'Converted BIS3 UBL validation Error'
      },
      {
        statusNumber: 26, statusMessage: 'BIS3 UBL to OIOUBL validation OK'
      },
      {
        statusNumber: 27, statusMessage: 'BIS3 UBL to OIOUBL validation Error'
      }
    );
  }

  getErrors() {
    return this.errors;
  }
}
