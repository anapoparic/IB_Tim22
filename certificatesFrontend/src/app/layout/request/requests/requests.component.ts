import { Component, OnInit } from '@angular/core';
import Swal from 'sweetalert2';
import { Template } from '../../certificate/model/enum/template.enum';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { CertificateRequest } from '../model/certificateRequest.model';
import { RequestsService } from '../requests.service';
import { MatDialog } from '@angular/material/dialog';
import { AcceptRequestComponent } from '../accept-request/accept-request.component';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.css']
})
export class RequestsComponent implements OnInit {

  requests: Observable<CertificateRequest[]> = new Observable<[]>;


  constructor(private router: Router, private route: ActivatedRoute, private requestService: RequestsService , private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.requests = this.requestService.getAllActiveRequests();
    });
  }

  approveRequest(id: number) {
    const dialogRef = this.dialog.open(AcceptRequestComponent, {
      width: 'auto',
      data: this.requestService.getRequestById(id)
    });
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
