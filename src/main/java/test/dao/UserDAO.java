package test.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.AbstractDAO;
import test.model.User;

public class UserDAO extends AbstractDAO<User, Integer> {

    public UserDAO() {
        super(User.class, "users");
    }

    @Override
    protected User mapResultSetToEntity(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        return u;
    }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, User entity) throws SQLException {
        // Aqui assumimos que o order das colunas no INSERT e UPDATE segue a ordem dos fields sem @Id
        // A ordem dos campos é definida pelo getColumnNames() no AbstractDAO
        // Vamos supor que "name" é o único campo além do id.

        // Como id é @Id, name será o único campo inserido
        stmt.setString(1, entity.getName());
    }
}
