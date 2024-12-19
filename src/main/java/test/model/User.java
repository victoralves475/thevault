package test.model;

import annotations.Column;
import annotations.Entity;
import annotations.Id;

@Entity(tableName = "users")
public class User {
    @Id
    @Column(name = "id", type = "SERIAL")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;
    
    // getters e setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
