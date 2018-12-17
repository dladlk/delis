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
    public router: Router
  ) {
    this.translate.addLangs(['en', 'fr', 'ur', 'es', 'it', 'fa', 'de', 'zh-CHS']);
    this.translate.setDefaultLang('en');
    const browserLang = this.translate.getBrowserLang();
    this.translate.use(browserLang.match(/en|fr|ur|es|it|fa|de|zh-CHS/) ? browserLang : 'en');
  }

  ngOnInit() {
  }

  onLoggedin(email: string, password: string) {
    console.log('email ' + email);
    console.log('password ' + password);
    this.auth.login(email, password);
    localStorage.setItem('isLoggedin', 'true');
  }
}
