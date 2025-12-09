///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {CertificateList} from './certificatelist'
import {User} from "./user";

export class ChainOfTrust {
    id: number;
    name: string;
    description: string;
    certificate: CertificateList[];
    user: User;
}
