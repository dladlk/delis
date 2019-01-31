import { DocumentsComponent } from "./documents.component";
import { PaginationModel } from "../../../bs-component/components/pagination/pagination.model";

export class DocumentsErrorComponent extends DocumentsComponent{

    errorsFlag = true;

    initSelected() {
        super.initSelected();
        let select = JSON.parse(localStorage.getItem("Document"));
        this.statuses = select.documentStatus.filter(err => {
            if (err.includes('ERROR')) {
                return err;
            }
        });
    }

    protected initProcess() {
        this.pagination = new PaginationModel();
        super.initDefaultValues();
        super.clearAllFilter();
        this.currentProdDocuments(1, 10, this.errorsFlag);
    }

    protected loadPage(page: number, pageSize: number) {
        this.currentProdDocuments(page, pageSize, this.errorsFlag);
    }
}
