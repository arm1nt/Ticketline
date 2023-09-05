import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StandingSectorDialogComponent } from './standing-sector-dialog.component';

describe('StandingSectorDialogComponent', () => {
  let component: StandingSectorDialogComponent;
  let fixture: ComponentFixture<StandingSectorDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StandingSectorDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StandingSectorDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
