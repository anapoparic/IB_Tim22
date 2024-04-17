import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnloggedNavBarComponent } from './unlogged-nav-bar.component';

describe('UnloggedNavBarComponent', () => {
  let component: UnloggedNavBarComponent;
  let fixture: ComponentFixture<UnloggedNavBarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UnloggedNavBarComponent]
    });
    fixture = TestBed.createComponent(UnloggedNavBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
