package io.github.lisalorita.ordermanagement.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

  private int minLength;
  private int minLowercase;
  private int minUppercase;
  private int minNumbers;
  private int minSymbols;

  @Override
  public void initialize(StrongPassword constraintAnnotation) {
    this.minLength = constraintAnnotation.minLength();
    this.minLowercase = constraintAnnotation.minLowercase();
    this.minUppercase = constraintAnnotation.minUppercase();
    this.minNumbers = constraintAnnotation.minNumbers();
    this.minSymbols = constraintAnnotation.minSymbols();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null)
      return false;

    if (value.length() < minLength)
      return false;

    long lowercaseCount = value.chars().filter(Character::isLowerCase).count();
    long uppercaseCount = value.chars().filter(Character::isUpperCase).count();
    long numberCount = value.chars().filter(Character::isDigit).count();
    long symbolCount = value.chars().filter(c -> !Character.isLetterOrDigit(c)).count();

    return lowercaseCount >= minLowercase &&
        uppercaseCount >= minUppercase &&
        numberCount >= minNumbers &&
        symbolCount >= minSymbols;
  }
}