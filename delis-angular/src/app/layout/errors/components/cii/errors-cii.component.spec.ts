import { async, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ErrorsCiiComponent } from './errors-cii.component';
import { ErrorsCiiModule } from './errors-cii.module';

describe('ErrorsCiiModule', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ ErrorsCiiModule, RouterTestingModule ],
    })
      .compileComponents();
  }));

  it('should create', () => {
    const fixture = TestBed.createComponent(ErrorsCiiComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
