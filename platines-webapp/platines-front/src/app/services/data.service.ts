///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from '@angular/core';
import jwt_decode from "jwt-decode";
import { CURRENT_USER_KEY } from './authentication.service';

//FIXME : ce service ne fait RIEN => sérieusement songer à le supprimer.
@Injectable()
export class DataService {


  private json = jwt_decode(localStorage.getItem(CURRENT_USER_KEY));
  private messageSource = "";

  constructor() { }

  changeMessage(message: string) {
  }

}
