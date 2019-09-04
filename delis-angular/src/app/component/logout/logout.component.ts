import { Component, OnInit} from '@angular/core';

import { TokenService } from '../../service/system/token.service';
import { HttpRestService } from '../../service/system/http-rest.service';
import { RuntimeConfigService } from '../../service/system/runtime-config.service';
import { LogoutService } from '../../service/system/logout.service';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.scss']
})
export class LogoutComponent implements OnInit {

  constructor(private tokenService: TokenService,
              private http: HttpRestService,
              private configService: RuntimeConfigService,
              private logoutService: LogoutService) { }

  ngOnInit() {
    this.http.methodDelete(this.configService.getConfigUrl() + '/rest/logout', this.tokenService.getToken()).subscribe(
      (data: {}) => {
        this.logoutService.logout();
      }
    );
  }
}
