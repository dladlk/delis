import {Injectable} from '@angular/core';
import * as fileSaver from 'file-saver';

@Injectable()
export class FileSaverService {

    public static saveFile(data: any, filename?: string) {
        const blob = new Blob([data], {type: 'application/octet-stream'});
        fileSaver.saveAs(blob, filename);
    }
}
