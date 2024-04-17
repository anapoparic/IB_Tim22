import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthGuard } from './infrastructure/auth/guard/auth.guard';
import { IndexComponent } from './layout/index/index.component';
import { SearchComponent } from './layout/search/search.component';

import { AccommodationDetailsComponent } from './accommodation/accommodation-details/accommodation-details.component';
import { AccommodationRequestsComponent } from './accommodation/accommodation-requests/accommodation-requests.component';
import { CreateAccommodationComponent } from './accommodation/create-accommodation/create-accommodation.component';
import { UpdateAccommodationComponent } from './accommodation/update-accommodation/update-accommodation.component';
import { AccommodationsComponent } from './accommodation/accommodations/accommodations.component';
import { FavouritesComponent} from "./accommodation/favourites/favourites.component";

import { ReservationRequestsComponent } from './reservation/reservation-requests/reservation-requests.component';
import { CreateReservationComponent } from './reservation/create-reservation/create-reservation.component';
import { ReservationDetailsComponent } from './reservation/reservation-details/reservation-details.component';
import { ReservationsComponent } from "./reservation/reservations/reservations.component";

import { ManageProfileComponent } from './user/manage-profile/manage-profile.component';
import { UserReportsComponent } from './user/user-reports/user-reports.component';
import { BlockUsersComponent } from './user/block-users/block-users.component';

import { CheckInboxComponent } from './layout/check-inbox/check-inbox.component';
import { ForgotPasswordComponent } from './layout/forgot-password/forgot-password.component';

import { AccommodationReviewsComponent } from './review/accommodation-reviews/accommodation-reviews.component';
import { AccommodationHostReviewComponent } from './review/accommodation-host-review/accommodation-host-review.component';
import { AddReviewComponent } from './review/add-review/add-review.component';
import { GuestReviewsComponent } from './review/guest-reviews/guest-reviews.component';
import { HostReviewsComponent } from './review/host-reviews/host-reviews.component';
import { ReviewReportsComponent } from './review/review-reports/review-reports.component';

import { AnalyticsComponent } from './analytics/analytics/analytics.component';

import { LoginComponent } from './infrastructure/auth/login/login.component';
import { RegistrationComponent } from "./infrastructure/auth/registration/registration.component";
import {HostReportsComponent} from "./user/host-reports/host-reports.component";


const routes: Routes = [
  { path: '', component: IndexComponent },
  { path: 'search', component: SearchComponent },

  { path: 'accommodation-details/:id', component: AccommodationDetailsComponent },
  { path: 'accommodation-requests', component: AccommodationRequestsComponent, canActivate: [AuthGuard], data: { role: ['ROLE_ADMIN'] }},
  { path: 'create-accommodation', component: CreateAccommodationComponent, canActivate: [AuthGuard], data: { role: ['ROLE_HOST'] }},
  { path: 'update-accommodation/:id', component: UpdateAccommodationComponent, canActivate: [AuthGuard], data: { role: ['ROLE_HOST'] }},
  { path: 'my-accommodations', component: AccommodationsComponent, canActivate: [AuthGuard], data: { role: ['ROLE_HOST'] }},
  { path: 'favourites', component: FavouritesComponent, canActivate: [AuthGuard], data: { role: ['ROLE_GUEST'] }},


  { path: 'reservation-requests', component: ReservationRequestsComponent, canActivate: [AuthGuard], data: { role: ['ROLE_HOST'] }},
  { path: 'create-reservation/:id', component: CreateReservationComponent, canActivate: [AuthGuard], data: { role: ['ROLE_GUEST'] }},
  { path: 'reservation-details/:id', component: ReservationDetailsComponent, canActivate: [AuthGuard], data: { role: ['ROLE_GUEST', 'ROLE_HOST'] }},
  { path: 'my-reservations', component: ReservationsComponent, canActivate: [AuthGuard], data: { role: ['ROLE_GUEST'] }},

  { path: 'manage-profile', component: ManageProfileComponent, canActivate: [AuthGuard], data: { role: ['ROLE_ADMIN', 'ROLE_HOST', 'ROLE_GUEST'] }},
  { path: 'user-reports', component: UserReportsComponent, canActivate: [AuthGuard], data: { role: ['ROLE_ADMIN'] } },
  { path: 'host-reports', component: HostReportsComponent, canActivate: [AuthGuard], data: { role: ['ROLE_GUEST'] } },
  { path: 'block-users', component: BlockUsersComponent, canActivate: [AuthGuard], data: { role: ['ROLE_HOST'] } },

  { path: 'accommodation-reviews/:id', component: AccommodationReviewsComponent},
  { path: 'accommodation-host-reviews/:id', component: AccommodationHostReviewComponent},
  { path: 'add-review/:id', component: AddReviewComponent, canActivate: [AuthGuard], data: { role: ['ROLE_GUEST'] } },
  { path: 'guest-reviews', component: GuestReviewsComponent, canActivate: [AuthGuard], data: { role: ['ROLE_GUEST'] } },
  { path: 'host-reviews', component: HostReviewsComponent, canActivate: [AuthGuard], data: { role: ['ROLE_HOST'] } },
  { path: 'review-reports', component: ReviewReportsComponent, canActivate: [AuthGuard], data: { role: ['ROLE_ADMIN'] } },

  { path: 'analytics', component: AnalyticsComponent, canActivate: [AuthGuard], data: { role: ['ROLE_HOST'] }},

  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegistrationComponent },
  { path: 'check-inbox', component: CheckInboxComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
