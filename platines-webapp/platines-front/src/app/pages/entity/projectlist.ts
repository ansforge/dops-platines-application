///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {TestCertificate} from './testcertificate';
import {Version} from './version';

export class ProjectList {
    id: Number = null;
    name: String = null;
    description: String = null;
    fileName: String = null;
    visibility: boolean = false;
    role: String = null;
    testCertificate: TestCertificate = new TestCertificate();
    versions: Version[] = new Array<Version>();
}
