package test.model;

import annotations.Column;
import annotations.Entity;
import annotations.Id;
import annotations.NotBlank;
import annotations.relationships.ManyToOne;

@Entity(tableName="products")
public class Product {
    @Id
    @Column(name="id", type="SERIAL")
    private Integer id;

    @NotBlank
    @Column(name="name", nullable=false)
    private String name;

    @Column(name="price")
    private Double price;

    @ManyToOne(targetEntity=Order.class, foreignKeyName="order_id")
    @Column(name="order_id", nullable=false)
    private Order order;

    public Integer getId(){return id;}
    public void setId(Integer id){this.id=id;}

    public String getName(){return name;}
    public void setName(String name){this.name=name;}

    public Double getPrice(){return price;}
    public void setPrice(Double price){this.price=price;}

    public Order getOrder(){return order;}
    public void setOrder(Order order){this.order=order;}
}
