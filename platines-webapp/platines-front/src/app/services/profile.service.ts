///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from "@angular/core";
import jwt_decode from "jwt-decode";
import { AuthenticationService, Profile } from "./authentication.service";

@Injectable()
export class ProfileService {
  
    constructor(private authenticationService: AuthenticationService){}

    getJwtToken(): any {
        return this.authenticationService.getConnectedUserProfile().jwtClaims;
    }

    getProfileFromToken(): String {
      return this.authenticationService.getConnectedUserProfile()?.auth[0]?.authority;
    }

    getUsernameFromLocalStorage(): String {
      return localStorage.getItem("username");
    }

    setUsername(username: String) {
      localStorage.setItem("username", username.toString());
    }

    getIssFromToken(): String {
      return this.authenticationService.getConnectedUserProfile()?.iss;
    }

    getValidationDated(): any {
      const profile: Profile = this.authenticationService.getConnectedUserProfile();
      if(profile){
        return profile.exp
      }else{
        return 0;
      }
    }

    refreshToken(response: Response) {
      this.authenticationService.refresh(response.headers.get("authorization"));
    }
}
