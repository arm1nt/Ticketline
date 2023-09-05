import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SectorCreate } from 'src/app/dtos/sector';
import { StandingData } from '../../../create-floorplan.component';

@Component({
  selector: 'app-standing-sector-dialog',
  templateUrl: './standing-sector-dialog.component.html',
  styleUrls: ['./standing-sector-dialog.component.scss']
})
export class StandingSectorDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<StandingSectorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: StandingData) { }

  ngOnInit(): void {
  }

  onCancelClick(): void {
    this.dialogRef.close();
  }

  onSaveClick(): void {

    this.dialogRef.close({
      changes: true,
      delete: false,
      capacity: this.data.capacity,
      price: this.data.price,
      sectorId: this.data.id
    });
  }

  onDeleteClick(): void {
    this.dialogRef.close({
      changes: false,
      delete: true,
      capacity: this.data.capacity,
      price: this.data.capacity,
      sectorId: this.data.id
    });
  }

}
