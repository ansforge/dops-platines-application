///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import { Project } from "./project";
import { TestSuiteDetail } from "./testSuiteDetail";

export class ProjectDetails extends Project{
    testSuiteDetail: TestSuiteDetail[];
}