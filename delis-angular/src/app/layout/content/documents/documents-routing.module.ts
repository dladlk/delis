import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DocumentsComponent } from './components/documents.component';
import { DocumentsOneComponent } from "./components/one/documents.one.component";
import { DocumentsErrorComponent } from "./components/documents.error.component";

const routes: Routes = [
  {
    path: '', component: DocumentsComponent
  },
  {
    path: 'errors', component: DocumentsErrorComponent
  },
  {
    path: 'details/:id', component: DocumentsOneComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DocumentsRoutingModule {
}
