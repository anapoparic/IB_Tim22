import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccommodationHostReviewComponent } from './accommodation-host-review.component';

describe('AccommodationHostReviewComponent', () => {
  let component: AccommodationHostReviewComponent;
  let fixture: ComponentFixture<AccommodationHostReviewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AccommodationHostReviewComponent]
    });
    fixture = TestBed.createComponent(AccommodationHostReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
