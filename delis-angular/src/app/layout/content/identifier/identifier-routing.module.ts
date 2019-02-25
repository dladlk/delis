import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { IdentifierOneComponent } from "./components/one/identifier.one.component";
import { IdentifierComponent } from "./components/identifier.component";

const routes: Routes = [
    {
        path: '', component: IdentifierComponent
    },
    {
        path: ':id', component: IdentifierOneComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class IdentifierRoutingModule { }
