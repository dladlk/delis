import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-stat',
  templateUrl: './stat.component.html',
  styleUrls: ['./stat.component.scss']
})
export class StatComponent implements OnInit {

  @Input() bgClass: string;
  @Input() icon: string;
  @Input() count: string;
  @Input() label: string;
  @Input() data: number;
  @Input() rotate: boolean;
  @Input() router: RouterLink;
  @Output() event: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

}
