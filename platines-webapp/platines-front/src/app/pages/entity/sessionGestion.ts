///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Application} from "./application";

export class SessionGestion {
    id: number = null;
	executionDate: Date = null;
	application: Application = new Application();
	sessionStatus: String = null;
	description: String = null;
	creationDate: Date = null;
}
