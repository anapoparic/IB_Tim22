import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
  Router,
} from '@angular/router';
import { Observable } from 'rxjs';
import { KeycloakService } from 'src/app/keycloak/keycloak.service';


@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(
      private router: Router,
      private keycloakService: KeycloakService
  ) {}

  canActivate(
      route: ActivatedRouteSnapshot,
      state: RouterStateSnapshot
  ):
      | Observable<boolean | UrlTree>
      | Promise<boolean | UrlTree>
      | boolean
      | UrlTree {
    if (this.keycloakService.keycloak.isTokenExpired()) {
      this.router.navigate(['login']);
      return false;
    }

    const tokenParsed = this.keycloakService.keycloak.tokenParsed;
    console.log('Parsed Token:', tokenParsed); // Log the parsed token to inspect it

    // Pristupamo ulogama korisnika pod "backend" resursom
    const userRoles: string[] = tokenParsed?.resource_access?.['backend']?.roles || [];
    console.log('User Roles:', userRoles); // Log the roles to inspect them

    if (!userRoles || userRoles.length === 0) {
      this.router.navigate(['login']);
      return false;
    }

    // const requiredRoles = route.data['role'] as string[];
    // console.log("REQUIREDROLEEEEEEEES"  + requiredRoles)
    // const hasRequiredRole = userRoles.some(role => requiredRoles.includes(role));
    // console.log("ROLEEEEEEEE"  + hasRequiredRole)

    if (userRoles[0] != "ADMIN" && userRoles[0] != "GUEST" && userRoles[0] != "HOST") {
      this.router.navigate(['']);
      return false;
    }

    return true;
  }
}
