import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnlockUserDialogComponent } from './unlock-user-dialog.component';

describe('UnlockUserDialogComponent', () => {
  let component: UnlockUserDialogComponent;
  let fixture: ComponentFixture<UnlockUserDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UnlockUserDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UnlockUserDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
