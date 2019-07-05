import { Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';

import { TokenService } from '../../service/system/token.service';
import { HttpRestService } from '../../service/system/http-rest.service';
import { RuntimeConfigService } from '../../service/system/runtime-config.service';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.scss']
})
export class LogoutComponent implements OnInit {

  constructor(private tokenService: TokenService,
              private http: HttpRestService,
              private router: Router, private configService: RuntimeConfigService) { }

  ngOnInit() {
    this.http.methodDelete(this.configService.getConfigUrl() + '/rest/logout', this.tokenService.getToken()).subscribe(
      (data: {}) => {
        console.log('logout : ' + data['data']);
      }
    );
    this.tokenService.resetToken();
    this.configService.resetCurrentUser();
    this.router.navigate(['/login']);
  }
}
