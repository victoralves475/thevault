package test.model;

import java.util.Date;
import java.util.List;

import annotations.Column;
import annotations.Entity;
import annotations.Id;
import annotations.relationships.ManyToOne;
import annotations.relationships.OneToMany;

@Entity(tableName="orders")
public class Order {
    @Id
    @Column(name="id", type="SERIAL")
    private Integer id;

    @ManyToOne(targetEntity = User.class, foreignKeyName = "user_id")
    @Column(name="user_id", nullable=false)
    private User user;

    @Column(name="order_date", nullable=false)
    private Date orderDate;

    @OneToMany(targetEntity=Product.class, mappedBy="order")
    private List<Product> products;

    public Integer getId(){return id;}
    public void setId(Integer id){this.id=id;}

    public User getUser(){return user;}
    public void setUser(User user){this.user=user;}

    public Date getOrderDate(){return orderDate;}
    public void setOrderDate(Date orderDate){this.orderDate=orderDate;}

    public List<Product> getProducts(){return products;}
    public void setProducts(List<Product> products){this.products=products;}
}
