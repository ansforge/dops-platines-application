///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {TestSuite} from "./testsuite";
import {Property} from "./property";

export class ProjectResultDetail {
    id: number;
    name: string;
    description: string;
    resultStatus: string;
    testSuites: TestSuite[] = new Array<TestSuite>();
    projectProperties: Property[] = new Array<Property>();
    idProject: number;
}
