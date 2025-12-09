///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, ElementRef, HostListener, Renderer2, ViewChild, ViewChildren} from '@angular/core';

import {ProfileService} from '../services/profile.service';
import {MENU_ADMIN, MENU_ITEMS, MENU_MANAGER} from "./pages-menu";
import {environment} from "../../environments/environment";
import {AuthRor} from "../services/auth.ror";
import {AuthenticationService} from "../services/authentication.service";
import {forEach} from "lodash";

@Component({
  selector: 'ngx-pages',
  templateUrl: './pages.component.html',
  styleUrls: ['pages.component.scss'],
})
export class PagesComponent {
  @ViewChild('userMenuModal')
  userMenuModalChild;
  @ViewChild('userMenuModalButton')
  userMenuModalButton;
  @ViewChildren('menuTrigger')
  menuTriggers;
  protected menu;
  protected readonly environment = environment;
  protected readonly AuthRor = AuthRor;

  constructor(
    protected renderer: Renderer2,
    protected profileService: ProfileService,
    protected authenticationService: AuthenticationService) {
    if (profileService.getProfileFromToken() === "admin") {
      this.menu = MENU_ADMIN;
    } else if (profileService.getProfileFromToken() === "manager") {
      this.menu = MENU_MANAGER;
    } else {
      this.menu = MENU_ITEMS;
    }
  }

  @HostListener('document:click', ['$event'])
  clickOutOfDropdownMenu(event) {
    if (event.target.classList.contains('mat-mdc-menu-trigger') || event.target.classList.contains('user-menu-button')) {
      for (let i = 0; i < this.menuTriggers.length; i++) {
        if (i != event.target.attributes['aria-controls']?.nodeValue?.slice(-1)) {
          this.menuTriggers._results[i].closeMenu();
        }
      }
      if (!event.target.classList.contains('user-menu-button')) {
        this.closeUserModal();
      } else {
        event.stopPropagation();
      }
    } else {
      this.menuTriggers._results.forEach((menuTrigger) => {
        menuTrigger.closeMenu();
      });
      this.closeUserModal();
    }
  }

  closeUserModal() {
    if(this.userMenuModalChild.nativeElement.classList.contains('show')) {
      this.userMenuModalChild.nativeElement.classList.remove('show');
    }
  }

  toggleShow(event){
    if(this.userMenuModalChild.nativeElement.classList.contains('show')) {
      this.userMenuModalChild.nativeElement.classList.remove('show');
    } else {
      this.userMenuModalChild.nativeElement.classList.add('show');
    }
  }
}
