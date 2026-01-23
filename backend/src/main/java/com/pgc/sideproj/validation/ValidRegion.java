package com.pgc.sideproj.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegionValidator.class)
public @interface ValidRegion {
    String message() default "유효하지 않은 지역입니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
