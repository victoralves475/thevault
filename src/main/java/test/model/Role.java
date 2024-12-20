package test.model;

import annotations.Column;
import annotations.Entity;
import annotations.Id;
import annotations.NotBlank;

@Entity(tableName="roles")
public class Role {
    @Id
    @Column(name="id", type="SERIAL")
    private Integer id;

    @NotBlank(message="Nome da função não pode ser vazio.")
    @Column(name="role_name", nullable=false)
    private String roleName;

    public Integer getId(){return id;}
    public void setId(Integer id){this.id= id;}

    public String getRoleName(){return roleName;}
    public void setRoleName(String roleName){this.roleName=roleName;}
}
