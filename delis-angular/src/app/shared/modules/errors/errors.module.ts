import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ErrorsComponent } from './errors.component';

@NgModule({
    imports: [CommonModule],
    declarations: [ErrorsComponent],
    exports: [ErrorsComponent]
})
export class ErrorsModule {}
