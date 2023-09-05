import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-panel-dialog',
  templateUrl: './admin-panel-dialog.component.html',
  styleUrls: ['./admin-panel-dialog.component.scss']
})
export class AdminPanelDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AdminPanelDialogComponent>,
    private router: Router,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
  }

  closeAndReroute(option: number): void {

    switch (option) {
      case 1:
        this.router.navigate(['/create-user']);
        break;
      case 2:
        this.router.navigate(['/news/create']);
        break;
      case 3:
        this.router.navigate(['/location']);
        break;
      case 4:
        this.router.navigate(['/eventhall']);
        break;
      case 5:
        this.router.navigate(['/layout/add']);
        break;
      case 6:
        this.router.navigate(['/event/create']);
        break;
      case 7:
        this.router.navigate(['/performance/create']);
        break;
      case 8:
        this.router.navigate(['/performers/add']);
        break;
      case 9:
        this.router.navigate(['/locked']);
        break;
      case 10:
        this.router.navigate(['/users']);
        break;
      default:
        this.snackBar.open('This option does currently not exist', 'Dismiss', {
          verticalPosition: 'top',
          horizontalPosition: 'right',
          duration: 1500
        });
        break;
    }
    this.dialogRef.close();
  }
}
