///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Version} from "./version";
import {Property} from "./property";

export class ProjectSession {
    id: Number;
    name: string;
    description: string;
    visibility: boolean;
    role: string;
    properties: Property[] = new Array<Property>();
    versions: Version[] = new Array<Version>();
}
