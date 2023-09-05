import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../../services/user.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';

@Component({
  selector: 'app-request-email',
  templateUrl: './request-email.component.html',
  styleUrls: ['./request-email.component.scss']
})
export class RequestEmailComponent implements OnInit {

  requestForm: FormGroup;
  loading = false;

  constructor(private formBuilder: FormBuilder,
              private userService: UserService,
              private snackBar: MatSnackBar,
              private router: Router) { }

  ngOnInit(): void {
    this.requestForm = this.formBuilder.group({
      username: ['', [Validators.required]],
    });
  }

  submit() {
    this.loading = true;
    if(this.requestForm.valid) {
      const formValue = this.requestForm.value;
      const username = formValue.username;
      this.userService.requestResetEmail(username).subscribe({
        next: () => {
          this.loading = false;
          this.snackBar.open('Reset request successful, you should have received an e-mail!', null, {
            duration: 2000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snackbar-success']
          });
          this.router.navigate(['']).then();
        }, error: error => {
          this.loading = false;
          this.snackBar.open('An error occurred during the request: ' + error.error, null, {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snackbar-error']
          });
        }
      });
    }
  }

}
