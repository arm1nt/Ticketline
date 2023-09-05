import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LayoutAddComponent } from './layout-add.component';

describe('LayoutAddComponent', () => {
  let component: LayoutAddComponent;
  let fixture: ComponentFixture<LayoutAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LayoutAddComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LayoutAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
