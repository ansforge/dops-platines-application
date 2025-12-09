///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {ChainOfTrust} from './chainoftrust';

export class Certificate {
    file: Blob;
    fileName: String;
    id: number;
    pem: string;
    chainOfTrust: ChainOfTrust;
}
