///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

export class UpdateStatus {
    get OK(): string{return 'OK'}
    get WARNING(): string{return 'WARNING'}
    get REJECTED(): string{return 'REJECTED'}
}
export class BulkReportLine{
        filename: string;
        previousName: string;
        newName: string;
        lastUpdateDate: Date;
        status: string;
        statusMessage: string;
}

export class BulkUpdateReport{
    themeId: number;
    archiveId: string;
    reportLines: BulkReportLine[];
}
