import { async, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { DocumentsComponent } from './documents.component';
import { DocumentsModule } from '../modules/documents.module';

describe('DocumentsComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ DocumentsModule, RouterTestingModule ],
    })
      .compileComponents();
  }));

  it('should create', () => {
    const fixture = TestBed.createComponent(DocumentsComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
