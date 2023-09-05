import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateEventhallComponent } from './create-eventhall.component';

describe('CreateEventhallComponent', () => {
  let component: CreateEventhallComponent;
  let fixture: ComponentFixture<CreateEventhallComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateEventhallComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateEventhallComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
