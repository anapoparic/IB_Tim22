import { Injectable } from '@angular/core';
import * as DOMPurify from 'dompurify';

@Injectable({
  providedIn: 'root'
})
export class SanitizationService {

  constructor() { }

  sanitize(input?: string): string | undefined {
    if (input) {
        return DOMPurify.sanitize(input);
    }
    return undefined;
}

}
