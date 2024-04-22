import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateRequestComponent } from './create-request.component';

describe('CreateRequestComponent', () => {
  let component: CreateRequestComponent;
  let fixture: ComponentFixture<CreateRequestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateRequestComponent]
    });
    fixture = TestBed.createComponent(CreateRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
