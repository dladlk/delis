import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ErrorsInvoicesComponent } from './errors-invoices.component';

const routes: Routes = [
  {
    path: '', component: ErrorsInvoicesComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ErrorsInvoicesRoutingModule {
}
