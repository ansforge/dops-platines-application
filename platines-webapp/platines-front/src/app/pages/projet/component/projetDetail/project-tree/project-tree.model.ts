///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import { TreeNode } from '../../../../../shared/app-tree/treeNode.model'; 

interface ProjectElement {
  name: String;
  description: String;
}
export class ProjectTreeNode extends TreeNode<ProjectElement>{}