///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import { Theme } from "./theme";
import { CLIENT, SERVER } from "./role";

export class FamiliesRoles {
    family: Theme;
    coveredRoles: string[];
}

export function coversAppRole(cov: FamiliesRoles,role: string): boolean {
    switch(role){
        case CLIENT:
            return cov.coveredRoles.includes(SERVER);
        case SERVER:
            return cov.coveredRoles.includes(CLIENT);
        default:
            console.error("Rôle d'application inconnu: "+role);
            return false;
    }
}