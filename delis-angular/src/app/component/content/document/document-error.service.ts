import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class DocumentErrorService {

    private statusErrorList = ['Validation error', 'Loading error', 'Unknown receiver', 'Indlæsningsfejl', 'Ukendt modtager',
        'Valideringsfejl'];

    get statusErrors(): string[] {
        return this.statusErrorList;
    }
}
