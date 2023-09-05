import { Component, OnInit } from '@angular/core';
import {UserShort} from '../../dtos/user';
import {AdminService} from '../../services/admin.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatTableDataSource} from '@angular/material/table';
import {MatDialog} from '@angular/material/dialog';
import {UnlockUserDialogComponent} from './unlock-user-dialog/unlock-user-dialog.component';

@Component({
  selector: 'app-locked-users',
  templateUrl: './locked-users.component.html',
  styleUrls: ['./locked-users.component.scss']
})
export class LockedUsersComponent implements OnInit {

  lockedUsers: MatTableDataSource<UserShort>;
  columnsToShow = ['username', 'email', 'firstName', 'lastName', 'unlock'];

  constructor(
    private adminService: AdminService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) { }

  openDialog(username: string) {

    this.dialog.open(UnlockUserDialogComponent, {
      height: '150px',
      width: '300px',
      data: {
        username
      }
    });
  }

  unlockUser(username: string) {
    this.adminService.unlockUser(username).subscribe({
      next: () => {
        this.snackBar.open('Successfully unlocked user!', null, {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-success']
        });
      },
      error: error => {
        this.snackBar.open('An error occurred while unlocking user: ' + error.error, null, {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-error']
        });
      }
    });
  }

  ngOnInit(): void {
    this.adminService.getLockedUsers().subscribe({
      next: (data) => this.lockedUsers = new MatTableDataSource(data),
      error: error => {
        this.snackBar.open('An error occurred while getting locked users: ' + error.error, null, {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-error']
        });
      }
    });
  }

}
