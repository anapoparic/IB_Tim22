import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-star-rating',
  templateUrl: './star-rating.component.html',
  styleUrls: ['./star-rating.component.css']
})
export class StarRatingComponent {
  @Input() maxStars: number = 5;
  @Output() ratingClicked = new EventEmitter<number>();

  stars: boolean[] = [];

  constructor() {
    this.stars = Array.from({ length: this.maxStars }, () => false);
  }

  rate(starIndex: number): void {
    this.stars = this.stars.map((_, index) => index <= starIndex);
    const selectedStars = this.stars.filter(star => star).length;
    this.ratingClicked.emit(selectedStars);
  }
}
