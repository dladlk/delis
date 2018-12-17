import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ErrorsCiiComponent } from './errors-cii.component';

const routes: Routes = [
  {
    path: '', component: ErrorsCiiComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ErrorsCiiRoutingModule {
}
