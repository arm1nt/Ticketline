import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ResetPassword} from '../../dtos/user';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  tokenObject;
  token: string;
  resetForm: FormGroup;
  tokenValid: boolean;
  errorMessage = '';
  constructor(private route: ActivatedRoute,
              private formBuilder: FormBuilder,
              private userService: UserService,
              private snackBar: MatSnackBar,
              private router: Router) { }

  ngOnInit(): void {
    this.route.queryParamMap
      .subscribe((params) => {
          this.tokenObject = { ...params.keys, ...params };
          this.token = this.tokenObject.params.token;
        }
      );

    this.resetForm = this.formBuilder.group({
      password: ['', [Validators.required,Validators.minLength(8)]],
      confirmedPassword: ['', [Validators.required]],
    }, {validator: this.matchPasswords('password','confirmedPassword')}
    );

    this.userService.isTokenValid(this.token).subscribe({
      next: () => this.tokenValid = true,
      error: error => {
        this.tokenValid = false;
        this.errorMessage = error.error;
      }
    });
  }

  matchPasswords(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({passwordMismatch: true});
      } else {
        matchingControl.setErrors(null);
      }
    };
  }

  submit() {
    if(this.resetForm.valid) {
      const formValue = this.resetForm.value;
      const resetPassword: ResetPassword = {
        password: formValue.password,
        token: this.token
      };
      this.userService.resetPassword(resetPassword).subscribe({
        next: () => {
          this.snackBar.open('Password reset successful!', null, {
            duration: 2000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snackbar-success']
          });
          this.router.navigate(['login']).then();
        }, error: error => {
          this.snackBar.open('An error occurred while resetting password: ' + error.error, null, {
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
