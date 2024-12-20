package test.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.AbstractDAO;
import dao.exceptions.DataAccessException;
import database.DatabaseConnection;
import test.model.Order;

public class OrderDAO extends AbstractDAO<Order, Integer> {

    public OrderDAO() {
        super(Order.class, "orders");
    }

    @Override
    protected Order mapResultSetToEntity(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        int userId = rs.getInt("user_id");
        java.sql.Date orderDate = rs.getDate("order_date");
        o.setOrderDate(new java.util.Date(orderDate.getTime()));

        // Buscar o usuário associado
        // Supondo que você tenha o UserDAO
        UserDAO userDAO = new UserDAO();
        o.setUser(userDAO.findById(userId));

        return o;
    }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, Order entity) throws SQLException {
        // Assumindo que a ordem dos campos sem @Id é user_id, order_date
        // conforme definido na classe Order
        stmt.setInt(1, entity.getUser().getId());
        stmt.setDate(2, new java.sql.Date(entity.getOrderDate().getTime()));
    }

    /**
     * Método para buscar orders por user_id
     */
    public List<Order> findByUserId(int userId) throws DataAccessException {
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
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