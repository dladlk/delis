export class DashboardModel {

    publishedLastHour: number;
    errorLastHour: number;
    receivedDocumentsLastHour: number;
    averageDocumentsLastHour: number;
    journalDocument: number;
    journalIdentifier: number;
    journalOrganisation: number;

    constructor(model ?: any) {
        this.publishedLastHour = model.publishedLastHour;
        this.errorLastHour = model.errorLastHour;
        this.receivedDocumentsLastHour = model.receivedDocumentsLastHour;
        this.averageDocumentsLastHour = model.averageDocumentsLastHour;
        this.journalDocument = model.journalDocument;
        this.journalIdentifier = model.journalIdentifier;
        this.journalOrganisation = model.journalOrganisation;
    }
}