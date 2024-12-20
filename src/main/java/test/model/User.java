package test.model;

import java.util.List;

import annotations.Column;
import annotations.Entity;
import annotations.Id;
import annotations.NotBlank;
import annotations.relationships.ManyToMany;

@Entity(tableName="users")
public class User {
    @Id
    @Column(name="id", type="SERIAL")
    private Integer id;

    @NotBlank(message="Nome do usuário não pode ser vazio.")
    @Column(name="name", nullable=false)
    private String name;

    @ManyToMany(
        targetEntity = Role.class,
        joinTable = "users_roles",
        joinColumn = "user_id",
        inverseJoinColumn = "role_id"
    )
    private List<Role> roles;

    // Um User tem um Address (OneToOne)
    // Opcional: caso queira que Address seja o dono do relacionamento, inverta a lógica.
    // Aqui, faremos Address ser o dono do relacionamento, então User não precisa do campo.
    // Se quiser que o User seja dono, usar @OneToOne aqui. Vamos deixar User sem @OneToOne
    // e o Address com @OneToOne apontando para User.
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() {return name;}
    public void setName(String name){this.name = name;}

    public List<Role> getRoles() {return roles;}
    public void setRoles(List<Role> roles){this.roles = roles;}
}
