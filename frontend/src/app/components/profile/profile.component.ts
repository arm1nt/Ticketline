import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { User } from 'src/app/dtos/user';
import { UserService } from 'src/app/services/user.service';
import { Router } from '@angular/router';
import { OrderService } from 'src/app/services/order.service';
import { buffer } from 'stream/consumers';
import { TicketsService } from 'src/app/services/tickets.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  crop = false;

  loaded = false;

  user: User;

  constructor(
    private responsive: BreakpointObserver,
    private userService: UserService,
    private snackbar: MatSnackBar,
    private router: Router,
    private orderService: OrderService,
    private ticketService: TicketsService
  ) { }

  ngOnInit(): void {

    this.getDetails();

    this.responsive
      .observe(['(min-width: 775px)'])
      .subscribe((state: BreakpointState) => {
        if (state.matches) {
          this.crop = false;
        } else {
          this.crop = true;
        }
      });
  }


  getDetails(): void {
    this.userService.getDetails().subscribe({
      next: data => {
        this.loaded = true;
        this.user = data;
      },
      error: error => {
        //navigate back to homepage
        this.snackbar.open('Error fetching your data', null, {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
        this.router.navigate(['/']);
      }
    });
  }
}
