import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Request } from '../request/request.model';
import { RequestsService } from '../request/requests.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css', '../../../styles.css']
})

export class IndexComponent implements OnInit {

  selectedClass: string = 'requests';
  filter: string = 'requests';

  requests: Observable<Request[]> = new Observable<[]>;


  constructor(private router: Router, private route: ActivatedRoute, private requestService: RequestsService) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.filter = params['filter'] || 'requests';
      this.requests = this.requestService.getRequests();
    });
  }

  changeStyle(className: string): void {
    this.selectedClass = className;
    if (className === 'new-accommodations') {
      this.router.navigate(['/accommodation-requests'], {queryParams: {filter: 'certificates'}});
    } else {
      this.router.navigate(['/accommodation-requests'], {queryParams: {filter: 'requests'}});
    }
  }

  approveRequest(arg0: number) {
    throw new Error('Method not implemented.');
  }

  rejectRequest(arg0: number) {
    throw new Error('Method not implemented.');
    }

}
