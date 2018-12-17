import { ErrorsModule } from './errors.module';

describe('ErrorsModule', () => {
    let statModule: ErrorsModule;

    beforeEach(() => {
        statModule = new ErrorsModule();
    });

    it('should create an instance', () => {
        expect(statModule).toBeTruthy();
    });
});
