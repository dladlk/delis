import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from "@ngx-translate/core";

import { PublishingRoutingModule } from './publishing-routing.module';
import { PublishingComponent } from './publishing.component';
import { PageHeaderModule } from './../../shared';

@NgModule({
  imports: [CommonModule, TranslateModule, PublishingRoutingModule, PageHeaderModule],
  declarations: [PublishingComponent]
})
export class PublishingModule {}
