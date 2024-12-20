package test.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.AbstractDAO;
import dao.exceptions.DataAccessException;
import test.model.Order;
import test.model.User;

public class OrderDAO extends AbstractDAO<Order, Integer> {

    public OrderDAO() {
        super(Order.class, "orders");
    }

    @Override
    protected Order mapResultSetToEntity(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        java.sql.Date date = rs.getDate("order_date");
        o.setOrderDate(new java.util.Date(date.getTime()));

        int userId = rs.getInt("user_id");
        UserDAO userDAO = new UserDAO();
        User u = userDAO.findById(userId);
        o.setUser(u);

        return o;
    }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, Order entity) throws SQLException {
        // Campos sem @Id: user_id, order_date
        stmt.setInt(1, entity.getUser().getId());
        stmt.setDate(2, new java.sql.Date(entity.getOrderDate().getTime()));
    }

    public List<Order> findByUserId(int userId) throws DataAccessException {
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = database.DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao buscar orders por user_id", e);
        }
        return orders;
    }
}
