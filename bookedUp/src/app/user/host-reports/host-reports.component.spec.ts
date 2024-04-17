import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HostReportsComponent } from './host-reports.component';

describe('HostReportsComponent', () => {
  let component: HostReportsComponent;
  let fixture: ComponentFixture<HostReportsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HostReportsComponent]
    });
    fixture = TestBed.createComponent(HostReportsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
