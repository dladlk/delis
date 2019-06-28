import { JournalDocumentModel } from './journal-document.model';
import { ErrorDictionaryModel } from './error-dictionary.model';

export class JournalDocumentErrorModel {

  id: number;
  createTime: string;
  journalDocument: JournalDocumentModel = new JournalDocumentModel();
  errorDictionary: ErrorDictionaryModel = new ErrorDictionaryModel();
}
