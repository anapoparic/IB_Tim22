// image-frame.component.ts
import { Component, ElementRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-image-frame',
  templateUrl: './image-frame.component.html',
  styleUrls: ['./image-frame.component.css']
})
export class ImageFrameComponent {
  @ViewChild('fileInput') fileInput!: ElementRef;

  selectedImageSrc: string | ArrayBuffer | null = null;

  openFileExplorer() {
    this.fileInput.nativeElement.click();
  }

  displayImage(event: any) {
    const input = event.target;
    const file = input.files?.[0];

    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.selectedImageSrc = e.target?.result as string;
      };
      reader.readAsDataURL(file);
    }
  }
}
