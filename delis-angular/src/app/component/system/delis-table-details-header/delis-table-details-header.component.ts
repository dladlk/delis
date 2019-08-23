import { Component, Input, OnInit } from '@angular/core';
import {ActivatedRoute, Params, Router} from "@angular/router";
import { StateService } from "../../../service/state/state-service";
import { TableStateModel } from "../../../model/filter/table-state.model";
import { RoutingStateService } from "../../../service/state/routing-state.service";

@Component({
  selector: 'app-delis-table-details-header',
  templateUrl: './delis-table-details-header.component.html',
  styleUrls: ['./delis-table-details-header.component.scss']
})
export class DelisTableDetailsHeaderComponent implements OnInit {

  @Input() isNextUp: boolean;
  @Input() isNextDown: boolean;
  @Input() currentIds: number[];
  @Input() id: number;
  @Input() path: string;
  @Input() stateService: StateService<TableStateModel>;

  previousRoute: string;

  constructor(private router: Router, private route: ActivatedRoute, private routingState: RoutingStateService) { }

  ngOnInit() {
    this.previousRoute = this.routingState.getPreviousUrl();
    if (this.stateService !== undefined) {
      let filter = this.stateService.getFilter();
      if (filter !== undefined) {
        let detailsState = filter.detailsState;
        detailsState.currentId = this.id;
        filter.detailsState = detailsState;
        this.stateService.setFilter(filter);
      }
    }
  }

  back() {
    // let url;
    // console.log(this.previousRoute);
    // if (this.previousRoute.indexOf('skip') <=0) {
    //   url = this.previousRoute + '?skip=false';
    // } else {
    //   url = this.previousRoute;
    // }
    // console.log(url);

    // const firstParam: string = this.route.snapshot.queryParamMap.getAll();

      const queryParams: Params = { skip: false };
    console.log(this.previousRoute);
    let params = this.previousRoute.substring(this.previousRoute.indexOf('?') + 1, this.previousRoute.length);
    console.log(params);
    let listParams = params.split('&');
    console.log(listParams);
    for (let param of listParams) {
      console.log(param);
      let currentParams = param.split('=');

    }

    this.router.navigate(
        ['/' + this.path],
        {
          relativeTo: this.route,
          queryParams: queryParams,
          queryParamsHandling: 'merge'
        });



    // this.router.navigateByUrl(url);
  }

  nextUp() {
    let upId = this.currentIds[this.currentIds.indexOf(this.id) - 1];
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
        this.router.navigate(['/' + this.path, upId]));
  }

  nextDown() {
    let downId = this.currentIds[this.currentIds.indexOf(this.id) + 1];
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
        this.router.navigate(['/' + this.path, downId]));
  }

  refreshData() {
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
        this.router.navigate(['/' + this.path, this.id]));
  }
}
