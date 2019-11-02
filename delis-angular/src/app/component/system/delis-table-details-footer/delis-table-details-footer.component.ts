import { Component, Input, OnInit } from '@angular/core';
import { Router} from '@angular/router';
import { StateService } from '../../../service/state/state-service';
import { TableStateModel } from '../../../model/filter/table-state.model';
import { DelisEntityDetailsObservable } from "../../../observable/delis-entity-details.observable";
import { RoutingStateService } from "../../../service/system/routing-state.service";

@Component({
  selector: 'app-delis-table-details-footer',
  templateUrl: './delis-table-details-footer.component.html',
  styleUrls: ['./delis-table-details-footer.component.scss']
})
export class DelisTableDetailsFooterComponent implements OnInit {

  @Input() isNextUp: boolean;
  @Input() isNextDown: boolean;
  @Input() currentIds: number[];
  @Input() id: number;
  @Input() path: string;
  @Input() stateService: StateService<TableStateModel>;

  constructor(private router: Router, private routingState: RoutingStateService, private delisEntityDetailsObservable: DelisEntityDetailsObservable) { }

  ngOnInit() {
    if (this.stateService !== undefined) {
      const filter = this.stateService.getFilter();
      if (filter !== undefined) {
        const detailsState = filter.detailsState;
        detailsState.currentId = this.id;
        filter.detailsState = detailsState;
        this.stateService.setFilter(filter);
      }
    }
  }

  back() {
    this.stateService.filter.detailsState.skip = false;
    this.router.navigateByUrl(this.routingState.getPreviousUrl());
  }

  nextUp() {
    const upId = this.currentIds[this.currentIds.indexOf(this.id) - 1];
    this.delisEntityDetailsObservable.loadCurrentId(upId);
  }

  nextDown() {
    const downId = this.currentIds[this.currentIds.indexOf(this.id) + 1];
    this.delisEntityDetailsObservable.loadCurrentId(downId);
  }

  refreshData() {
    this.delisEntityDetailsObservable.loadCurrentId(this.id);
  }

  gotoTop() {
    window.scroll({
      top: 0,
      left: 0,
      behavior: 'smooth'
    });
  }
}
