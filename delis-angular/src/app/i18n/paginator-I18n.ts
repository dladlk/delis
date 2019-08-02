import { MatPaginatorIntl } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';

export class PaginatorI18n extends MatPaginatorIntl {

    translate: TranslateService;

    itemsPerPageLabel = 'Items per page';
    nextPageLabel     = 'Next page';
    previousPageLabel = 'Previous page';

    getRangeLabel = function(page: number, pageSize: number, length: number): string {
        const of = this.translate ? this.translate.instant('pagination.status.of') : 'of';
        if (length === 0 || pageSize === 0) {
            return '0 ' + of + ' ' + length;
        }
        length = Math.max(length, 0);
        const startIndex = page * pageSize;
        const endIndex = startIndex < length ?
            Math.min(startIndex + pageSize, length) :
            startIndex + pageSize;
        return startIndex + 1 + ' - ' + endIndex + ' ' + of + ' ' + length;
    };

    injectTranslateService(translate: TranslateService) {
        this.translate = translate;
        this.translate.onLangChange.subscribe(() => {
            this.translateLabels();
        });
        this.translateLabels();
    }

    translateLabels() {
        this.itemsPerPageLabel = this.translate.instant('paginator.items_per_page');
    }
}
