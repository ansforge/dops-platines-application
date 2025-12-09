///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Profile} from './profile';
import {Theme} from './theme';

export class User {
    id: Number;
    mail: string;
    name: string;
    forename: string;
    firm: string;
    creationDate: Date;
    profile: Profile;
    families: Array<Theme>
}
