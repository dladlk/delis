import { SendDocumentModel } from './send-document.model';

export class SendDocumentsBytesModel {

  id: number;
  createTime: string;
  type: string;
  size: number;
  sendDocument: SendDocumentModel = new SendDocumentModel();
}
