import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DocumentsComponent } from './components/documents.component';
import { DocumentsOneComponent } from "./components/one/documents.one.component";

const routes: Routes = [
  {
    path: '', component: DocumentsComponent
  },
  {
    path: ':id', component: DocumentsOneComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DocumentsRoutingModule {
}
