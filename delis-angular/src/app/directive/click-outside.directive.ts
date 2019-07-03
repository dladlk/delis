// import {Directive, ElementRef, EventEmitter, HostListener, Output} from '@angular/core';
//
// @Directive({
//   selector: '[appClickOutside]'
// })
// export class ClickOutsideDirective {
//
//   @Output('clickOutside') public clickOutside = new EventEmitter();
//
//   constructor(private el: ElementRef) { }
//
//   @HostListener('document:click', ['$event.target']) public onMouseEnter(targetElement) {
//     const clickedInside = this.el.nativeElement.contains(targetElement);
//     if (!clickedInside) {
//       this.clickOutside.emit(null);
//     }
//   }
// }
