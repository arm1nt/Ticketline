import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LockUserDialogComponent } from './lock-user-dialog.component';

describe('LockUserDialogComponent', () => {
  let component: LockUserDialogComponent;
  let fixture: ComponentFixture<LockUserDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LockUserDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LockUserDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
