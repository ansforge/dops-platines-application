///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {FunctionalData} from '../entity/functionaldata';
import {HomeService} from '../../services/home.service';
import {ProfileService} from '../../services/profile.service';
import jwt_decode from 'jwt-decode';
import DecoupledEditor from "@ckeditor/ckeditor5-build-decoupled-document";
import { CURRENT_USER_KEY } from '../../services/authentication.service';

@Component({
  selector: 'accueil',
  templateUrl: './accueil.html',
  styleUrls: ['./accueil.scss'],
})

export class AccueilComponent {

  user = jwt_decode(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService
  isAdmin: boolean;
  functionalData: FunctionalData = new FunctionalData();
  editor = DecoupledEditor;

  public onReady(editor: any){
    const decoupledEditor = editor as DecoupledEditor;
    const element = decoupledEditor.ui.getEditableElement()!;
    const parent = element.parentElement!

    parent.insertBefore(
      decoupledEditor.ui.view.toolbar.element!,
      element
    )
  }

  editable: Boolean = false;
  ngOnInit() {
    if(this.profileService.getProfileFromToken() === "admin") {
      this.isAdmin = true;
    } else {
      this.isAdmin = false;
    }
    this.homeService.getHomePageContent().subscribe(
      resp => {
        if(resp.content !== null) {
          this.functionalData.content = resp.content.toString();
        } else {
          this.functionalData.content = "";
        }

      }
    );
  }
  constructor(private homeService: HomeService, private router: Router, private profileService: ProfileService) {

  }

  edit() {
    this.editable = true;
  }

  cancel() {
    this.editable = false;
    this.homeService.getHomePageContent().subscribe(
      resp => {
        this.functionalData.content = resp.content.toString();
      }
    );
  }

  updateHomePage() {
    this.homeService.updateHomePageContent(this.functionalData.content).subscribe(
      resp => {
        this.functionalData.content = resp.content;
        this.editable = false;
      }
    );
  }

}
