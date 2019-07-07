import { Component, Input, OnInit } from '@angular/core';
import { DashboardSendDocumentData } from "../../../../model/content/dashboard/dashboard-send-document.data";

@Component({
  selector: 'app-dashboard-send-document',
  templateUrl: './dashboard-send-document.component.html',
  styleUrls: ['./dashboard-send-document.component.scss']
})
export class DashboardSendDocumentComponent implements OnInit {

  @Input() data: DashboardSendDocumentData;

  constructor() { }

  ngOnInit() { }

}
