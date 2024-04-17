import {Component, OnInit, signal} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, concatMap, map, of } from 'rxjs';
import { PhotoService } from 'src/app/shared/photo/photo.service';
import Swal from 'sweetalert2';
import { User } from '../model/user.model';
import { UserService } from '../user.service';
import {UserReportService} from "../user-report/user-report.service";

@Component({
  selector: 'app-user-reports',
  templateUrl: './user-reports.component.html',
  styleUrls: ['./user-reports.component.css']
})
export class UserReportsComponent implements OnInit {
  users: Observable<User[]> = new Observable();
  selectedClass: string = 'all-accommodations';
  filter: string = 'all';

  photoDict: { accId: number, url: string }[] = [];
  user: User[] = [];
  reportedReasons: string[] = [];


  constructor(private userService: UserService, private router: Router, private route: ActivatedRoute, private photoService: PhotoService, private userReportService: UserReportService) {
  }


  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.filter = params['filter'] || 'all';
      this.loadUsers();
    });
  }

  changeStyle(className: string): void {
    this.selectedClass = className;
    if (className === 'changed-accommodations') {
      this.router.navigate(['/user-reports'], {queryParams: {filter: 'changed'}});
    } else if (className === 'new-accommodations') {
      this.router.navigate(['/user-reports'], {queryParams: {filter: 'new'}});
    } else {
      this.router.navigate(['/user-reports'], {queryParams: {filter: 'all'}});
    }
  }

  private loadUsers(): void {
    if (this.filter === 'all') {
      this.users = this.userService.getUsers();
      this.userService.getUsers().subscribe((results) => {
        this.user = results;
        this.loadPhotos();
      });
    } else if (this.filter === 'new') {
      this.users = this.userService.getBlockedUsers();
      this.userService.getBlockedUsers().subscribe((results) => {
        this.user = results;
        console.log('Blocked Users:', this.user); // Dodajte ovu liniju

        this.loadPhotos();
      });
    } else {
      this.users = this.userReportService.getAllReportedUsers();
      this.userReportService.getAllReportedUsers().subscribe((results) => {
        this.user = results;
        this.loadPhotos();

      });
    }

  }


  showReportReasons(id: number): void {
    this.userReportService.getReportReasonsForUser(id)
      .subscribe((reasons: string[]) => {
        this.reportedReasons = reasons;

        // Dodajte ID korisnika kao dodatni parametar kada pozivate askForBlockConfirmation
        this.showSwalWithReportedReasons(id);
      });
  }

  showSwalWithReportedReasons(userId: number): void {
    Swal.fire({
      title: 'Reported Reasons',
      html: this.generateReportedReasonsHtml(),
      confirmButtonText: 'OK',
      showCancelButton: true,
      cancelButtonText: 'Block User',
    }).then((result) => {
      if (result.dismiss === Swal.DismissReason.cancel) {
        this.askForBlockConfirmation(userId);
      }
    });
  }

  askForBlockConfirmation(userId: number): void {
    Swal.fire({
      title: 'Block User Confirmation',
      text: 'Are you sure you want to block this user?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, block user!',
      cancelButtonText: 'Cancel',
    }).then((result) => {
      if (result.isConfirmed) {
        this.blockUser(userId);
      }
    });
  }

  blockUser(userId: number): void {
    this.userService.blockUser(userId).subscribe(
      (blockedUser: User) => {console.log('User blocked:', blockedUser);this.loadUsers();}, (error) => {console.error('Error blocking user:', error);}
    );
  }

  generateReportedReasonsHtml(): string {
    // Generisanje HTML-a sa izlistanim razlozima prijave
    return this.reportedReasons.map((reason, index) => `
    <div style="margin-bottom: 8px;">
      <div style="border: 1px solid #ccc; padding: 8px; border-radius: 8px;">
        ${index + 1}. ${reason}
      </div>
    </div>`).join('');
  }

  showBlockedReasons(id: number): void {
    this.userReportService.getReportReasonsForUser(id)
      .subscribe((reasons: string[]) => {
        // Prikazivanje Swal-a sa razlozima blokiranja i dugmetom za deblokiranje
        this.showSwalWithBlockedReasons(id, reasons);
      });
  }

  showSwalWithBlockedReasons(userId: number, reasons: string[]): void {
    Swal.fire({
      title: 'Blocked Reasons',
      html: this.generateBlockedReasonsHtml(reasons),
      confirmButtonText: 'Unblock User',
      showCancelButton: true,
      cancelButtonText: 'Cancel',
    }).then((result) => {
      // Ako korisnik pritisne "Unblock User"
      if (result.isConfirmed) {
        console.log('User ID for unblocking:', this.userService.getUser(userId));
        this.unblockUser(userId);
      }
    });
  }
  unblockUser(userId: number): void {
    this.userService.unblockUser(userId).subscribe(
      (unblockedUser: User) => {
        // Logika nakon uspešnog deblokiranja korisnika
        console.log('User unblocked:', unblockedUser);
        this.loadUsers();      },
      (error) => {
        // Obrada greške
        console.error('Error unblocking user:', error);
      }
    );
  }


  generateBlockedReasonsHtml(reasons: string[]): string {
    // Generisanje HTML-a sa izlistanim razlozima blokiranja
    return reasons.map((reason, index) => `
    <div style="margin-bottom: 8px;">
      <div style="border: 1px solid #ccc; padding: 8px; border-radius: 8px;">
        ${index + 1}. ${reason}
      </div>
    </div>`).join('');
  }




  loadPhotos() {
    this.user.forEach((acc) => {
      if (acc.profilePicture) {
        this.photoService.loadPhoto(acc.profilePicture).subscribe(
            (data) => {
              this.createImageFromBlob(data).then((url: string) => {
                if (acc.id) {
                  this.photoDict.push({accId: acc.id, url: url});
                }
              }).catch(error => {
                console.error("Greška prilikom konverzije slike ${imageName}:", error);
              });
            },
            (error) => {
              console.log("Doslo je do greske pri ucitavanju slike ${imageName}:", error);
            }
        );
      }
    });
  }


  createImageFromBlob(imageBlob: Blob): Promise<string> {
    const reader = new FileReader();

    return new Promise<string>((resolve, reject) => {
      reader.onloadend = () => {
        resolve(reader.result as string);
      };
      reader.onerror = reject;
      reader.readAsDataURL(imageBlob);
    });
  }

  getPhotoUrl(accId: number | undefined): string | undefined {
    const photo = this.photoDict.find((item) => item.accId === accId);
    return photo ? photo.url : '';
  }

}
