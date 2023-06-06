import { TestBed } from '@angular/core/testing';

import { AlgorithmParamsService } from './algorithm-params.service';

describe('AlgorithmParamsService', () => {
  let service: AlgorithmParamsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AlgorithmParamsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
