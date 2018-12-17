import { async, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PublishingComponent } from './publishing.component';
import { PublishingModule } from './publishing.module';

describe('PublishingComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ PublishingModule, RouterTestingModule ],
    })
      .compileComponents();
  }));

  it('should create', () => {
    const fixture = TestBed.createComponent(PublishingComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
