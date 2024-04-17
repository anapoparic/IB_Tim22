import { Component , OnInit } from '@angular/core';
import { Router, ActivatedRoute  } from '@angular/router';

@Component({
  selector: 'app-check-inbox',
  templateUrl: './check-inbox.component.html',
  styleUrls: ['./check-inbox.component.css']
})
export class CheckInboxComponent implements OnInit {

  userEmail: string = '';

  constructor(private router: Router, private route: ActivatedRoute) { }

  ngOnInit() {

    this.userEmail = this.route.snapshot.paramMap.get('email') || '';

    var backButton = document.getElementById("backButton");
    if (backButton) {
      backButton.addEventListener("click", () => {
        this.router.navigate(['/login']);
      });
    }

  }
}
