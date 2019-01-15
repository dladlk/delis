import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { JournalOrganisationComponent } from "./components/journal.organisation.component";
import { JournalOneOrganisationComponent } from "./components/one/journal.one.organisation.component";

const routes: Routes = [
    {
        path: '', component: JournalOrganisationComponent
    },
    {
        path: ':id', component: JournalOneOrganisationComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class JournalOrganisationRoutingModule {

}