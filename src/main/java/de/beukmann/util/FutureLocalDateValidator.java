package de.beukmann.util;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Future;

public class FutureLocalDateValidator implements ConstraintValidator<Future, LocalDate>{

	@Override
	public void initialize(Future constraintAnnotation) {
		
	}

	@Override
	public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
		if (value == null)
			return true;
		return LocalDate.now().isBefore(value);
	}
	
}
