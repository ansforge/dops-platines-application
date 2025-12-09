///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import { Property } from "../../../entity/property";
import { TreeNode } from "../../../../shared/app-tree/treeNode.model";

interface SessionResultElement {
    name: string,
    description:  string,
    resultStatus: string,
    projectProperties?: Property[]
}

export class SessionResultTreeNode extends TreeNode<SessionResultElement> {

}