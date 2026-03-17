package io.github.lisalorita.ordermanagement.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
  String message() default "Password is not strong enough";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  int minLength() default 8;

  int minLowercase() default 1;

  int minUppercase() default 1;

  int minNumbers() default 1;

  int minSymbols() default 1;
}