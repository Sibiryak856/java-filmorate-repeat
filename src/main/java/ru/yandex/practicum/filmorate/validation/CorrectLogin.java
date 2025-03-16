package ru.yandex.practicum.filmorate.validation;

import javax.validation.constraints.NotBlank;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NotBlank
public @interface CorrectLogin {
    String message() default "Login cannot be empty and contain spaces";

    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}
