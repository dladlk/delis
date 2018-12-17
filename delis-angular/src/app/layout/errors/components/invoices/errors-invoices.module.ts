import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ErrorsInvoicesRoutingModule } from './errors-invoices-routing.module';
import { ErrorsInvoicesComponent } from './errors-invoices.component';
import { PageHeaderModule } from '../../../../shared/index';

@NgModule({
  imports: [CommonModule, ErrorsInvoicesRoutingModule, PageHeaderModule],
  declarations: [ErrorsInvoicesComponent]
})
export class ErrorsInvoicesModule {}
