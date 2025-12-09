///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import { TestStep } from "./teststep";

export class TestCaseDetail {
    id: number;
    name: string;
    description: string;
    criticality: string;
    resultStatus: string;
    listTestSteps: TestStep[] = new Array<TestStep>();
}