///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from '@angular/core';

@Injectable()
export class FilterListService {
    constructor() {
    }

    filterListForDelete(checkList: any[], visibleList: any[]): any[] {

        const finalList: any[]= [];
        visibleList.forEach(element => {
            if(checkList.indexOf(element) != -1) {
                finalList.push(element);
            }

        });


        return finalList;
    }

    filterListForAssign(checkList: any[], visibleList: any[]): any[] {

        const finalList: any[]= [];
        visibleList.forEach(element => {
            if(checkList.indexOf(element) != -1) {
                finalList.push(element);
            }

        });


        return finalList;
    }
}
