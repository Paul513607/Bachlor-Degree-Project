import { TestBed } from '@angular/core/testing';

import { TimetableFileService } from './timetable-file.service';

describe('TimetableFileService', () => {
  let service: TimetableFileService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TimetableFileService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
