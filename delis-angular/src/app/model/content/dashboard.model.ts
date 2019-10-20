export class DashboardModel {

  errorLastHour: number;
  receivedDocumentsLastHour: number;
  sendDocumentsLastHour: number;
  deliveryAlertCount: number;

  constructor() {
    this.errorLastHour = 0;
    this.receivedDocumentsLastHour = 0;
    this.sendDocumentsLastHour = 0;
    this.deliveryAlertCount = 10;
  }
}
