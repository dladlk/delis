import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { IdentifierModel } from '../../../../model/content/identifier/identifier.model';
import { ErrorService } from '../../../../service/system/error.service';
import { IdentifierService } from '../../../../service/content/identifier.service';
import { ErrorModel } from '../../../../model/system/error.model';
import { JournalIdentifierService } from '../../../../service/content/journal-identifier.service';
import { JournalIdentifierModel } from '../../../../model/content/identifier/journal-identifier.model';

import {SHOW_DATE_FORMAT, IDENTIFIER_PATH} from '../../../../app.constants';
import {IdentifierStateService} from "../../../../service/state/identifier-state.service";

@Component({
  selector: 'app-identifier-details',
  templateUrl: './identifier-details.component.html',
  styleUrls: ['./identifier-details.component.scss']
})
export class IdentifierDetailsComponent implements OnInit {

  identifierId: number;
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

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private location: Location,
    private errorService: ErrorService,
    private identifierService: IdentifierService,
    public stateService: IdentifierStateService,
    private journalIdentifierService: JournalIdentifierService) { }

  ngOnInit() {
    const id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
    this.identifierId = id;
    this.identifierService.getOneIdentifierById(id).subscribe((data: IdentifierModel) => {
      this.identifier = data;
      this.errorOneIdentifier = false;
    }, error => {
      this.errorOneIdentifierModel = this.errorService.errorProcess(error);
      this.errorOneIdentifier = true;
    });
    this.id = id;
    this.currentProdJournalIdentifier();
    this.initStateDetails(id);
  }

  private initStateDetails(id: number) {
    if (this.stateService.getFilter() !== undefined) {
      let stateDetails = this.stateService.getFilter().detailsState;
      this.currentIds = stateDetails.currentIds;
      if (this.currentIds.length !== 0) {
        this.isNextUp = id !== this.currentIds[0];
        this.isNextDown = id !== this.currentIds[this.currentIds.length - 1];
      }
    } else {
      this.router.navigate(['/' + IDENTIFIER_PATH], { queryParams: { skip: false } });
    }
  }

  private currentProdJournalIdentifier() {
    this.journalIdentifierService.getAllByIdentifierId(this.id).subscribe(
      (data: {}) => {
        this.journalIdentifiers = data['items'];
        this.errorOneJournalIdentifiers = false;
      }, error => {
        this.errorOneJournalIdentifiersModel = this.errorService.errorProcess(error);
        this.errorOneJournalIdentifiers = true;
      }
    );
  }
}
