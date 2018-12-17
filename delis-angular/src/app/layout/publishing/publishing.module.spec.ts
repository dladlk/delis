import { PublishingModule } from './publishing.module';

describe('PublishingModule', () => {
  let publishingModule: PublishingModule;

  beforeEach(() => {
    publishingModule = new PublishingModule();
  });

  it('should create an instance', () => {
    expect(publishingModule).toBeTruthy();
  });
});
