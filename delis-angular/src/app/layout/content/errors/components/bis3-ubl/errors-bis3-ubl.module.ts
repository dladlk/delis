import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ErrorsBis3UblRoutingModule } from './errors-bis3-ubl-routing.module';
import { ErrorsBis3UblComponent } from './errors-bis3-ubl.component';
import { ErrorsBis3UblService } from '../../services/errors-bis3-ubl.service';
import { ErrorsModule, PageHeaderModule } from '../../../../../shared/modules';

@NgModule({
  imports: [CommonModule, ErrorsBis3UblRoutingModule, PageHeaderModule, ErrorsModule],
  declarations: [ErrorsBis3UblComponent],
  providers: [ErrorsBis3UblService]
})
export class ErrorsBis3UblModule {}
