///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import { TestCaseDetail } from "./testcaseDetail";

export class TestSuiteDetail {
    id: number;
    name: string;
    description: string;
    resultStatus: string;
    listTestCase: TestCaseDetail[] = new Array<TestCaseDetail>();
}
