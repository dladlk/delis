import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { TranslateModule } from "@ngx-translate/core";
import { NgSelectModule } from "@ng-select/ng-select";
import { PageHeaderModule } from "../../../../../shared/modules";
import { BsDatepickerModule } from "ngx-bootstrap";
import { HttpClientModule } from "@angular/common/http";
import { BsComponentModule } from "../../../../bs-component/bs-component.module";
import { JournalIdentifierRoutingModule } from "../journal.identifier-routing.module";
import { JournalIdentifierComponent } from "../components/journal.identifier.component";
import { JournalOneIdentifierComponent } from "../components/one/journal.one.identifier.component";
import { JournalIdentifierService } from "../services/journal.identifier.service";
import { JournalIdentifierTestGuiStaticService } from "../services/journal.identifier.test-gui-static.service";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        NgbModule,
        TranslateModule,
        NgSelectModule,
        JournalIdentifierRoutingModule,
        PageHeaderModule,
        BsDatepickerModule,
        HttpClientModule,
        BsComponentModule],
    declarations: [JournalIdentifierComponent, JournalOneIdentifierComponent],
    providers: [JournalIdentifierService, JournalIdentifierTestGuiStaticService]
})
export class JournalIdentifierModule {

}