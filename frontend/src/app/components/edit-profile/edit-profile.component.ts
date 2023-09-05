import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { Component, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { UpdateUser, User } from 'src/app/dtos/user';
import { UserService } from 'src/app/services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Address } from 'src/app/dtos/address';
import { MatDialog } from '@angular/material/dialog';
import { ChangePasswordDialogComponent } from './edit-dialogs/change-password-dialog/change-password-dialog.component';
import { AuthService } from 'src/app/services/auth.service';
import { DeleteAccountDialogComponent } from './edit-dialogs/delete-account-dialog/delete-account-dialog/delete-account-dialog.component';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.scss']
})
export class EditProfileComponent implements OnInit {

  crop = false;

  // set to true if user has been loaded
  loaded = false;

  // store the current user
  user: User;

  // Flags for user input validation in relevant fields
  emailCheckValid: FormControl; // = new FormControl('', [EditProfileValidator.validEmail, EditProfileValidator.cantBeBlank]);
  emailConfirmCheckValid: FormControl; // = new FormControl('', [EditProfileValidator.validEmail, EditProfileValidator.cantBeBlank]);
  passwordCheckValid: FormControl;
  passwordConfirmCheckValid: FormControl;

  // is true if every field regarding the address is null, then a button is displayed to add an address
  toAddAddress = false;

  // toggle error div if new password and new password confirmatino do not match
  passwordMatches = true;

  // Toggled when user clicks the corresponding edit button
  editFirstNameFlag = false;
  editLastNameFlag = false;
  editEmailFlag = false;
  editAddressFlag = false;
  editPasswordFlag = false;


  // store the changed e.g. firstname,
  //  if editing is canceld set the value to the corresponding value in the user
  editedFirstName = '';
  editedLastName = '';
  editedEmail = '';
  userConfirmChangeEmail = '';
  editedAddress: Address = {
    country: '',
    city: '',
    zipCode: '',
    street: ''
  };
  editedPassword = '';
  editedPasswordConfirm = '';


  constructor(
    private responsive: BreakpointObserver,
    private userService: UserService,
    private authService: AuthService,
    private snackbar: MatSnackBar,
    private router: Router,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {

    this.getDetails();

    this.responsive
      .observe(['(min-width: 500px)'])
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
        this.editedFirstName = this.user.firstName;
        this.editedLastName = this.user.lastName;
        this.editedEmail = this.user.email;
        this.editedAddress.country = this.user.country;
        this.editedAddress.city = this.user.city;
        this.editedAddress.zipCode = this.user.zipCode;
        this.editedAddress.street = this.user.street;

        if (this.user.country === null &&
          this.user.city === null &&
          this.user.zipCode === null &&
          this.user.street === null) {
            this.toAddAddress = true;
          }

        if (this.editedAddress.country === null) {
          this.editedAddress.country = '';
        }
        if (this.editedAddress.city === null) {
          this.editedAddress.city = '';
        }
        if (this.editedAddress.zipCode === null) {
          this.editedAddress.zipCode = '';
        }
        if (this.editedAddress.street === null) {
          this.editedAddress.street = '';
        }
      },
      error: error => {
        this.snackbar.open('Error fetching your data', 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
        this.router.navigate(['/']);
      }
    });
  }


  // toggle editFirstNameFlag
  editFirstName(): void {
    this.editFirstNameFlag = !this.editFirstNameFlag;
  }
  // toggle editLastNameFlag
  editLastName(): void {
    this.editLastNameFlag = !this.editLastNameFlag;
  }
  // toggle editEmailFlag
  editEmail(): void {
    this.editEmailFlag = !this.editEmailFlag;
  }
  // toggle editAddressFlag
  editAddress(): void {
    this.editAddressFlag = !this.editAddressFlag;
  }


  changeFirstName(): void {

    const toUpdate: UpdateUser = {
      firstName: this.editedFirstName,
      lastName: null,
      email: null,
      password: null,
      country: null,
      city: null,
      zipCode: null,
      street: null
    };

    this.userService.updateFirstname(toUpdate).subscribe({
      next: data => {
        this.user = data;
        this.snackbar.open('First name successfully updated', 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-success']
        });
        this.editFirstNameFlag = false;
      },
      error: error => {
        this.snackbar.open(`${error.error}`, 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });
  }

  cancelFirstNameEdit(): void {
    this.editFirstNameFlag = false;
    this.editedFirstName = this.user.firstName;
  }

  changeLastName(): void {
    const toUpdate: UpdateUser = {
      firstName: null,
      lastName: this.editedLastName,
      email: null,
      password: null,
      country: null,
      city: null,
      zipCode: null,
      street: null
    };

    this.userService.updateLastname(toUpdate).subscribe({
      next: data => {
        this.user = data;
        this.snackbar.open('Last name successfully updated', 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-success']
        });
        this.editLastNameFlag = false;
      },
      error: error => {
        this.snackbar.open(`${error.error}`, 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });
  }

  cancelLastNameEdit(): void {
    this.editLastNameFlag = false;
    this.editedLastName = this.user.lastName;
  }

  changeEmail(): void {

    const toUpdate: UpdateUser = {
      firstName: null,
      lastName: null,
      email: this.editedEmail,
      password: null,
      country: null,
      city: null,
      zipCode: null,
      street: null
    };

    this.userConfirmChangeEmail = '';

    this.userService.updateEmail(toUpdate).subscribe({
      next: data => {
        this.user = data;
        this.snackbar.open('Email successfully updated', 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-success']
        });
        this.editEmailFlag = false;
      },
      error: error => {
        this.snackbar.open(`${error.error}`, 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });
  }

  cancelEmailEdit(): void {
    this.editEmailFlag = false;
    this.editedEmail = this.user.email;
    this.userConfirmChangeEmail = '';
  }

  changeAddress(): void {
    if (this.editedAddress.country === '') {
      this.editedAddress.country = null;
    }
    if (this.editedAddress.city === '') {
      this.editedAddress.city = null;
    }
    if (this.editedAddress.zipCode === '') {
      this.editedAddress.zipCode = null;
    }
    if (this.editedAddress.street === '') {
      this.editedAddress.street = null;
    }

    const toUpdate: UpdateUser = {
      firstName: null,
      lastName: null,
      email: null,
      password: null,
      country: this.editedAddress.country,
      city: this.editedAddress.city,
      zipCode: this.editedAddress.zipCode,
      street: this.editedAddress.street
    };

    this.toAddAddress = false;


    this.userService.updateAddress(toUpdate).subscribe({
      next: data => {
        this.user = data;
        this.snackbar.open('Address successfully updated', 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-success']
        });
        this.editAddressFlag = false;
      },
      error: error => {
        this.snackbar.open(`${error.error}`, 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });
  }

  cancelAddressEdit(): void {
    this.editAddressFlag = false;
    // dem editAddress die werte vom user.address zuweisen eigentlich
    if (this.user.country === null) {
      this.editedAddress.country = '';
    } else {
      this.editedAddress.country = this.user.country;
    }
    if (this.user.city === null) {
      this.editedAddress.city = '';
    } else {
      this.editedAddress.city = this.user.city;
    }
    if (this.user.zipCode === null) {
      this.editedAddress.zipCode = '';
    } else {
      this.editedAddress.zipCode = this.user.zipCode;
    }
    if (this.user.street === null) {
      this.editedAddress.street = '';
    } else {
      this.editedAddress.street = this.user.street;
    }
  }

  changePassword(): void {
    if (!(this.editedPassword === this.editedPasswordConfirm)) {
      // set flag to displaye error div
      this.passwordMatches = false;
      return;
    }

    if ((this.editedPassword.length < 8) || (this.editedPasswordConfirm.length < 8)) {
      return;
    }

    this.passwordMatches = true;


    // if validation passt
    const toUpdate: UpdateUser = {
      firstName: null,
      lastName: null,
      email: null,
      password: this.editedPassword,
      country: null,
      city: null,
      zipCode: null,
      street: null
    };

    this.editedPassword = '';
    this.editedPasswordConfirm = '';

    this.userService.updatePassword(toUpdate).subscribe({
      next: data => {
        this.snackbar.open('Password successfully changed', 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-success']
        });
        this.editPasswordFlag = false;
      }, error: error => {
        this.snackbar.open(`${error.error}`, 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });
  }

  cancelPasswordEdit(): void {
    this.editedPassword = '';
    this.editedPasswordConfirm = '';
    this.editPasswordFlag = false;
    this.passwordMatches = true;
  }

  openConfirmationDialog(): void {
    const dialogRef = this.dialog.open(ChangePasswordDialogComponent, {
      width: '30em'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.editPasswordFlag = true;
      } else {
        this.editAddressFlag = false;
      }
    });
  }

  openDeletionDialog(): void {
    const dialogRef = this.dialog.open(DeleteAccountDialogComponent, {
      width: '30em'
    });
  }

  countryIsBlank(): boolean {
    if (this.editedAddress.country.trim() === '') {
      return true;
    }
    return false;
  }

  cityIsBlank(): boolean {
    if (this.editedAddress.city.trim() === '') {
      return true;
    }
    return false;
  }

  zipCodeIsBlank(): boolean {
    if (this.editedAddress.zipCode.trim() === '') {
      return true;
    }
    return false;
  }

  streetIsBlank(): boolean {
    if (this.editedAddress.street.trim() === '') {
      return true;
    }
    return false;
  }
}
