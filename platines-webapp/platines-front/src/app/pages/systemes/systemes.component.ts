///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component} from "@angular/core";
import {Router} from "@angular/router";

@Component({
    selector: 'systemes',
    template: `<router-outlet></router-outlet>`,
})
export class SystemesComponent {

    constructor(private router: Router){}
}
