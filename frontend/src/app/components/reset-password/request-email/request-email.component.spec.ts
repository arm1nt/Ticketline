import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestEmailComponent } from './request-email.component';

describe('RequestEmailComponent', () => {
  let component: RequestEmailComponent;
  let fixture: ComponentFixture<RequestEmailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RequestEmailComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RequestEmailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
