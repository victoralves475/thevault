package annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Min {
    double value();
    String message() default "Valor menor que o mínimo permitido.";
}
