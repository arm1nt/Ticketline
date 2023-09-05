import { Component, OnInit } from '@angular/core';
import {AbstractControlOptions, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  registerForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private userService: UserService,
              private snackBar: MatSnackBar,
              private router: Router) { }

  get email() {
    return this.registerForm.get('email');
  }

  get password() {
    return this.registerForm.get('password');
  }

  get username() {
    return this.registerForm.get('username');
  }

  get firstName() {
    return this.registerForm.get('firstName');
  }

  get lastName() {
    return this.registerForm.get('lastName');
  }

  get country() {
    return this.registerForm.get('country');
  }

  get city() {
    return this.registerForm.get('city');
  }

  get street() {
    return this.registerForm.get('street');
  }

  get zipCode() {
    return this.registerForm.get('zipCode');
  }


  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      email: ['', [Validators.required]],
      password: ['', [Validators.required,Validators.minLength(8)]],
      confirmedPassword: ['', [Validators.required]],
      username: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      firstName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      street: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      country: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      city: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      zipCode: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    },
      {validator: this.matchPasswords('password','confirmedPassword')} as AbstractControlOptions);
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
    if(this.registerForm.valid) {
      const formValue = this.registerForm.value;
      delete formValue.confirmedPassword;
      this.userService.registerUser(formValue).subscribe({
        next: () => {
          this.snackBar.open('Registration successful!', null, {
            duration: 2000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snackbar-success']
          });
          this.router.navigate(['login']).then();
        }, error: error => {
          this.snackBar.open('An error occurred while registering: ' + error.error, null, {
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
