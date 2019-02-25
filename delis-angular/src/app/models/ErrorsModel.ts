export interface IErrorsModel {

  statusMessage: string;
}

export class ErrorsModel implements IErrorsModel {

  statusNumber: number;
  statusMessage: string;

  constructor(model ?: any) {
    if (model) {
      this.statusNumber = model.statusNumber;
      this.statusMessage = model.statusMessage;
    }
  }
}

export class ErrorsInvoicesModel implements IErrorsModel {

  statusMessage: string;
  sender: string;
  receiver: string;
  invoiceDate: Date;
  sendDate: Date;

  constructor(model ?: any) {
    if (model) {
      this.statusMessage = model.statusMessage;
      this.sender = model.sender;
      this.receiver = model.receiver;
      this.invoiceDate = new Date(model.invoiceDate);
      this.sendDate = new Date(model.sendDate);
    }
  }
}
