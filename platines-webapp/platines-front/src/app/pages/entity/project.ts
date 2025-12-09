///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Version} from './version';
import {RelatedFiles} from './relatedfiles';
import {TestCertificate} from './testcertificate';
import {Property} from './property';

export class Project {
    id:Number;
    name:String;
    description:String;
    fileName:String;
    file: Blob;
    visibility: boolean;
    role:String;
    versions: Version[] = [];
    relatedFiles: RelatedFiles[]= [];
    testCertificate: TestCertificate = new TestCertificate();
    properties: Property[] = [];
}

