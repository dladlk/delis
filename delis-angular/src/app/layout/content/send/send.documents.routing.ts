import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {SendDocumentsComponent} from "./components/send.documents.component";

const routes: Routes = [
    {
        path: '', component: SendDocumentsComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SendDocumentsRouting {}
