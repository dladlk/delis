import { AfterViewInit, Directive, ElementRef, Input } from '@angular/core';

@Directive({
  selector: '[appAutofocus]'
})
export class AutofocusDirective implements AfterViewInit {

  @Input() public appAutoFocus: boolean;

  public constructor(private el: ElementRef) { }

  public ngAfterViewInit() {
    setTimeout(() => {
      this.el.nativeElement.focus();
    }, 0);
  }
}
