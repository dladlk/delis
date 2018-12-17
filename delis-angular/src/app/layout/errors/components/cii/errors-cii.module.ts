import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ErrorsCiiService } from '../../services/errors-cii.service';
import { ErrorsCiiRoutingModule } from './errors-cii-routing.module';
import { ErrorsCiiComponent } from './errors-cii.component';
import { ErrorsModule, PageHeaderModule } from '../../../../shared/modules';

@NgModule({
  imports: [CommonModule, ErrorsCiiRoutingModule, PageHeaderModule, ErrorsModule],
  declarations: [ErrorsCiiComponent],
  providers: [ErrorsCiiService]
})
export class ErrorsCiiModule {}
