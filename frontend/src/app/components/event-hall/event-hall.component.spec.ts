import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventHallComponent } from './event-hall.component';

describe('SeatChartComponent', () => {
  let component: EventHallComponent;
  let fixture: ComponentFixture<EventHallComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EventHallComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventHallComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
