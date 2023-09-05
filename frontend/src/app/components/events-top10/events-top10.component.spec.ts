import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventsTop10Component } from './events-top10.component';

describe('EventsTop10Component', () => {
  let component: EventsTop10Component;
  let fixture: ComponentFixture<EventsTop10Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EventsTop10Component ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventsTop10Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
