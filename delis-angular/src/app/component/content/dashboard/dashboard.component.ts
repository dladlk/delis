import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';

import { DashboardModel } from '../../../model/content/dashboard.model';
import { DashboardObservable } from '../../../observable/dashboard.observable';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {

  dashboardModel: DashboardModel = new DashboardModel();
  dateStart: string = null;
  dateEnd: string = null;
  private dashboardSubscription$: Subscription;

  constructor(private dashboardObservable: DashboardObservable) { }

  ngOnInit() {
    this.dashboardSubscription$ = this.dashboardObservable.listen().subscribe((data: any) => {
      if (data) {
        this.dashboardModel = data.dashboardModel;
        this.dateStart = data.dateStart;
        this.dateEnd = data.dateEnd;
      }
    });
  }

  ngOnDestroy() {
    if (this.dashboardSubscription$) {
      this.dashboardSubscription$.unsubscribe();
    }
  }
}
