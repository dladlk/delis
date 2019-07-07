import { Component, Input, OnInit } from '@angular/core';
import { DashboardDocumentAdminData } from "../../../../model/content/dashboard/dashboard-document-admin.data";

@Component({
  selector: 'app-dashboard-document-admin',
  templateUrl: './dashboard-document-admin.component.html',
  styleUrls: ['./dashboard-document-admin.component.scss']
})
export class DashboardDocumentAdminComponent implements OnInit {

  @Input() data: DashboardDocumentAdminData;

  constructor() { }

  ngOnInit() { }

}
