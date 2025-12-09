///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Pipe, PipeTransform} from '@angular/core';

import {DomSanitizer} from '@angular/platform-browser';

@Pipe({ name: 'safeHtml'})
export class SafeHtmlPipe implements PipeTransform  {
  constructor(private sanitized: DomSanitizer) {}
  transform(value) {
    return this.sanitized.bypassSecurityTrustHtml(value);
  }
}
