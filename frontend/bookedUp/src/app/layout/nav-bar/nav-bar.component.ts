import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { KeycloakService } from 'src/app/keycloak/keycloak.service';
import { Role } from 'src/app/user/model/role.enum';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent implements OnInit{
  role: string = '' ;
  constructor(private authService: AuthService, private keycloakService: KeycloakService) {
  }

  ngOnInit(): void {
    
    if (!this.keycloakService.keycloak.isTokenExpired()){

      const tokenParsed = this.keycloakService.keycloak.tokenParsed;
      console.log('Parsed Token:', tokenParsed); // Log the parsed token to inspect it

      // Pristupamo ulogama korisnika pod "backend" resursom
      const userRoles: string[] = tokenParsed?.resource_access?.['backend']?.roles || [];
      console.log('User Roles:', userRoles);

      if (userRoles[0] == "ADMIN"){
        this.role = "ROLE_ADMIN";
      } 

      if (userRoles[0] == "GUEST"){
        this.role = "ROLE_GUEST";
      }

      if (userRoles[0] == "HOST"){
        this.role = "ROLE_HOST";
      }

      if (userRoles[0] == "SUPER_ADMIN"){
        this.role = "ROLE_SUPER_ADMIN";
      }

      

      console.log(this.role)
    
      
    }
    // this.authService.userState.subscribe((result) => {
    //   this.role = result;
    // })
  }
}
