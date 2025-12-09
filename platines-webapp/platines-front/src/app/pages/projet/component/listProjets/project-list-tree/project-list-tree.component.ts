///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Observable} from "rxjs";
import {Version} from "../../../../entity/version";
import {Theme} from "../../../../entity/theme";
import {ProjectListTreeNode} from "./project-list-tree.model";
import {System} from "../../../../entity/system";
import {ProjectTreeNode} from "../../projetDetail/project-tree/project-tree.model";

@Component({
  selector: 'app-project-list-tree',
  templateUrl: './project-list-tree.component.html',
  styleUrls: ['./project-list-tree.component.scss']
})
export class ProjectListTreeComponent {
  @Output() nodeClick = new EventEmitter<{ node: ProjectTreeNode; event: MouseEvent; }>();
  @Input() projectObs: Observable<any>;
  @Input() activeNode: any = undefined;
  @Input() activeNodes: any[] = [];
  scrolledTo: number;
  @Output() scrolledToOut = new EventEmitter<number>();
  @Input() scrollTo: number;
  @Input() withHeader: any = false;
  @Input() treeTitle: any = null;
  @Input() frameHeight: string = "calc(var(--vh,1vh) * 100 - (var(--header-min-height,10.6rem)) - 13.5rem)";
  @Input() innerHeight: string = "auto";
  @Input() allowMultipleSelection: boolean = false;

  loadTree(res: Theme[]): ProjectListTreeNode[] {
    let projectListTree: ProjectListTreeNode[] = [];
    res.sort((a, b) => a.name.localeCompare(b.name));
    res.forEach(
      (family: Theme) => {
        const familyNode = new ProjectListTreeNode(
          {
            name: family.name,
            id: family.id,
            description: "Thème : " + family.name
          },
          []
        );
        family.children.sort((a, b) => a.name.localeCompare(b.name));
        family.children.forEach(
          (system: System) => {
            const systemNode = new ProjectListTreeNode(
              {
                name: system.name,
                id: system.id,
                description: "Web Service : " + family.name
              },
              []
            );
            system.children.sort((a, b) => a.label.localeCompare(b.label));
            system.children.forEach(
              (version: Version) => {
                const versionNode = new ProjectListTreeNode(
                  {
                    name: version.label,
                    id: version.id,
                    description: "Version : " + version.label
                  },
                  null
                );
                systemNode.children.push(versionNode);
              });
            familyNode.children.push(systemNode);
          });
        projectListTree.push(familyNode);
      });
    return projectListTree;
  }

  ngOnDestroy() {
    this.scrolledToOut.emit(this.scrolledTo);
  }

  compare(a, b) {
    if (a.name < b.name) {
      return -1;
    }
    if (a.name > b.name) {
      return 1;
    }
    return 0;
  }

  onNodeClick({node, event}) {
    this.nodeClick.emit({node, event});
  }
}
