import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Request } from '../request/request.model';
import { RequestsService } from '../request/requests.service';
import { Observable } from 'rxjs';
import { Certificate } from '../certificate/certificate.model';
import { CertificatesService } from '../certificate/certificates.service';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css', '../../../styles.css']
})

export class IndexComponent implements OnInit {

  selectedClass: string = 'requests';
  filter: string = 'requests';

  requests: Observable<Request[]> = new Observable<[]>;
  certifications: Observable<Certificate[]> = new Observable<[]>;


  constructor(private router: Router, private route: ActivatedRoute, private requestService: RequestsService, private certificationService: CertificatesService) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.filter = params['filter'] || 'requests';
      this.requests = this.requestService.getRequests();
      this.certifications = this.certificationService.getCertifications();
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

  approveRequest(arg0: number) {
    throw new Error('Method not implemented.');
  }

  rejectRequest(arg0: number) {
    throw new Error('Method not implemented.');
    }

  revoke(arg0: number) {
    throw new Error('Method not implemented.');
    }

}
