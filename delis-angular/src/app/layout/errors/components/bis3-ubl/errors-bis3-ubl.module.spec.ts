import { ErrorsBis3UblModule } from './errors-bis3-ubl.module';

describe('ErrorsCiiModule', () => {
  let errorsBis3UblModule: ErrorsBis3UblModule;

  beforeEach(() => {
    errorsBis3UblModule = new ErrorsBis3UblModule();
  });

  it('should create an instance', () => {
    expect(errorsBis3UblModule).toBeTruthy();
  });
});
