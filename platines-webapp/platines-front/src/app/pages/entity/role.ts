///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

export const CLIENT: string='CLIENT';
export const SERVER: string='SERVER';

export function toTestWebserviceRole(applicationRole: string): string {
    switch(applicationRole){
        case CLIENT:
            return SERVER;
        case SERVER:
            return CLIENT;
        default:
            throw `Unsupported role ${applicationRole}`
    }
}