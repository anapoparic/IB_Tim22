import {Injectable} from '@angular/core';
import Keycloak from 'keycloak-js';
import {UserProfile} from './user-profile';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private _keycloak: Keycloak | undefined;

  get keycloak() {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: 'https://localhost:8443',
        realm: 'booked-up',
        clientId: 'bsn'
      });
    }
    return this._keycloak;
  }

  private _profile: UserProfile | undefined;

  get profile(): UserProfile | undefined {
    return this._profile;
  }

  async init() {
    console.log("authentification");
    const authenticated = await this.keycloak.init({
      onLoad: 'login-required',
    });

    if (authenticated) {
      console.log("user authenticated");
      this._profile = (await this.keycloak.loadUserProfile()) as UserProfile;
      this._profile.token = this.keycloak.token || '';
    }
  }

  login() {
    return this.keycloak.login();
  }

  logout() {
    this.keycloak.accountManagement();
    return this.keycloak.logout({redirectUri: 'https://localhost:4200'});
  }
}
