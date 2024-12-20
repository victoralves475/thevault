package test.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.AbstractDAO;
import test.model.Order;
import test.model.Product;

public class ProductDAO extends AbstractDAO<Product, Integer> {

    public ProductDAO() {
        super(Product.class, "products");
    }

    @Override
    protected Product mapResultSetToEntity(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getDouble("price"));

        int orderId = rs.getInt("order_id");
        OrderDAO orderDAO = new OrderDAO();
        Order o = orderDAO.findById(orderId);
        p.setOrder(o);

        return p;
    }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, Product entity) throws SQLException {
        // Campos sem @Id: name, price, order_id
        stmt.setString(1, entity.getName());
        if (entity.getPrice() != null) {
            stmt.setDouble(2, entity.getPrice());
        } else {
            stmt.setNull(2, java.sql.Types.DOUBLE);
        }
        stmt.setInt(3, entity.getOrder().getId());
    }
}
