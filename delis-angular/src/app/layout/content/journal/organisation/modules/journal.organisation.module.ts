import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { TranslateModule } from "@ngx-translate/core";
import { NgSelectModule } from "@ng-select/ng-select";
import { PageHeaderModule } from "../../../../../shared/modules";
import { HttpClientModule } from "@angular/common/http";
import { BsComponentModule } from "../../../../bs-component/bs-component.module";
import { JournalOrganisationComponent } from "../components/journal.organisation.component";
import { JournalOneOrganisationComponent } from "../components/one/journal.one.organisation.component";
import { JournalOrganisationService } from "../services/journal.organisation.service";
import { JournalOrganisationRoutingModule } from "../journal.organisation-routing.module";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        NgbModule,
        TranslateModule,
        NgSelectModule,
        JournalOrganisationRoutingModule,
        PageHeaderModule,
        HttpClientModule,
        BsComponentModule],
    declarations: [JournalOrganisationComponent, JournalOneOrganisationComponent],
    providers: [JournalOrganisationService]
})
export class JournalOrganisationModule {

}
