import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { routerTransition } from '../router.animations';
import { AuthorizationService } from './authorization.service';
import {LocaleService} from "../service/locale.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  animations: [routerTransition()]
})
export class LoginComponent implements OnInit {
  constructor(
    private auth: AuthorizationService,
    private translate: TranslateService,
    private locale: LocaleService,
    public router: Router) {
    // this.translate.addLangs(['en', 'da']);
    this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
  }

  ngOnInit() {
  }

  onLoggedin(email: string, password: string) {
    this.auth.login(email, password);
    localStorage.setItem('isLoggedin', 'true');
  }
}
