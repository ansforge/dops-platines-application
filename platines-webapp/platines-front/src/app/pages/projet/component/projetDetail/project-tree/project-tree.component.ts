///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import { ProjectDetails } from "../../../../entity/projectDetail";
import { ProjectTreeNode } from "./project-tree.model";
import { TestSuiteDetail } from "../../../../entity/testSuiteDetail";
import { TestCaseDetail } from "../../../../entity/testcaseDetail";
import { Component, EventEmitter, Input, Output } from "@angular/core";
import { Observable } from "rxjs";

@Component(
    {
        selector: 'app-project-tree',
        templateUrl: './project-tree.component.html'
    }
)
export class ProjectTreeComponent {
    @Output() nodeClick=new EventEmitter<ProjectTreeNode>();
    @Input() projectObs: Observable<any>;
    @Input() activeNodes: any[] = [];

    loadTree(res: ProjectDetails): ProjectTreeNode[] {


        const project = new ProjectTreeNode(
            res,
            []
          );

          res.testSuiteDetail.forEach(
            (suiteDetail: TestSuiteDetail) => {
                const suite = new ProjectTreeNode(
                  suiteDetail,
                  []
                );

            suiteDetail.listTestCase.forEach(
              (caseDetail: TestCaseDetail) => {
              const cas = new ProjectTreeNode(
                {
                  name: caseDetail.name,
                  description: caseDetail.description,
                },
                []
              );

              caseDetail.listTestSteps.forEach(stepDetail => {
                const step = new ProjectTreeNode(
                  {
                    name: stepDetail.name,
                    description: stepDetail.description,
                  },
                  null
                );
                cas.children.push(step);
              })
              suite.children.push(cas);
            });
            project.children.push(suite);
          })
        return [project];
    }

    onNodeClick($event:{ node: ProjectTreeNode; event: MouseEvent;}){
        this.nodeClick.emit($event.node);
    }
}
