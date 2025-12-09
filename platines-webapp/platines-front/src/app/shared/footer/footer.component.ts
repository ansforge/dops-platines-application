///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import { Component, OnInit } from '@angular/core';
import { PlatinesTechDataService } from '../../services/platines-techdata.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit{
  private _version: string;

  constructor(private techDataSrv: PlatinesTechDataService){}

  ngOnInit(): void {
    this.techDataSrv.platinesVersion().subscribe(
      (version: string) => this._version=version
    );
  }

  public get version(): string {
    return this._version;
  }
}
