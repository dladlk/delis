import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {SendDocumentsComponent} from "./components/send.documents.component";
import {SendDocumentsOneComponent} from "./components/one/send.documents.one.component";

const routes: Routes = [
    {
        path: '', component: SendDocumentsComponent
    },
    {
        path: 'details/:id', component: SendDocumentsOneComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SendDocumentsRouting {}
