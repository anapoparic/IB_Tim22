import { TestBed } from '@angular/core/testing';

import { ReviewReportService } from './review-report.service';

describe('ReviewReportService', () => {
  let service: ReviewReportService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReviewReportService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
