import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SingleAccommodationAnalyticsComponent } from './single-accommodation-analytics.component';

describe('SingleAccommodationAnalyticsComponent', () => {
  let component: SingleAccommodationAnalyticsComponent;
  let fixture: ComponentFixture<SingleAccommodationAnalyticsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SingleAccommodationAnalyticsComponent]
    });
    fixture = TestBed.createComponent(SingleAccommodationAnalyticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
