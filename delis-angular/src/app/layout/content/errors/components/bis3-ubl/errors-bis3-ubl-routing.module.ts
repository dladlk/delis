import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ErrorsBis3UblComponent } from './errors-bis3-ubl.component';

const routes: Routes = [
  {
    path: '', component: ErrorsBis3UblComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ErrorsBis3UblRoutingModule {
}
