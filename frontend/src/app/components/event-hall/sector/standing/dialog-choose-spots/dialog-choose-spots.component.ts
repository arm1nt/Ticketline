import { Component, OnInit, Inject } from '@angular/core';
import {MatDialog, MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {DialogChooseSpotsData} from '../../../../../dtos/dialog-choose-spots-data';

@Component({
  selector: 'app-dialog-choose-spots',
  templateUrl: './dialog-choose-spots.component.html',
  styleUrls: ['./dialog-choose-spots.component.scss']
})
export class DialogChooseSpotsComponent implements OnInit {

  public oldSelectedSpots: number;

  constructor(
    public dialogRef: MatDialogRef<DialogChooseSpotsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogChooseSpotsData) { }

  ngOnInit(): void {
    this.oldSelectedSpots = this.data.selectedSpots;
    this.data.selectedSpots++;
  }

}
