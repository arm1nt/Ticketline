import { Component, OnInit, Inject } from '@angular/core';
import {MatDialog, MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UpdatePassword } from 'src/app/dtos/user';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-change-password-dialog',
  templateUrl: './change-password-dialog.component.html',
  styleUrls: ['./change-password-dialog.component.scss']
})
export class ChangePasswordDialogComponent implements OnInit {
  password: '';
  passwordCorrect?: boolean;

  constructor(public dialogRef: MatDialogRef<ChangePasswordDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: string,
    private userService: UserService,
    private snackbar: MatSnackBar) { }

  ngOnInit(): void {
    this.password = '';
  }

  onNoClick(): void{
    this.dialogRef.close();
  }

  checkPassword(): void {
    if (this.password === '' || this.password === null) {
      return;
    }
    const toCheck: UpdatePassword = {
      password: this.password
    };
    this.userService.checkIfPasswordMatches(toCheck).subscribe({
      next: data => {
        if (data) {
          this.passwordCorrect = true;
          document.getElementById('exitDialog').click();
        } else {
          this.passwordCorrect = false;
        }
      }, error: error => {
        this.snackbar.open('An error occured while fetching your password', null, {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-error']
        });
      }
    });
  }
}
