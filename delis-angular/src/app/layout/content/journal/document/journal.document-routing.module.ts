import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { JournalDocumentComponent} from "./components/journal.document.component";
import { JournalOneDocumentComponent } from "./components/one/journal.one.document.component";

const routes: Routes = [
    {
        path: '', component: JournalDocumentComponent
    },
    {
        path: ':id', component: JournalOneDocumentComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class JournalDocumentRoutingModule {

}