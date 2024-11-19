package il.panda.pancord.validation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@NotBlank(message = "filename of the video cannot be empty !")
@Size(min = 1, max = 512)
@Retention(RUNTIME)
public @interface FileName {
    String message() default "invalid title of content file";
}
