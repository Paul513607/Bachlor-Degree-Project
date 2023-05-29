import { TestBed } from '@angular/core/testing';

import { AvailabilitySlotService } from './availability-slot.service';

describe('AvailabilitySlotService', () => {
  let service: AvailabilitySlotService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AvailabilitySlotService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
