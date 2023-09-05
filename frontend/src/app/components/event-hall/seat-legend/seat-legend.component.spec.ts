import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeatLegendComponent } from './seat-legend.component';

describe('SeatLegendComponent', () => {
  let component: SeatLegendComponent;
  let fixture: ComponentFixture<SeatLegendComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SeatLegendComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SeatLegendComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
