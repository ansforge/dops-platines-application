///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {AfterViewInit, Component} from '@angular/core';
import {Router} from '@angular/router';
import {ProfileService} from '../../services/profile.service'
import {PreferencesService} from '../../services/preferences.service';
import {EditService} from '../../services/edit.service';
import {StateService} from '../../services/state.service';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {MatTableDataSource} from "@angular/material/table";
import {Sort} from "@angular/material/sort";
import {ContextStateService} from "../../services/context-state.service";

export interface Preference {
  key: string;
  value: string;
  isBeingEdited: boolean;
}

@Component({
  selector: 'preferences',
  templateUrl: './preferences.html',
  styleUrls: ['./preferences.scss'],
  providers: [EditService, StateService]
})
export class PreferencesComponent implements AfterViewInit {
  modalRef: NgbModalRef;
  dataSource = new MatTableDataSource();
  autoColumns: string[] = [];
  allColumns: string[] = [...this.autoColumns, 'key', 'value', 'actions'];
  properties = [
    {
      key: "textHomePage",
      description: `Texte de la page d'accueil, attendu au format html`
    },
    {
      key: "token_duration",
      description: "Durée de validitée du token. La valeur est attendue en minutes (valeur numérique)"
    },
    {
      key: "blocked_account_duration",
      description: `Temps de blocage d'un compte. La valeur est attendue en minutes (valeur numérique)`
    },
    {
      key: "limit_try_authentication",
      description: "Limite d'essais infructueux avant de bloquer le compte. Valeur numérique attendue"
    }, {
      key: "provisionning_timeout",
      description: "Temps d'attente pour le provisionnement d'un conteneur docker lorsque les ressources sont insuffisantes. La valeur est attendue en minutes (valeur numérique)"
    }, {
      key: "list_email_notification",
      description: "Liste des mails des personnes à prévenir lors d'un échec de création de session. Les différents mails doivent être séparés par un point-virgule."
    }
  ];
  labelsPath: string = "pages.preferences.labels";
  tooltipPath: string = "pages.preferences.tooltips";

  constructor(private router: Router, private profileService: ProfileService, private editService: EditService,
              private modalService: NgbModal, private preferenceService: PreferencesService, private contextStateService: ContextStateService) {
  }

  ngAfterViewInit() {
    this.getPreferences();
  }

  getPreferences() {
    this.preferenceService.getPreferences().subscribe(
      res => {
        Object.entries(res).forEach(element => {
          this.dataSource.data.push({key: element[0], value: element[1]})
          this.dataSource.data = this.dataSource.data;
          setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
        });
        //Attaching descriptions to each preference
        this.dataSource.data.forEach((element: any) => {
          if (element.description != undefined)
            element.description = this.properties.find(e => e.key == element.key.toString()).description;
        })
      },
      err => {
        console.log(err);
      }
    )
  }

  ngOnInit() {
    this.dataSource.sortingDataAccessor = (data: any, sortHeaderId: string): string => {
      if (typeof data[sortHeaderId] === 'string') {
        return data[sortHeaderId].toLocaleLowerCase();
      }
      return data[sortHeaderId];
    };
  }

  toggleEditing(row) {
    row.isBeingEdited = !row.isBeingEdited;
    if (row.isBeingEdited) {
      this.validateValueInput(row);
    } else {
      let preferenceMap = {}
      preferenceMap[row.key] = row.value;
      this.preferenceService.updatePreferences(preferenceMap).subscribe();
    }
    //TODO: Add backend call to update the preference
  }

  validateValueInput(element) {
    switch (element.key) {
      case "blocked_account_duration":
        element.valid = /^\d+$/.test(element.value);
        break;
      case "token_duration":
        element.valid = /^\d+$/.test(element.value);
        break;
      case "limit_try_authentication":
        element.valid = /^\d+$/.test(element.value);
        break;
      case "textHomePage":
        element.valid = !(!element.value || /^\s+$/.test(element.value));
        break;
      case "provisionning_timeout":
        element.valid = /^\d+$/.test(element.value);
        break;
      case "list_email_notification":
        element.valid = /^([a-zA-Z0-9.!#$%&’*+\/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*;?\s*)+$/.test(element.value);
        break;
    }
  }

  expandValue(element) {
    element.expanded = !element.expanded;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  sortData(sort: Sort) {
    const sortedData = this.dataSource.data.slice();
    sortedData.sort(
      (a, b) => sort.direction === 'asc' ?
        this.sortAlphanumeric(a[sort.active], b[sort.active], true) :
        this.sortAlphanumeric(a[sort.active], b[sort.active], false));
    this.dataSource.data = sortedData;
  }

  sortAlphanumeric(a, b, isAsc: boolean): number {
    if (isAsc) {
      if (!a || !b) {
        return !a ? -1 : 1;
      } else {
        return a.localeCompare(b, 'en', {numeric: true});
      }
    } else {
      if (!a || !b) {
        return !a ? 1 : -1;
      } else {
        return b.localeCompare(a, 'en', {numeric: true});
      }
    }
  }
}


