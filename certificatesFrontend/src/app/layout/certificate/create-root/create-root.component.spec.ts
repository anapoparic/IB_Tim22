import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateRootComponent } from './create-root.component';

describe('CreateRequestComponent', () => {
  let component: CreateRootComponent;
  let fixture: ComponentFixture<CreateRootComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateRootComponent]
    });
    fixture = TestBed.createComponent(CreateRootComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
