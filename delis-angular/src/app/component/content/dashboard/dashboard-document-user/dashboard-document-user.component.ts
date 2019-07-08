import { Component, Input, OnInit } from '@angular/core';
import { DashboardDocumentUserData } from "../../../../model/content/dashboard/dashboard-document-user.data";

@Component({
  selector: 'app-dashboard-document-user',
  templateUrl: './dashboard-document-user.component.html',
  styleUrls: ['./dashboard-document-user.component.scss']
})
export class DashboardDocumentUserComponent implements OnInit {

  @Input() data: DashboardDocumentUserData;
  @Input() statusError: boolean;

  constructor() { }

  ngOnInit() {
  }

}
