import { TestBed } from '@angular/core/testing';

import { EventhallService } from './eventhall.service';

describe('EventhallService', () => {
  let service: EventhallService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EventhallService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
