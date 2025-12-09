///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Application} from "./application";
import {Version} from "./version";
import {SessionDuration} from "./session.duration";
import {ProjectResult} from "./project.result";

export class Session {

    id: Number = null;
    description: String = null;
    sessionType: String = null;
    application: Application = new Application();
    version: Version = new Version();
    sessionDuration: SessionDuration = new SessionDuration();
    projectResults: ProjectResult[] = new Array();
}
