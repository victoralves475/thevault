package test.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dao.AbstractDAO;
import dao.exceptions.DataAccessException;
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
    
   // Método para demonstrar a busca com where

    public List<Product> findByOrderAndPrice(String order_id, String price) throws DataAccessException {
        // Montar a cláusula WHERE
        String where = "order_id = ? AND price >= ?";

        // Obter a query completa do QueryBuilder
        String sql = queryBuilder.buildFindWithWhere(where);

        try (Connection conn = database.DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Converter os valores antes de setar no statement
            // Exemplo: order_id -> int, price -> double
            int orderIdInt = Integer.parseInt(order_id);
            double priceDouble = Double.parseDouble(price);

            // Substituir parâmetros
            stmt.setInt(1, orderIdInt);
            stmt.setDouble(2, priceDouble);

            List<Product> result = new java.util.ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToEntity(rs));
                }
            }
            return result;

        } catch (SQLException e) {
            throw new DataAccessException("Erro: ", e);
        } catch (NumberFormatException nfe) {
            throw new DataAccessException("Parâmetros inválidos: não foi possível converter para número.", nfe);
        }
    }


}
