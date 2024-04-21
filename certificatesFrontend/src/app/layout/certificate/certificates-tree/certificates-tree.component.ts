import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { Certificate } from '../model/certificate.model';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-certificates-tree',
  templateUrl: './certificates-tree.component.html',
  styleUrls: ['./certificates-tree.component.css']
})
export class CertificatesTreeComponent implements OnInit{

  aliasList: string[] = [];

  constructor(
    public dialogRef: MatDialogRef<CertificatesTreeComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Observable<Certificate[]>,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.data.subscribe(
      certificates => {
        this.extractAliases(certificates);
      },
      error => {
        console.error('Došlo je do greške prilikom dobijanja podataka:', error);
      }
    );
  }

  private extractAliases(certificates: Certificate[]): void {
    this.aliasList = certificates.map(cert => cert.alias);
  }
  closeDialog(): void {
    this.dialogRef.close();
  }
}
