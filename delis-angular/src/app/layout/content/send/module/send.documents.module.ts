import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {TranslateModule} from "@ngx-translate/core";
import {NgSelectModule} from "@ng-select/ng-select";
import {PageHeaderModule} from "../../../../shared/modules";
import {HttpClientModule} from "@angular/common/http";
import {BsComponentModule} from "../../../bs-component/bs-component.module";
import {SendDocumentsComponent} from "../components/send.documents.component";
import {SendDocumentsService} from "../service/send.documents.service";
import {SendDocumentsRouting} from "../send.documents.routing";
import {SendDocumentsOneComponent} from "../components/one/send.documents.one.component";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        NgbModule,
        TranslateModule,
        NgSelectModule,
        SendDocumentsRouting,
        PageHeaderModule,
        HttpClientModule,
        BsComponentModule],
    declarations: [SendDocumentsComponent, SendDocumentsOneComponent],
    providers: [SendDocumentsService]
})
export class SendDocumentsModule {

}
