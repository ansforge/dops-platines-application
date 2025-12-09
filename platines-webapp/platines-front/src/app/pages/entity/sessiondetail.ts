///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {ProjectResultDetail} from "./projectresult.detail";
import {SessionDuration} from "./session.duration";

export class SessionDetail {

    id: Number;
    executionDuration: Number;
    description: string;
    sessionStatus: string;
    projectResults: ProjectResultDetail[] = new Array<ProjectResultDetail>();
    executionDate: Date;
    uuid: string;
    sessionDuration: SessionDuration = new SessionDuration();
    url: string;
    resourcePath: Array<string>;
    creationDate: Date;
}
