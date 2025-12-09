///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component} from '@angular/core';

import {Router} from '@angular/router';

@Component({
  selector: 'resources',
  template: `<router-outlet></router-outlet>`,
})
export class ResourcesComponent {


  constructor(private router: Router) {
    }

}
