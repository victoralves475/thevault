package test.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.AbstractDAO;
import test.model.Address;
import test.model.User;

public class AddressDAO extends AbstractDAO<Address, Integer> {

    public AddressDAO() {
        super(Address.class, "addresses");
    }

    @Override
    protected Address mapResultSetToEntity(ResultSet rs) throws SQLException {
        Address a = new Address();
        a.setId(rs.getInt("id"));
        a.setStreet(rs.getString("street"));
        a.setCity(rs.getString("city"));

        int userId = rs.getInt("user_id");
        UserDAO userDAO = new UserDAO();
        User u = userDAO.findById(userId);
        a.setUser(u);

        return a;
    }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, Address entity) throws SQLException {
        // Campos sem @Id: street, city, user_id
        stmt.setString(1, entity.getStreet());
        stmt.setString(2, entity.getCity());
        stmt.setInt(3, entity.getUser().getId());
    }
}
