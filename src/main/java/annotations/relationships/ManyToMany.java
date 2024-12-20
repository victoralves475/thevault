package annotations.relationships;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToMany {
    Class<?> targetEntity();
    String joinTable();        // nome da tabela de junção
    String joinColumn();       // nome da FK que referencia a entidade atual
    String inverseJoinColumn(); // nome da FK que referencia a entidade alvo
}
