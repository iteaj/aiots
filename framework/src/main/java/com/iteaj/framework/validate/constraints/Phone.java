package com.iteaj.framework.validate.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.*;

@Documented
@Constraint(
        validatedBy = {}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Phone.List.class)
public @interface Phone {

    String message() default "{jakarta.validation.constraints.Phone.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String regexp() default ".*";

    Pattern.Flag[] flags() default {};

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        Phone[] value();
    }
}
