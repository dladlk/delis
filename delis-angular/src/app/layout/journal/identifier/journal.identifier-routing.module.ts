import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { JournalIdentifierComponent } from "./components/journal.identifier.component";
import { JournalOneIdentifierComponent } from "./components/one/journal.one.identifier.component";

const routes: Routes = [
    {
        path: '', component: JournalIdentifierComponent
    },
    {
        path: ':id', component: JournalOneIdentifierComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class JournalIdentifierRoutingModule {

}