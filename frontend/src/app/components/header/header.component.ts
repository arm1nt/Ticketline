import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Breakpoints, BreakpointObserver} from '@angular/cdk/layout';
import { MatDialog } from '@angular/material/dialog';
import { AdminPanelDialogComponent } from '../admin-panel-dialog/admin-panel-dialog.component';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  crop = false;
  expand = false;

  constructor(
    public authService: AuthService,
    private responsive: BreakpointObserver,
    private dialog: MatDialog) { }

  ngOnInit() {

    this.responsive.observe([Breakpoints.XSmall, Breakpoints.Small]).subscribe(
      result => {
        if (result.matches) {
          this.crop = true;
        } else {
          this.crop = false;
          this.expand = false;
        }
      }
    );
  }

  expandCroppedView(): void {
    this.expand = !this.expand;
  }

  hideExpandedView(): void {
    this.expand = false;
  }

  logoutAndHideExpandedView(): void {
    this.expand = false;
    this.authService.logoutUser();
  }

  openAdminPanel(): void {

    if (this.crop) {
      this.hideExpandedView();
    }


    this.dialog.open(AdminPanelDialogComponent, {
      width: '30em',
      height: '45em'
    });
  }

}
