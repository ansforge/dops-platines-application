///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Observable } from 'rxjs';
import { SessionDetail } from '../../../../entity/sessiondetail';
import { SessionResultTreeNode } from '../session-tree.model';
import { ProjectResultDetail } from '../../../../entity/projectresult.detail';
import { TestSuite } from '../../../../entity/testsuite';
import { TestCase } from '../../../../entity/testcase';
import { TestStep } from '../../../../entity/teststep';
import {ROperationDto} from "../../../../entity/rOperationDto";

@Component({
  selector: 'app-session-result-tree',
  templateUrl: './session-result-tree.component.html',
  styleUrls: ['./session-result-tree.component.scss']
})
export class SessionResultTreeComponent {
  @Input() projectObs: Observable<any>;
  @Input() withHeader: boolean=true;
  @Input() border = "1px solid var(--gray-400)";
  @Input() frameHeight: string = "60rem";
  @Output() nodeSelection: EventEmitter<SessionResultTreeNode>=new EventEmitter();
  @Input() activeNodes: any[] = [];

  loadTree(sessionDetail: SessionDetail): SessionResultTreeNode[] {
    const nodes: SessionResultTreeNode[]=[];
    sessionDetail.projectResults.forEach(
      (resultatProjet: ProjectResultDetail) => {
        const projet = new SessionResultTreeNode(
          resultatProjet,
          []
        )
        if (resultatProjet.resultStatus !== ('NONEXECUTE' && null)) {
          nodes.push(projet);
        }

        resultatProjet.testSuites.forEach(
          (resultatSuite: TestSuite) => {
            const suite = new SessionResultTreeNode(
              resultatSuite,
              []
            )
            projet.children.push(suite);

            resultatSuite.testCases.forEach(
              (resultatCas: TestCase) => {
                const cas = new SessionResultTreeNode(
                  resultatCas,
                  []
                );

                suite.children.push(cas);

                resultatCas.testSteps.forEach(
                  (resultatPas: TestStep) => {
                    const pas = new SessionResultTreeNode(
                      resultatPas,
                      []
                    );

                    cas.children.push(pas)
                  }
                )
                if(resultatCas.rOperationDto){
                  resultatCas.rOperationDto.forEach(
                    (resultatOperation: ROperationDto) => {
                      console.log(resultatOperation)
                      const date = new Date(resultatOperation.operationDate);
                      const dateUTC = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(),
                        date.getHours(), date.getMinutes(), date.getSeconds());
                      const rop = {
                        id: resultatOperation.id,
                        name: new Date(dateUTC).toLocaleString(),
                        description: "",
                        resultStatus: "SUCCESS",
                        isOperation: true
                      }
                      const operation = new SessionResultTreeNode(
                        rop,
                        []
                      );
                      cas.children.push(operation)
                    }
                  );
                }
              }
            );
          }
        );
      }
    );
    return nodes;
  }

  onNodeSelection($event:{ node: SessionResultTreeNode; event: MouseEvent;}){
    this.nodeSelection.emit($event.node);
  }

  isFailure(node: SessionResultTreeNode){
    return !['SUCCESS','NONEXECUTE'].includes(node.payload.resultStatus);
  }
}
