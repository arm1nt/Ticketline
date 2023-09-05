import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';
import {AdminService} from '../../services/admin.service';

@Component({
  selector: 'app-user-create',
  templateUrl: './user-create.component.html',
  styleUrls: ['./user-create.component.scss']
})
export class UserCreateComponent implements OnInit {

  createUserForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private adminService: AdminService,
              private snackBar: MatSnackBar,
              private router: Router) { }

  get email() {
    return this.createUserForm.get('email');
  }

  get username() {
    return this.createUserForm.get('username');
  }

  get firstName() {
    return this.createUserForm.get('firstName');
  }

  get lastName() {
    return this.createUserForm.get('lastName');
  }

  get country() {
    return this.createUserForm.get('country');
  }

  get city() {
    return this.createUserForm.get('city');
  }

  get street() {
    return this.createUserForm.get('street');
  }

  get zipCode() {
    return this.createUserForm.get('zipCode');
  }


  ngOnInit(): void {
    this.createUserForm = this.formBuilder.group({
        email: ['', [Validators.required]],
        username: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
        admin: ['false', [Validators.required]],
        firstName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
        lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
        street: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
        country: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
        city: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
        zipCode: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      });
  }

  submit() {
    if(this.createUserForm.valid) {
      const formValue = this.createUserForm.value;
      this.adminService.createUser(formValue).subscribe({
        next: () => {
          this.snackBar.open('User creation successful!', null, {
            duration: 2000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-success']
          });
          this.router.navigate(['/']).then();
        }, error: error => {
          this.snackBar.open('An error occurred while creating the user: ' + error.error , null, {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-error']
          });
        }
      });
    }
  }

}
