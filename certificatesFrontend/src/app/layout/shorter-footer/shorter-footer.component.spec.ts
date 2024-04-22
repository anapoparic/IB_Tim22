import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShorterFooterComponent } from './shorter-footer.component';

describe('ShorterFooterComponent', () => {
  let component: ShorterFooterComponent;
  let fixture: ComponentFixture<ShorterFooterComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShorterFooterComponent]
    });
    fixture = TestBed.createComponent(ShorterFooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
