import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerformersAddComponent } from './performers-add.component';

describe('PerformersAddComponent', () => {
  let component: PerformersAddComponent;
  let fixture: ComponentFixture<PerformersAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PerformersAddComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PerformersAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
