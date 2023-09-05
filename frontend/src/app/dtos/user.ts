export class User {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  country: string;
  city: string;
  zipCode: string;
  street: string;
}

export interface UserRegistration {
  email: string;
  password: string;
  username: string;
  firstName: string;
  lastName: string;
  country: string;
  city: string;
  zipCode: string;
  street: string;
}

export interface UserCreation {
  email: string;
  username: string;
  admin: boolean;
  firstName: string;
  lastName: string;
  country: string;
  city: string;
  zipCode: string;
  street: string;
}

export interface ResetPassword {
  password: string;
  token: string;
}

export class UpdateUser {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    country: string;
    city: string;
    zipCode: string;
    street: string;
}

export class UserShort {
  id: number;
  email: string;
  username: string;
  firstName: string;
  lastName: string;
}

export class UpdateFirstname {
    firstName: string;
}

export class UpdateLastname {
    lastName: string;
}

export class UpdateEmail {
    email: string;
}

export class UpdateAddress {
    country: string;
    city: string;
    zipCode: string;
    street: string;
}

export class UpdatePassword {
    password: string;
}
