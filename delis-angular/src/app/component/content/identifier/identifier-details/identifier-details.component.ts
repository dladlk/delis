import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from "rxjs";

import { ErrorService } from '../../../../service/system/error.service';
import { IdentifierService } from '../../../../service/content/identifier.service';
import { JournalIdentifierService } from '../../../../service/content/journal-identifier.service';
import { IdentifierStateService } from '../../../../service/state/identifier-state.service';
import { RoutingStateService } from "../../../../service/system/routing-state.service";
import { DelisEntityDetailsObservable } from "../../../../observable/delis-entity-details.observable";
import { ErrorModel } from '../../../../model/system/error.model';
import { IdentifierModel } from '../../../../model/content/identifier/identifier.model';
import { JournalIdentifierModel } from '../../../../model/content/identifier/journal-identifier.model';

import { DASHBOARD_PATH, IDENTIFIER_PATH, SHOW_DATE_FORMAT } from '../../../../app.constants';

@Component({
  selector: 'app-identifier-details',
  templateUrl: './identifier-details.component.html',
  styleUrls: ['./identifier-details.component.scss']
})
export class IdentifierDetailsComponent implements OnInit, OnDestroy {

  id: number;
  identifier: IdentifierModel = new IdentifierModel();
  journalIdentifiers: JournalIdentifierModel[];

  errorOneIdentifier = false;
  errorOneIdentifierModel: ErrorModel;

  errorOneJournalIdentifiers = false;
  errorOneJournalIdentifiersModel: ErrorModel;

  SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;
  IDENTIFIER_PATH = IDENTIFIER_PATH;
  isNextUp: boolean;
  isNextDown: boolean;
  currentIds: number[];

  isShowFooter: boolean;
  topPosToStartShowing = 100;

  private pageUpdate$: Subscription;

  @HostListener('window:scroll')
  checkScroll() {
    const scrollPosition = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
    this.isShowFooter = scrollPosition >= this.topPosToStartShowing;
  }

  constructor(
      private router: Router,
      private route: ActivatedRoute,
      private location: Location,
      private errorService: ErrorService,
      private identifierService: IdentifierService,
      public stateService: IdentifierStateService,
      private journalIdentifierService: JournalIdentifierService,
      private routingState: RoutingStateService,
      private delisEntityDetailsObservable: DelisEntityDetailsObservable) {
    this.pageUpdate$ = this.delisEntityDetailsObservable.listen().subscribe((id: any) => {
      this.id = id;
      this.stateService.filter.detailsState.currentId = this.id;
      this.initPage(id);
      this.initStateDetails(id);
    });
  }

  ngOnInit() {
    if (this.stateService.getFilter() !== undefined) {
      this.id = this.stateService.getFilter().detailsState.currentId;
      this.initPage(this.id);
      this.initStateDetails(this.id);
    } else {
      this.router.navigate(['/' + DASHBOARD_PATH]);
    }
    this.initStateDetails(this.id);
  }

  ngOnDestroy() {
    if (this.pageUpdate$) {
      this.pageUpdate$.unsubscribe();
    }
  }

  private initPage(id: any) {
    this.identifierService.getOneIdentifierById(id).subscribe((data: IdentifierModel) => {
      this.identifier = data;
      this.errorOneIdentifier = false;
    }, error => {
      this.errorOneIdentifierModel = this.errorService.errorProcess(error);
      this.errorOneIdentifier = true;
    });
    this.currentProdJournalIdentifier();
  }

  private initStateDetails(id: number) {
    if (this.stateService.getFilter() !== undefined) {
      const stateDetails = this.stateService.getFilter().detailsState;
      this.currentIds = stateDetails.currentIds;
      if (this.currentIds.length !== 0) {
        this.isNextUp = id !== this.currentIds[0];
        this.isNextDown = id !== this.currentIds[this.currentIds.length - 1];
      }
    } else {
      this.router.navigate(['/' + DASHBOARD_PATH]);
    }
  }

  private currentProdJournalIdentifier() {
    this.journalIdentifierService.getAllByIdentifierId(this.id).subscribe((data: any) => {
          this.journalIdentifiers = data.items;
          this.errorOneJournalIdentifiers = false;
        }, error => {
          this.errorOneJournalIdentifiersModel = this.errorService.errorProcess(error);
          this.errorOneJournalIdentifiers = true;
        }
    );
  }
}
