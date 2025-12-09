///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Theme} from "./theme";
import {Version} from "./version";

export class System {
    id: number = null;
    name: string = null;
    theme: Theme = new Theme();
    children: Version[] = null;
}
