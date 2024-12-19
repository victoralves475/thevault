package test.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import dao.AbstractDAO;
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
        int userId = rs.getInt("user_id");
        Date orderDate = rs.getDate("order_date");
        o.setOrderDate(orderDate);

        // Buscar o usuário referenciado
        UserDAO userDAO = new UserDAO();
        try {
            User u = userDAO.findById(userId);
            o.setUser(u);
        } catch (Exception e) {
            throw new SQLException("Erro ao buscar usuário relacionado", e);
        }

        return o;
    }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, Order entity) throws SQLException {
        // Os campos sem @Id são user_id e order_date na ordem que aparecem na classe
        // O foreignKeyName="user_id" define a coluna user_id no banco
        // A ordem dada pelo getColumnNames() no AbstractDAO segue a ordem dos fields na classe (exceto o @Id)
        // Fields: user, orderDate
        // user -> user_id
        // orderDate -> order_date

        // 1º parametro: user_id
        stmt.setInt(1, entity.getUser().getId());
        // 2º parametro: order_date
        stmt.setDate(2, new java.sql.Date(entity.getOrderDate().getTime()));
    }
}
