import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { Interceptor } from "./infrastructure/auth/interceptor";
import { LayoutModule } from './layout/layout.module';
import { AuthModule } from "./infrastructure/auth/auth.module";
import { UserModule } from './user/user.module';
import { ReservationModule } from './reservation/reservation.module';
import { AccommodationModule } from './accommodation/accommodation.module';
import { ReviewModule } from './review/review.module';
import { AnalyticsModule } from './analytics/analytics.module';
import { CreateRequestComponent } from './certification/create-request/create-request.component';
import { KeycloakService } from './keycloak/keycloak.service';

export function kcFactory(kcService: KeycloakService) {
  return () => kcService.init();
}


@NgModule({
  declarations: [
    AppComponent,
    CreateRequestComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    LayoutModule,
    BrowserAnimationsModule,
    AuthModule,
    UserModule,
    ReservationModule,
    AccommodationModule,
    ReviewModule,
    AnalyticsModule,
    HttpClientModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: Interceptor,
      multi: true,
    },
    {
      provide: APP_INITIALIZER,
      deps: [KeycloakService],
      useFactory: kcFactory,
      multi: true
    }
  ],
  bootstrap: [AppComponent]

})
export class AppModule { }



