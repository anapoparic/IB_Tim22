// review-report.model.ts

import {Review} from "./review.model";  // Prilagodite putanju prema stvarnom modelu vaših recenzija

export interface ReviewReport {
    id?: number;
    reason?: string;
    reportedReview?: Review;
    status?: boolean;
}
