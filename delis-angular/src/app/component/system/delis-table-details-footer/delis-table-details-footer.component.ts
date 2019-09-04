import {Component, Input, OnInit} from '@angular/core';
import {StateService} from '../../../service/state/state-service';
import {TableStateModel} from '../../../model/filter/table-state.model';
import {Router} from '@angular/router';

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

  constructor(private router: Router) { }

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
    this.router.navigate(['/' + this.path], { queryParams: { skip: false } });
  }

  nextUp() {
    const upId = this.currentIds[this.currentIds.indexOf(this.id) - 1];
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
      this.router.navigate(['/' + this.path, upId]));
  }

  nextDown() {
    const downId = this.currentIds[this.currentIds.indexOf(this.id) + 1];
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
      this.router.navigate(['/' + this.path, downId]));
  }

  refreshData() {
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
      this.router.navigate(['/' + this.path, this.id]));
  }

  gotoTop() {
    window.scroll({
      top: 0,
      left: 0,
      behavior: 'smooth'
    });
  }
}
