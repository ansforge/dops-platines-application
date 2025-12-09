///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from "@angular/core";

@Injectable()
export class DateConverterService {

    constructor(){
    }

    convertdate(date): string {
        let dateUTC = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
        return new Date(dateUTC).toLocaleString();
    }
}
