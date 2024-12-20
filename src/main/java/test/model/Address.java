package test.model;

import annotations.Column;
import annotations.Entity;
import annotations.Id;
import annotations.NotBlank;
import annotations.relationships.OneToOne;

@Entity(tableName="addresses")
public class Address {
    @Id
    @Column(name="id", type="SERIAL")
    private Integer id;

    @NotBlank
    @Column(name="street", nullable=false)
    private String street;

    @NotBlank
    @Column(name="city", nullable=false)
    private String city;

    @OneToOne(targetEntity = User.class, foreignKeyName="user_id", optional=false)
    @Column(name="user_id", nullable=false)
    private User user;

    public Integer getId(){return id;}
    public void setId(Integer id){this.id=id;}

    public String getStreet(){return street;}
    public void setStreet(String street){this.street=street;}

    public String getCity(){return city;}
    public void setCity(String city){this.city=city;}

    public User getUser(){return user;}
    public void setUser(User user){this.user=user;}
}
