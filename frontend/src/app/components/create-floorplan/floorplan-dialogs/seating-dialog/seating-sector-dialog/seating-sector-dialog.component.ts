import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SeatingData } from '../../../create-floorplan.component';

@Component({
  selector: 'app-seating-sector-dialog',
  templateUrl: './seating-sector-dialog.component.html',
  styleUrls: ['./seating-sector-dialog.component.scss']
})
export class SeatingSectorDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<SeatingSectorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SeatingData

  ) { }

  ngOnInit(): void {
  }

  onDeleteClick(): void {
    this.dialogRef.close({
      delete: true,
      price: undefined
    });
  }


  onSaveClick(): void {
    this.dialogRef.close({
      delete: false,
      price: this.data.price
    });

  }

  onCancelClick(): void {
    this.dialogRef.close();
  }

}
