import { async, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ErrorsInvoicesComponent } from './errors-invoices.component';
import { ErrorsInvoicesModule } from './errors-invoices.module';

describe('ErrorsCiiModule', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ ErrorsInvoicesModule, RouterTestingModule ],
    })
      .compileComponents();
  }));

  it('should create', () => {
    const fixture = TestBed.createComponent(ErrorsInvoicesComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
