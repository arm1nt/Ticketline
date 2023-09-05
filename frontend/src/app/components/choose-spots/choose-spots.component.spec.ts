import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChooseSpotsComponent } from './choose-spots.component';

describe('ChoosePlacesComponent', () => {
  let component: ChooseSpotsComponent;
  let fixture: ComponentFixture<ChooseSpotsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChooseSpotsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChooseSpotsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
