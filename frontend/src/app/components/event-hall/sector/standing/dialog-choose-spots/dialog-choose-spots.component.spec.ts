import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogChooseSpotsComponent } from './dialog-choose-spots.component';

describe('DialogChooseSpotsComponent', () => {
  let component: DialogChooseSpotsComponent;
  let fixture: ComponentFixture<DialogChooseSpotsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogChooseSpotsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DialogChooseSpotsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
