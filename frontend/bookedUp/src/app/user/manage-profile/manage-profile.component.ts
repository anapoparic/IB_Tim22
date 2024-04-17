import {Component, ElementRef, NgZone, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {User} from "../model/user.model";
import {UserService} from "../user.service";
import {AuthService} from "../../infrastructure/auth/auth.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import Swal from "sweetalert2";
import {tap} from "rxjs";
import {Role} from "../model/role.enum";
import {GuestService} from "../guest/guest.service";
import {HostService} from "../host/host.service";
import {PhotoService} from "../../shared/photo/photo.service";

@Component({
  selector: 'app-manage-profile',
  templateUrl: './manage-profile.component.html',
  styleUrls: ['./manage-profile.component.css']
})
export class ManageProfileComponent implements OnInit {

  isPasswordVisible: boolean = false;
  loggedUser!: User;
  updatedUser!: User;


  updateProfilePicture: string  = '';
  displayedImageUrl: string | null = null;

  @ViewChild('fileInput') fileInput!: ElementRef;

  updateForm: FormGroup | undefined;


  constructor(private userService: UserService,private photoService:PhotoService, private guestService: GuestService,private hostService: HostService, private router: Router,
    private authService: AuthService, private formBuilder: FormBuilder,private zone: NgZone
  ) {
    this.updateForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: [{value: '', disabled: true}],
      password: ['', Validators.required],
      phone: [0, Validators.required],
      streetAndNumber: ['', Validators.required],
      city: ['', Validators.required],
      postalCode: ['', Validators.required],
      country: ['', Validators.required],

    });
  }

  ngOnInit() {
    this.userService.getUser(this.authService.getUserID()).subscribe(
        (user: User) => {
          this.loggedUser = user;

          this.loadPhotos();

          this.updateForm!.setValue({
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            password: user.password,
            phone: user.phone,
            streetAndNumber: user.address?.streetAndNumber,
            city: user.address?.city,
            postalCode: user.address?.postalCode,
            country: user.address?.country,
          });
        },
        (error) => {
          console.error('Error loading user:', error);
        }
    );
  }

  ngAfterViewInit() {
    this.fileInput.nativeElement.addEventListener('change', (event: any) => {

      const file = event.target.files[0];
      console.log('Nova slika dodata', event.target.files[0]);
      if (file) {
        const imageUrl = URL.createObjectURL(file);
        this.displayedImageUrl = imageUrl;
      }
    });

  }

  handleFileButtonClick() {
    this.fileInput.nativeElement.click();

    this.fileInput.nativeElement.addEventListener('change', (event: any) => {

      const file = event.target.files[0];
      console.log('Nova slika dodata', event.target.files[0]);

      if (file) {
        const imageUrl = URL.createObjectURL(file);
        this.displayedImageUrl = imageUrl;
        this.convertBlobToFile(this.displayedImageUrl??'')
          .then(file => {
            console.log('Converted file:', file);

            // Now you can upload the file or use it as needed.
            this.photoService.uploadImage(file).subscribe(
              response => {
                console.log('Image uploaded successfully:', response);
                this.updateProfilePicture = 'images/'+file.name;
                console.log("ovo je naziv koji se prosledjuje ", this.updateProfilePicture);
                // Handle success as needed
              },
              error => {
                console.error('Error uploading image:', error);
                // Handle error as needed
              }
            );
          })
          .catch(error => {
            console.error('Error converting blob to file:', error);
          });
      }
    });

  }

  togglePasswordVisibility() {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  convertBlobToFile(blobUrl: string): Promise<File> {
    return fetch(blobUrl)
      .then(response => response.blob())
      .then(blob => new File([blob], `us${Date.now()}.png`, { type: blob.type }));
  }


  updateUser() {

    if(this.updateProfilePicture=="" && this.loggedUser.profilePicture?.url ){
      this.updateProfilePicture = this.loggedUser.profilePicture?.url;
    }

    if (this.validate()) {

      this.updatedUser = {
        id: this.authService.getUserID(),
        firstName: this.updateForm!.get('firstName')!.value,
        lastName: this.updateForm!.get('lastName')!.value,
        password: this.updateForm!.get('password')!.value,
        phone: this.updateForm!.get('phone')!.value,
        email: this.loggedUser.email,
        role: this.loggedUser.role,


        profilePicture: {url: this.updateProfilePicture, caption: 'profilePicture'}, // Dodajte ovo

        address: {
          streetAndNumber: this.updateForm!.get('streetAndNumber')!.value,
          city: this.updateForm!.get('city')!.value,
          postalCode: this.updateForm!.get('postalCode')!.value,
          country: this.updateForm!.get('country')!.value,
          latitude: 0,
          longitude: 0
        }
      };

      this.userService.updateUser(this.authService.getUserID(), this.updatedUser).pipe(
          tap((response) => {
            this.loggedUser = {...this.loggedUser, ...this.updatedUser};
            this.displayedImageUrl = null; // Resetujte sliku nakon ažuriranja
          })
      ).subscribe(
          (response) => {
            Swal.fire({
              icon: 'success',
              title: 'User Updated',
              text: 'User information has been successfully updated.',
            });

            this.router.navigate(['/']);
          },
          (error) => {
            console.error('Error updating user:', error);
            Swal.fire({
              icon: 'error',
              title: 'Error',
              text: 'An error occurred while updating user information.',
            });
          }
      );
    }
  }

  validate(): boolean {
    if (this.updateForm && this.updateForm.dirty) {
      if (this.checkForEmptyValues() && this.updateForm.valid) {
        return true;
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Validation failed',
          text: 'Some fields are empty or have invalid values.',
        });
        return false;
      }
    } else {
      Swal.fire({
        icon: 'error',
        title: 'Validation failed',
        text: 'No changes detected.',

      });
      return false;
    }
  }

  checkForEmptyValues(): boolean {
    const formValues = this.updateForm!.value;

    return Object.values(formValues).every(value => {
      if (typeof value === 'string') {
        return value.trim() !== '';
      } else if (typeof value === 'number') {
        return value !== 0;
      } else {
        return true;
      }
    });
  }

  deleteUser() {
    if (this.loggedUser.role === Role.Guest) {
      this.deleteGuest();
    }else{
      this.deleteHost();

    }
  }

  private deleteGuest(){
    Swal.fire({
      title: 'Are you sure?',
      text: 'You won\'t be able to revert this!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'No, cancel!',
    }).then((result) => {
      if (result.isConfirmed) {
        if (this.loggedUser?.id != null) {
          this.logout();
          this.guestService.deleteGuest(this.loggedUser?.id).subscribe(
              () => {
                Swal.fire('Deleted!', 'Your account has been deleted.', 'success');
              },
              (error) => {
                Swal.fire('Error', 'You cannot delete your account because you have active reservations in the future. You will be unlogged now!', 'error');
              }
          );
        }
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        Swal.fire('Cancelled', 'Your account is safe :)', 'info');
      }
    });
  }



  private deleteHost() {
    Swal.fire({
      title: 'Are you sure?',
      text: 'You won\'t be able to revert this!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'No, cancel!',
    }).then((result) => {
      if (result.isConfirmed) {
        if (this.loggedUser?.id != null) {
          this.logout();
          this.hostService.deleteHost(this.loggedUser?.id).subscribe(
              () => {
                Swal.fire('Deleted!', 'Your account has been deleted.', 'success');
              },
              (error) => {
                Swal.fire('Error', 'You cannot delete your account because you have active reservations in the future. You will be unlogged now!', 'error');
              }
          );
        }
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        Swal.fire('Cancelled', 'Your account is safe :)', 'info');
      }
    });
  }

  private logout() {
    this.authService.logout().subscribe(
      () => {
        localStorage.removeItem('user');
        this.authService.setUser();
        // Use Angular Zone to trigger change detection
        this.zone.run(() => {
          this.router.navigate(['/']).then(() => {
            // Reload the current route to reflect changes (optional)
            this.router.navigate([this.router.url]);
          });
        });
      },
      (error) => {
        console.error('Logout error:', error);
      }
    );
  }


  loadPhotos() {
    if(this.loggedUser.profilePicture){
      this.photoService.loadPhoto(this.loggedUser.profilePicture).subscribe(
        (data) => {
          this.createImageFromBlob(data).then((url: string) => {
            this.displayedImageUrl=url;
          }).catch(error => {
            console.error("Greška prilikom konverzije slike ${imageName}: ",this.displayedImageUrl, error);
          });
        },
        (error) => {
          console.log("Doslo je do greske pri ucitavanju slike ${imageName}:" , error);
        }
      );
    }
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
}
