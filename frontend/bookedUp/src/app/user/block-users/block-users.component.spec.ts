import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlockUsersComponent } from './block-users.component';

describe('BlockUsersComponent', () => {
  let component: BlockUsersComponent;
  let fixture: ComponentFixture<BlockUsersComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BlockUsersComponent]
    });
    fixture = TestBed.createComponent(BlockUsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
