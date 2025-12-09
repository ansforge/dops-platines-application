///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {TestCase} from "./testcase";

export class TestSuite {
    id: number;
    name: string;
    description: string;
    resultStatus: string;
    testCases: TestCase[] = new Array<TestCase>();
}
