///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'certificats',
  template: `<router-outlet></router-outlet>`,
})
export class CertificatsComponent {


  constructor(private router: Router) {
    }

}
