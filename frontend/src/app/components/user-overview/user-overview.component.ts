import {Component, OnInit, ViewChild} from '@angular/core';
import {UserShort} from '../../dtos/user';
import {AdminService} from '../../services/admin.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {PageEvent} from '@angular/material/paginator';
import {MatDialog} from '@angular/material/dialog';
import {MatTable, MatTableDataSource} from '@angular/material/table';
import {LockUserDialogComponent} from './lock-user-dialog/lock-user-dialog.component';
import {ResetPasswordDialogComponent} from './reset-password-dialog/reset-password-dialog.component';

@Component({
  selector: 'app-user-overview',
  templateUrl: './user-overview.component.html',
  styleUrls: ['./user-overview.component.scss']
})
export class UserOverviewComponent implements OnInit {

  @ViewChild(MatTable) table: MatTable<UserShort>;
  users: MatTableDataSource<UserShort>;
  columnsToShow = ['username', 'email', 'firstName', 'lastName', 'lock', 'reset'];
  numberOfUsers = 0;
  pageSize = 10;

  constructor(
    private adminService: AdminService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) { }

  openLockDialog(username: string) {
    this.dialog.open(LockUserDialogComponent, {
      height: '150px',
      width: '300px',
      data: {
        username
      }
    });
  }

  openResetDialog(username: string) {
    this.dialog.open(ResetPasswordDialogComponent, {
      height: '150px',
      width: '300px',
      data: {
        username
      }
    });
  }

  ngOnInit(): void {
    this.getUsers(0);
  }

  nextPage(event: PageEvent) {
    this.getUsers(event.pageIndex);
  }

  private getUsers(page: number) {
    this.adminService.findAllNonAdminsOrderedByUsername(page, this.pageSize)
      .subscribe(
        {
          next: data => {
            this.users = new MatTableDataSource(data['content']);
            this.numberOfUsers = data['totalElements'];
          } ,
          error: error => {
            this.snackBar.open('An error occurred while getting users: ' + error.error, null, {
              duration: 3000,
              horizontalPosition: 'right',
              verticalPosition: 'top',
              panelClass: ['snackbar-error']
            });
          }
        }
      );
  }

}
