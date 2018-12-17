import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { StatComponent } from './stat.component';

@NgModule({
    imports: [CommonModule, RouterModule],
    declarations: [StatComponent],
    exports: [StatComponent]
})
export class StatModule {}
