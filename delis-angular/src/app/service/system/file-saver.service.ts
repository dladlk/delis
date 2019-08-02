import { Injectable } from '@angular/core';
import * as fileSaver from 'file-saver';

@Injectable({
  providedIn: 'root'
})
export class FileSaverService {

  constructor() { }

  public static saveFile(data: any, filename?: string) {
    const blob = new Blob([data], {type: 'application/octet-stream'});
    fileSaver.saveAs(blob, filename);
  }

  public static getFileNameFromResponseContentDisposition = (res: Response) => {
    const contentDisposition = res.headers.get('content-disposition') || '';
    const matches = /filename=([^;]+)/ig.exec(contentDisposition);
    return (matches[1] || 'untitled').trim();
  }
}
