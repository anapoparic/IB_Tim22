import { Component, OnInit } from '@angular/core';
import Swal from 'sweetalert2';
import { Template } from '../../certificate/model/enum/template.enum';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { CertificateRequest } from '../model/certificateRequest.model';
import { RequestsService } from '../requests.service';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.css']
})
export class RequestsComponent implements OnInit {

  requests: Observable<CertificateRequest[]> = new Observable<[]>;


  constructor(private router: Router, private route: ActivatedRoute, private requestService: RequestsService) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.requests = this.requestService.getAllActiveRequests();
    });
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

    if (template === Template.ROOT) {
      extensions.push({ extension: 'X509Extension.keyUsage', value: 'true, keyCertSign, cRLSign' });
      extensions.push({ extension: 'X509Extension.basicConstraints', value: 'true' });
      extensions.push({ extension: 'X509Extension.subjectKeyIdentifier', value: 'false' });
      extensions.push({ extension: 'X509Extension.certificatePolicies', value: 'false, 1.3.6.1.4.1.99999.1' });
      extensions.push({ extension: 'X509Extension.extendedKeyUsage', value: 'false, anyExtendedKeyUsage' });
    } else if (template === Template.CA) {
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
      error: () => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'An error is occured. Please try again.',
        });
      }
    });
  }

}
