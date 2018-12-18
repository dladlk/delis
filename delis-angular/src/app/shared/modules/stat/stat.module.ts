import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from "@ngx-translate/core";
import { StatComponent } from './stat.component';

@NgModule({
    imports: [CommonModule, RouterModule, TranslateModule],
    declarations: [StatComponent],
    exports: [StatComponent]
})
export class StatModule {}
