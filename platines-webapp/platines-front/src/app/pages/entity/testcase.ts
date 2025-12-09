///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {TestStep} from "./teststep";
import {ROperationDto} from "./rOperationDto";

export class TestCase {
    id: number;
    name: string;
    description: string;
    criticality: string;
    resultStatus: string;
    testSteps: TestStep[] = new Array<TestStep>();
    rOperationDto: ROperationDto[] = new Array<ROperationDto>();
}
