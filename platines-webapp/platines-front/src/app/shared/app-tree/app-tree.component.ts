///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */
import {
  Component,
  ContentChild,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
  ViewChild
} from '@angular/core';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { NestedTreeControl } from '@angular/cdk/tree';
import { Observable } from 'rxjs';
import { TreeNode } from './treeNode.model';

@Component({
  selector: 'app-tree',
  templateUrl: './app-tree.component.html',
  styleUrls: ['./app-tree.component.scss']
})
export class AppTreeComponent<Payload,DataType> implements OnInit{
  treeDataSource: MatTreeNestedDataSource<TreeNode<Payload>> = new MatTreeNestedDataSource<TreeNode<Payload>>();
  treeControl: NestedTreeControl<TreeNode<Payload>> = new NestedTreeControl<TreeNode<Payload>>(
    (node: TreeNode<Payload>) => {
      if(node.hasChildren) {
        return node.children;
      }
    }
  );
  @Input() treeTitle: string='no.tree.title';
  @Input() innerHeight: string='400px';
  @Input() dataObs: Observable<DataType>;
  @Input() withHeader: boolean=true;
  @Input() border = "1px solid var(--gray-400)";
  @Input() frameHeight: string = "60rem";
  @Input() doNotInitialize: boolean = false;
  @Input() expandByDefault: boolean = true;
  @Output() nodeClick: EventEmitter<{ node: TreeNode<Payload>; event: MouseEvent; }> = new EventEmitter();
  @Input() loadTree: (data: DataType) => (TreeNode<Payload>[]);
  @ContentChild('nodeIcon',{static: false}) iconRef: TemplateRef<any>;
  @Input() protected activeNodes: any[] = [];
  protected expanded: boolean = false;
  @ViewChild("scrollableTreeContainer") scrollableTreeContainer;
  @Output() scrolledTo: EventEmitter<number> = new EventEmitter();
  @Input() scrollTo = 0;
  @Input() allowMultipleSelection: boolean = false;

  ngOnInit(): void {
    this.dataObs?.subscribe(
      (newData: any) => {
        const treeNodes = this.loadTree(newData);
        this.treeDataSource.data = treeNodes;
        this.treeControl.dataNodes = treeNodes;
        if(this.expandByDefault){
          this.toggleAll();
        }
        if(treeNodes.length>0 && !this.doNotInitialize){
          this.nodeClick.emit({node:treeNodes[0],event:null});
        }
        if(this.activeNodes.length>0){
          let flatNodes: TreeNode<Payload>[] = [];
          this.flattenTree(treeNodes,flatNodes);
          this.activeNodes = flatNodes.filter((node: TreeNode<Payload>) => this.activeNodes.find((activeNode: TreeNode<Payload>) => activeNode?.payload['id'] === node?.payload['id']));
        }
        setTimeout(()=>this.scrollableTreeContainer.nativeElement.scrollTop = this.scrollTo);
      }
    );
    this.treeDataSource.data=[];
  }

  ngOnDestroy(): void {
    this.scrolledTo.emit(this.scrollableTreeContainer.nativeElement.scrollTop);
  }

  flattenTree(treeNodes,flatNodes){
    treeNodes.forEach(
      (node: TreeNode<Payload>) => {
        flatNodes.push(node);
        if(node.hasChildren()){
          this.flattenTree(node.children,flatNodes);
        }
      }
    );
  }

  ngAfterViewInit(): void {
  }

  hidden(node: TreeNode<Payload>): boolean{
    if(this.treeControl.isExpanded(node) && node.parent){
      return this.hidden(node.parent);
    }else{
      return !this.treeControl.isExpanded(node);
    }
  }

  hasNestedChild(index: number, node: TreeNode<Payload>): boolean {
    return node.hasChildren();
  }

  onNodeSelect(node: TreeNode<Payload>, event: MouseEvent){
    this.nodeClick.emit({node,event});
  }

  toggleAll() {
    if(this.expanded){
      this.treeControl.collapseAll();
      this.expanded=false;
    }else{
      this.treeControl.expandAll();
      this.expanded=true;
    }
  }
}
