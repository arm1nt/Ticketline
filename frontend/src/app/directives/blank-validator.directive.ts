import { Directive } from '@angular/core';
import { AbstractControl, ValidationErrors, Validator } from '@angular/forms';
import { NG_VALIDATORS } from '@angular/forms';

@Directive({
  selector: '[appBlankValidator]',
  providers: [{provide: NG_VALIDATORS, useExisting: BlankValidatorDirective, multi: true}]
})
export class BlankValidatorDirective  implements Validator{

  validate(control: AbstractControl): ValidationErrors | null {
    if (!(control.value === null)) {
      if ((control.value as string) === '') {
        return {blankValidate: false};
      }
      if ((control.value as string).trim() === '') {
        return {blankValidate: false};
      }
    }
    return null;
  }
}
