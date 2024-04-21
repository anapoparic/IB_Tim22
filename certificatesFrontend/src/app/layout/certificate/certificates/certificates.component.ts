import { Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Certificate } from '../model/certificate.model';
import { CertificatesService } from '../certificates.service';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ReasonForRevoke } from '../model/enum/reasonForRevoke.enum';
import Swal from 'sweetalert2';
import { CreateRequestComponent } from '../create-request/create-request.component';
import { CertificatesTreeComponent } from '../certificates-tree/certificates-tree.component';

@Component({
  selector: 'app-certificates',
  templateUrl: './certificates.component.html',
  styleUrls: ['./certificates.component.css']
})
export class CertificatesComponent implements OnInit {


  certifications: Observable<Certificate[]> = new Observable<[]>;


  constructor(private router: Router, private route: ActivatedRoute, private certificationService: CertificatesService, private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.certifications = this.certificationService.getAllCertificates();
    });
  }

  revoke(id: number) {
    const reasons = Object.values(ReasonForRevoke).map(value => ({ value, label: value }));

    const checkboxes = reasons.map(reason => `
    <div style="text-align: left;">
      <label class="swal2-checkbox" style="text-align: right;">
        <input type="radio" name="revokeReason" value="${reason.value}">
        <span class="swal2-label" style="text-align: right;">${reason.label}</span>
      </label>
    </div>
  `).join('<br>');

    Swal.fire({
      title: 'Revoke Certificate',
      html: checkboxes,
      showCancelButton: true,
      confirmButtonText: 'Revoke',
      cancelButtonText: 'Cancel',
      preConfirm: () => {
        const selectedReason = (document.querySelector('input[name="revokeReason"]:checked') as HTMLInputElement)?.value;
        if (!selectedReason) {
          Swal.showValidationMessage('Please select a reason.');
        }
        return selectedReason;
      }
    }).then((result) => {
      if (result.isConfirmed) {
        const reason = result.value;
        // Ovde možeš izvršiti akciju za poništenje certifikata sa odabranim razlogom
        // Na primer: this.certificationService.revokeCertificate(id, reason).subscribe(...);
        Swal.fire('Certificate Revoked!', `Certificate with ID ${id} has been successfully revoked.`, 'success');
      }
    });
  }

  openDetails(certificateId: number): void {
    this.certificationService.getCertificateById(certificateId).subscribe(certificate => {
      const dialogRef = this.dialog.open(CertificatesTreeComponent, {
        width: 'auto',
        data: certificate
      });
    });
  }

  //ovde se dodaje implementacija za dodavanje novog sertikata
  signCertificate(arg0: number) {
    const dialogRef = this.dialog.open(CreateRequestComponent, {
      width: 'auto'
    });
  }

}