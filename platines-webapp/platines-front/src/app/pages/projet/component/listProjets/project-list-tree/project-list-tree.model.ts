///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import { TreeNode } from '../../../../../shared/app-tree/treeNode.model';

interface ProjectListElement {
  name: string,
  id: number,
  description: string
}
export class ProjectListTreeNode extends TreeNode<ProjectListElement>{}
