///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {User} from './user';
import {ChainOfTrustList} from './chainoftrustlist';

export class Application {
    id: Number;
    name: string;
    version: string;
    description: string;
    url: string;
    role: string;
    user: User = new User();
    chainOfTrustDto: ChainOfTrustList = new ChainOfTrustList();
}
