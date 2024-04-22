import { ComponentFixture, TestBed } from '@angular/core/testing';

import { YearlyAnalyticsComponent } from './yearly-analytics.component';

describe('YearlyAnalyticsComponent', () => {
  let component: YearlyAnalyticsComponent;
  let fixture: ComponentFixture<YearlyAnalyticsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [YearlyAnalyticsComponent]
    });
    fixture = TestBed.createComponent(YearlyAnalyticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
