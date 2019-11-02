import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {

  @Output() sidenavClose = new EventEmitter();
  public showMenu: string;
  constructor() {}

  ngOnInit() {
    this.showMenu = '';
  }

  public onSidenavClose = () => {
    this.sidenavClose.emit();
  };

  addExpandClass(element: any) {
    if (element === this.showMenu) {
      this.showMenu = '0';
    } else {
      this.showMenu = element;
    }
  }
}
