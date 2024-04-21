import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CertificateRequest } from '../request/certificateRequest.model';
import { RequestsService } from '../request/requests.service';
import { Observable } from 'rxjs';
import { Certificate } from '../certificate/certificate.model';
import { CertificatesService } from '../certificate/certificates.service';
import Swal from 'sweetalert2';
import {Template} from "../certificate/template.enum";
import {ReasonForRevoke} from "../certificate/reasonForRevoke.enum";

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css', '../../../styles.css']
})

export class IndexComponent implements OnInit {

  selectedClass: string = 'requests';
  filter: string = 'requests';

  requests: Observable<CertificateRequest[]> = new Observable<[]>;
  certifications: Observable<Certificate[]> = new Observable<[]>;


  constructor(private router: Router, private route: ActivatedRoute, private requestService: RequestsService, private certificationService: CertificatesService) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.filter = params['filter'] || 'requests';
      this.requests = this.requestService.getAllActiveRequests();
      this.certifications = this.certificationService.getAllCertificates();
    });
  }

  changeStyle(className: string): void {
    this.selectedClass = className;
    if (className === 'certifications') {
      this.router.navigate(['/index'], {queryParams: {filter: 'certifications'}});
    } else {
      this.router.navigate(['/index'], {queryParams: {filter: 'requests'}});
    }
  }

  approveRequest(id: number) {
    const templateOptions = Object.values(Template).map(value => ({ value, label: value }));

    const templateSelect = document.getElementById('template') as HTMLSelectElement;
    const seeExtensionsBtn = document.getElementById('seeExtensionsBtn') as HTMLButtonElement;
    const extensionsDiv = document.getElementById('extensions');

    // Check if extensionsDiv exists
    if (extensionsDiv) {
      templateSelect.addEventListener('change', () => {
        seeExtensionsBtn.style.display = 'block';
        extensionsDiv.style.display = 'none';
      });

      seeExtensionsBtn.addEventListener('click', () => {
        this.showExtensions(templateSelect.value as Template, extensionsDiv);
      });
    }

    Swal.fire({
      title: 'Accept Certificate Request',
      html: this.generateFormHtml(templateOptions),
      showCancelButton: true,
      confirmButtonText: 'Accept',
      cancelButtonText: 'Cancel',
      preConfirm: () => {
        return this.getFormData();
      }
    }).then((result) => {
      if (result.isConfirmed) {
        this.handleAcceptance(id);
      }
    });
  }



  generateFormHtml(templateOptions: { value: string, label: string }[]): string {
    return `
    <label for="template">Select Template:</label>
    <select id="template" class="swal2-select">
      ${templateOptions.map(option => `<option value="${option.value}">${option.label}</option>`).join('')}
    </select>
    <br>
    <label for="issuer">Select Issuer:</label>
    <select id="issuer" class="swal2-select">
      <option value="CA1">CA1</option>
      <option value="CA2">CA2</option>
    </select>
    <br>
    <input type="text" id="alias" class="swal2-input" placeholder="Alias">
    <br>
    <button id="seeExtensionsBtn" class="swal2-confirm swal2-styled" style="display: none;">See Extensions</button>
    <div id="extensions" style="display: none;"></div>
  `;
  }

  showExtensions(template: Template, extensionsDiv: HTMLElement): void {
    const extensions = this.getExtensionsForTemplate(template);
    extensionsDiv.innerHTML = this.generateExtensionsTable(extensions);
    extensionsDiv.style.display = 'block';
  }

  getExtensionsForTemplate(template: Template): { extension: string, value: string }[] {
    let extensions: { extension: string, value: string }[] = [];

    if (template === Template.CA) {
      extensions.push({ extension: 'X509Extension.keyUsage', value: 'true, keyCertSign, cRLSign' });
      extensions.push({ extension: 'X509Extension.basicConstraints', value: 'true' });
      extensions.push({ extension: 'X509Extension.subjectKeyIdentifier', value: 'false' });
      extensions.push({ extension: 'X509Extension.certificatePolicies', value: 'false, 1.3.6.1.4.1.99999.1' });
      extensions.push({ extension: 'X509Extension.extendedKeyUsage', value: 'false, anyExtendedKeyUsage' });
    } else if (template === Template.INTERMEDIATE) {
      extensions.push({ extension: 'X509Extension.keyUsage', value: 'true, keyCertSign, cRLSign' });
      extensions.push({ extension: 'X509Extension.basicConstraints', value: 'true' });
      extensions.push({ extension: 'X509Extension.subjectKeyIdentifier', value: 'false' });
      extensions.push({ extension: 'X509Extension.certificatePolicies', value: 'false, 1.3.6.1.4.1.99999.2' });
      extensions.push({ extension: 'X509Extension.extendedKeyUsage', value: 'false, anyExtendedKeyUsage' });
    } else if (template === Template.END_ENTITY) {
      extensions.push({ extension: 'X509Extension.keyUsage', value: 'true, digitalSignature, keyEncipherment' });
      extensions.push({ extension: 'X509Extension.basicConstraints', value: 'true' });
      extensions.push({ extension: 'X509Extension.subjectKeyIdentifier', value: 'false' });
      extensions.push({ extension: 'X509Extension.certificatePolicies', value: 'false, 1.3.6.1.4.1.99999.3' });
      extensions.push({ extension: 'X509Extension.extendedKeyUsage', value: 'false, anyExtendedKeyUsage' });
    }

    return extensions;
  }

  generateExtensionsTable(extensions: { extension: string, value: string }[]): string {
    return `
    <table>
      <tr>
        <th>Extension</th>
        <th>Value</th>
      </tr>
      ${extensions.map(ext => `<tr><td>${ext.extension}</td><td>${ext.value}</td></tr>`).join('')}
    </table>
  `;
  }

  getFormData(): { template: Template, issuer: string, alias: string } {
    const template = (document.getElementById('template') as HTMLSelectElement).value as Template;
    const issuer = (document.getElementById('issuer') as HTMLSelectElement).value;
    const alias = (document.getElementById('alias') as HTMLInputElement).value;
    return { template, issuer, alias };
  }

  handleAcceptance(id: number): void {
    // Obrada prihvatanja zahteva
    // this.requestService.acceptRequest(id, template, issuer, alias).subscribe(...);
    Swal.fire({
      icon: 'success',
      title: 'Certificate Request Accepted',
      text: `Certificate request with ID ${id} has been successfully accepted.`,
    });
    this.requests = this.requestService.getAllActiveRequests();
  }


  rejectRequest(id: number) {
    this.requestService.deleteRequest(id).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Successfully Rejected',
          text: 'You sucessfully reject this certificate request.',
        });
        this.requests = this.requestService.getAllActiveRequests();
      },
      error: (error) => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'An error is occured. Please try again.',
        });
      }
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

}
