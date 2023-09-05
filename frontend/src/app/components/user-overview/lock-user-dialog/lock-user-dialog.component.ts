import {Component, Inject, OnInit} from '@angular/core';
import {AdminService} from '../../../services/admin.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ActivatedRoute, Router} from '@angular/router';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'app-lock-user-dialog',
  templateUrl: './lock-user-dialog.component.html',
  styleUrls: ['./lock-user-dialog.component.scss']
})
export class LockUserDialogComponent implements OnInit {

  constructor(
    private adminService: AdminService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private router: Router,
    @Inject(MAT_DIALOG_DATA) public data: { username: string }
  ) { }

  ngOnInit(): void {
  }

  lockUser() {
    this.adminService.lockUser(this.data.username).subscribe({
      next: () => {
        this.snackBar.open('Successfully locked user!', null, {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-success']
        });
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.router.onSameUrlNavigation = 'reload';
        this.router.navigate(['/users']);
      },
      error: error => {
        this.snackBar.open('An error occurred while locking user: ' + error.error, null, {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-error']
        });
      }
    });
  }

}
