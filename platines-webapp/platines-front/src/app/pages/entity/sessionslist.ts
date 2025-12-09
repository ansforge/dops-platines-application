///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Application} from "./application";

export class SessionList {
    id: number;
	creationDate: Date;
	application: Application = new Application();
	description: string;
	sessionStatus: string;
	simulatedRole: string;
}
