import { Injectable } from '@angular/core';
import { HttpRestService } from './http-rest.service';

@Injectable({
  providedIn: 'root'
})
export class VersionCheckService {

  private currentHash = '{{POST_BUILD_ENTERS_HASH_HERE}}';

  constructor(private http: HttpRestService) {}

  /**
   *  interval default 30 minutes
   */
  public initVersionCheck(frequency = 1800000) {
    setInterval(() => {
      this.checkVersion();
    }, frequency);
  }

  private checkVersion() {
    this.http.methodInnerGet('assets/config/version.json?t=' + new Date().getTime()).subscribe(
      (data: any) => {
        const hash = data.hash;
        const hashChanged = this.hasHashChanged(this.currentHash, hash);
        if (hashChanged) {
          this.currentHash = hash;
          location.reload();
        }
      },
      (err) => {
        console.error(err, 'Could not get version');
      }
    );
  }

  private hasHashChanged(currentHash, newHash) {
    if (!currentHash || currentHash === '{{POST_BUILD_ENTERS_HASH_HERE}}') {
      return false;
    }
    return currentHash !== newHash;
  }
}
