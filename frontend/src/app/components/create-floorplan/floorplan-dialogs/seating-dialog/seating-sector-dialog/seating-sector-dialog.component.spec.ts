import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeatingSectorDialogComponent } from './seating-sector-dialog.component';

describe('SeatingSectorDialogComponent', () => {
  let component: SeatingSectorDialogComponent;
  let fixture: ComponentFixture<SeatingSectorDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SeatingSectorDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SeatingSectorDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
