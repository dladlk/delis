import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { routerTransition } from '../router.animations';
import { AuthorizationService } from './authorization.service';

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
    public router: Router) {
    this.translate.addLangs(['en', 'da']);
    const browserLang = this.translate.getBrowserLang();
    this.translate.use(browserLang.match(/en|da/) ? browserLang : 'en');
  }

  ngOnInit() {
  }

  onLoggedin(email: string, password: string) {
    this.auth.login(email, password);
    localStorage.setItem('isLoggedin', 'true');
  }
}
