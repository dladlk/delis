import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { TranslateModule } from "@ngx-translate/core";
import { NgSelectModule } from "@ng-select/ng-select";
import { BsDatepickerModule } from "ngx-bootstrap";
import { HttpClientModule } from "@angular/common/http";

import { PageHeaderModule } from "../../../../../shared/modules";
import { BsComponentModule } from "../../../../bs-component/bs-component.module";
import { JournalDocumentComponent} from "../components/journal.document.component";
import { JournalOneDocumentComponent } from "../components/one/journal.one.document.component";
import { JournalDocumentService } from "../services/journal.document.service";
import { JournalDocumentRoutingModule } from "../journal.document-routing.module";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        NgbModule,
        TranslateModule,
        NgSelectModule,
        JournalDocumentRoutingModule,
        PageHeaderModule,
        BsDatepickerModule,
        HttpClientModule,
        BsComponentModule],
    declarations: [JournalDocumentComponent, JournalOneDocumentComponent],
    providers: [JournalDocumentService]
})
export class JournalDocumentModule {}