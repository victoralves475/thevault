package annotations.relationships;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToOne {
	Class<?> targetEntity();
	String foreignKeyName() default "";
	boolean optional() default true;
	
}

