import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateFloorplanComponent } from './create-floorplan.component';

describe('CreateFloorplanComponent', () => {
  let component: CreateFloorplanComponent;
  let fixture: ComponentFixture<CreateFloorplanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateFloorplanComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateFloorplanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
