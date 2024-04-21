import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-certificates-tree',
  templateUrl: './certificates-tree.component.html',
  styleUrls: ['./certificates-tree.component.css']
})
export class CertificatesTreeComponent {
  constructor(public dialogRef: MatDialogRef<CertificatesTreeComponent>, @Inject(MAT_DIALOG_DATA) public data: any) {}

  closeDialog(): void {
    this.dialogRef.close();
  }
}
