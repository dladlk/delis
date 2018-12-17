import { Component, OnInit } from '@angular/core';
import { routerTransition } from '../../router.animations';

@Component({
  selector: 'app-publishing',
  templateUrl: './publishing.component.html',
  styleUrls: ['./publishing.component.scss'],
  animations: [routerTransition()]
})
export class PublishingComponent implements OnInit {
  constructor() {}

  ngOnInit() {}
}
