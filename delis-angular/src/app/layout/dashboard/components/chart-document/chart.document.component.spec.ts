import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ChartDocumentComponent } from "./chart.document.component";

describe('ChartDocumentComponent', () => {
    let component: ChartDocumentComponent;
    let fixture: ComponentFixture<ChartDocumentComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [ ChartDocumentComponent ]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(ChartDocumentComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
