import { ErrorsInvoicesModule } from './errors-invoices.module';

describe('ErrorsCiiModule', () => {
  let errorsInvoicesModule: ErrorsInvoicesModule;

  beforeEach(() => {
    errorsInvoicesModule = new ErrorsInvoicesModule();
  });

  it('should create an instance', () => {
    expect(errorsInvoicesModule).toBeTruthy();
  });
});
