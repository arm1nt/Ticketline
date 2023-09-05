import {Component, Inject, OnInit} from '@angular/core';
import {AdminService} from '../../../services/admin.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-unlock-user-dialog',
  templateUrl: './unlock-user-dialog.component.html',
  styleUrls: ['./unlock-user-dialog.component.scss']
})
export class UnlockUserDialogComponent implements OnInit {

  constructor(
    private adminService: AdminService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private router: Router,
    @Inject(MAT_DIALOG_DATA) public data: { username: string }
  ) { }

  ngOnInit(): void {
  }

  unlockUser() {
    this.adminService.unlockUser(this.data.username).subscribe({
      next: () => {
        this.snackBar.open('Successfully unlocked user!', null, {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-success']
        });
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.router.onSameUrlNavigation = 'reload';
        this.router.navigate(['/locked']);
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

}
