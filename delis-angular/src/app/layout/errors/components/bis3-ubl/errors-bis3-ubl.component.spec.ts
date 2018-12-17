import { async, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ErrorsBis3UblComponent } from './errors-bis3-ubl.component';
import { ErrorsBis3UblModule } from './errors-bis3-ubl.module';

describe('ErrorsCiiModule', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ ErrorsBis3UblModule, RouterTestingModule ],
    })
      .compileComponents();
  }));

  it('should create', () => {
    const fixture = TestBed.createComponent(ErrorsBis3UblComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
