import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CertificatesTreeComponent } from './certificates-tree.component';

describe('CertificatesTreeComponent', () => {
  let component: CertificatesTreeComponent;
  let fixture: ComponentFixture<CertificatesTreeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CertificatesTreeComponent]
    });
    fixture = TestBed.createComponent(CertificatesTreeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
