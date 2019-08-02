import { Component, Input, OnInit } from '@angular/core';
import { Router } from "@angular/router";
import { StateService } from "../../../service/state/state-service";
import { TableStateModel } from "../../../model/filter/table-state.model";

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

  constructor(private router: Router) { }

  ngOnInit() {
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
    this.router.navigate(['/' + this.path], { queryParams: { skip: false } });
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
