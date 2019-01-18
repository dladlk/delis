import { ErrorsCiiModule } from './errors-cii.module';

describe('ErrorsCiiModule', () => {
  let errorsCiiModule: ErrorsCiiModule;

  beforeEach(() => {
    errorsCiiModule = new ErrorsCiiModule();
  });

  it('should create an instance', () => {
    expect(errorsCiiModule).toBeTruthy();
  });
});
