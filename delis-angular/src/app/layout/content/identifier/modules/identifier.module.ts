import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { TranslateModule } from "@ngx-translate/core";
import { NgSelectModule } from "@ng-select/ng-select";
import { PageHeaderModule } from "../../../../shared/modules";
import { HttpClientModule } from "@angular/common/http";
import { BsComponentModule } from "../../../bs-component/bs-component.module";
import { IdentifierOneComponent } from "../components/one/identifier.one.component";
import { IdentifierService } from "../services/identifier.service";
import { IdentifierComponent } from "../components/identifier.component";
import { IdentifierRoutingModule } from "../identifier-routing.module";
import { JournalIdentifierService } from "../../journal/identifier/services/journal.identifier.service";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        NgbModule,
        TranslateModule,
        NgSelectModule,
        IdentifierRoutingModule,
        PageHeaderModule,
        HttpClientModule,
        BsComponentModule],
    declarations: [IdentifierComponent, IdentifierOneComponent],
    providers: [IdentifierService, JournalIdentifierService]
 })
export class IdentifierModule { }
