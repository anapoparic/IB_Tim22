import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Certificate } from '../model/certificate.model';
import { CertificatesService } from '../certificates.service';
import { ReasonForRevoke } from '../model/enum/reasonForRevoke.enum';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-certificates',
  templateUrl: './certificates.component.html',
  styleUrls: ['./certificates.component.css']
})
export class CertificatesComponent implements OnInit {


  certifications: Observable<Certificate[]> = new Observable<[]>;


  constructor(private router: Router, private route: ActivatedRoute, private certificationService: CertificatesService) {
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

  openDetails(arg0: number) {
    throw new Error('Method not implemented.');
  }

  //ovde se dodaje implementacija za dodavanje novog sertikata
  signCertificate(arg0: number) {
    throw new Error('Method not implemented.');
  }

}