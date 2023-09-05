import {ComponentFixture, TestBed} from '@angular/core/testing';

import {LocationPerformanceListComponent} from './location-performance-list.component';

describe('LocationPerformanceListComponent', () => {
  let component: LocationPerformanceListComponent;
  let fixture: ComponentFixture<LocationPerformanceListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LocationPerformanceListComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(LocationPerformanceListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
