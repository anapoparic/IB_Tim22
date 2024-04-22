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
import { CreateRootComponent } from '../create-root/create-root.component';
import { Template } from '../model/enum/template.enum';

@Component({
  selector: 'app-certificates',
  templateUrl: './certificates.component.html',
  styleUrls: ['./certificates.component.css']
})
export class CertificatesComponent implements OnInit {


  selectedClass: string = 'root';
  certifications: Observable<Certificate[]> = new Observable<[]>;
  Template = Template;


  constructor(private router: Router, private route: ActivatedRoute, private certificationService: CertificatesService, private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.certifications = this.certificationService.getAllRootCertificates();
    });
  }

  refreshView(): void {
    this.selectedClass = "root";
    this.certifications = this.certificationService.getAllRootCertificates();
  }

  revoke(id: number): void {
    const reasons = [
      { label: 'Unspecified reason for revocation', value: ReasonForRevoke.UNSPECIFIED },
      { label: 'Private key compromise', value: ReasonForRevoke.KEY_COMPROMISE },
      { label: 'CA compromise', value: ReasonForRevoke.CA_COMPROMISE },
      { label: 'Change in affiliation', value: ReasonForRevoke.AFFILIATION_CHANGED },
      { label: 'Superseded certificate', value: ReasonForRevoke.SUPERSEDED },
      { label: 'Cessation of operation', value: ReasonForRevoke.CESSATION_OF_OPERATION },
      { label: 'Certificate hold - temporary suspension', value: ReasonForRevoke.CERTIFICATE_HOLD },
      { label: 'Removal from Certificate Revocation List', value: ReasonForRevoke.REMOVE_FROM_CRL },
      { label: 'Withdrawal of associated privileges', value: ReasonForRevoke.PRIVILEGE_WITHDRAWN },
      { label: 'Attribute authority compromise', value: ReasonForRevoke.AACOMPROMISE }
    ];

    const checkboxes = reasons.map(reason => `
      <div style="text-align: left;">
        <label class="swal2-checkbox">
          <input type="radio" name="revokeReason" value="${reason.value}">
          ${reason.label}
        </label>
      </div>
    `).join('');

    Swal.fire({
      title: 'Revoke Certificate',
      html: checkboxes,
      showCancelButton: true,
      confirmButtonText: 'Revoke',
      cancelButtonText: 'Cancel',
      preConfirm: () => {
        const selectedReasonValue = (document.querySelector('input[name="revokeReason"]:checked') as HTMLInputElement)?.value;
        if (!selectedReasonValue) {
          Swal.showValidationMessage('Please select a reason.');
        }
        return selectedReasonValue;
      }
    }).then((result) => {
      if (result.isConfirmed) {
        const selectedReasonValue = result.value;
        const selectedReasonEnumValue = this.mapReasonToEnum(selectedReasonValue);

        if (selectedReasonEnumValue) {

          this.certificationService.revokeCertificate(id, selectedReasonEnumValue).subscribe(
            () => {
              Swal.fire('Certificate Revoked!', `Certificate with ID ${id} has been successfully revoked.`, 'success');
            },
            (error) => {
              Swal.fire('Error!', `Certificate with ID ${id} cannot be revoked.`, 'error');
            }
          );
        } else {
          Swal.fire('Error!', 'Invalid reason selected.', 'error');
        }
      }
    });
  }

  private mapReasonToEnum(reasonValue: string): string | null {
    switch (reasonValue) {
      case ReasonForRevoke.UNSPECIFIED:
        return "UNSPECIFIED";
      case ReasonForRevoke.KEY_COMPROMISE:
        return "KEY_COMPROMISE";
      case ReasonForRevoke.CA_COMPROMISE:
        return "CA_COMPROMISE";
      case ReasonForRevoke.AFFILIATION_CHANGED:
        return "AFFILIATION_CHANGED";
      case ReasonForRevoke.SUPERSEDED:
        return "SUPERSEDED";
      case ReasonForRevoke.CESSATION_OF_OPERATION:
        return "CESSATION_OF_OPERATION";
      case ReasonForRevoke.CERTIFICATE_HOLD:
        return "CERTIFICATE_HOLD";
      case ReasonForRevoke.REMOVE_FROM_CRL:
        return "REMOVE_FROM_CRL";
      case ReasonForRevoke.PRIVILEGE_WITHDRAWN:
        return "PRIVILEGE_WITHDRAWN";
      case ReasonForRevoke.AACOMPROMISE:
        return "AACOMPROMISE";
      default:
        return null;
    }
  }

  openDetails(certificateId: number): void {
    this.certificationService.getCertificateById(certificateId).subscribe(certificate => {
      const dialogRef = this.dialog.open(CertificatesTreeComponent, {
        width: 'auto',
        data: this.certificationService.getPathToRoot(certificateId)
      });
    });
  }

  signCertificate(certificateAlias: string) {
    const dialogRef = this.dialog.open(CreateRequestComponent, {
      width: 'auto',
      data: certificateAlias
    });
  }
  
  signRootCertificate() {
    const dialogRef = this.dialog.open(CreateRootComponent, {
      width: 'auto'
    });
  }
  openDescendantsOfRoot(certificateId: number): void {
    this.selectedClass = "tree";
    this.certifications = this.certificationService.getAllDescendantsOfRoot(certificateId);
  }

}