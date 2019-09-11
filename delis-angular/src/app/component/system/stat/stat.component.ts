import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RedirectContentService } from '../../../service/content/redirect-content.service';

@Component({
  selector: 'app-stat',
  templateUrl: './stat.component.html',
  styleUrls: ['./stat.component.scss']
})
export class StatComponent implements OnInit {

  @Input() bgClass: string;
  @Input() icon: string;
  @Input() count: number;
  @Input() label: string;
  @Input() statusError: boolean;
  @Input() path: string;
  @Input() dateStart: string;
  @Input() dateEnd: string;

  constructor(private router: Router, private redirectService: RedirectContentService) { }

  ngOnInit() {}

  redirectDelisContent() {
    let data = {
      dateStart: this.dateStart,
      dateEnd: this.dateEnd,
      statusError: this.statusError,
      path: this.path
    };
    this.redirectService.updateRedirectData(data);
    this.router.navigate(['/' + this.path]);
  }
}
