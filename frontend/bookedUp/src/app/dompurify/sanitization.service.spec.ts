import { TestBed } from '@angular/core/testing';

import { SanitazionService } from './sanitization.service';

describe('SanitazionService', () => {
  let service: SanitazionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SanitazionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
