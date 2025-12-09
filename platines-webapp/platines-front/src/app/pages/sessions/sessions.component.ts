///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component} from '@angular/core';

import {SessionService} from '../../services/session.service';
import {Router} from '@angular/router';

@Component({
  selector: 'sessions',
  template: `<router-outlet></router-outlet>`,
})
export class SessionsComponent {


  constructor(private sessionService: SessionService, private router: Router) {
    }

}
