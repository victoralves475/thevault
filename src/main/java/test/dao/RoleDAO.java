package test.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.AbstractDAO;
import test.model.Role;

public class RoleDAO extends AbstractDAO<Role, Integer> {

    public RoleDAO() {
        super(Role.class, "roles");
    }

    @Override
    protected Role mapResultSetToEntity(ResultSet rs) throws SQLException {
        Role r = new Role();
        r.setId(rs.getInt("id"));
        r.setRoleName(rs.getString("role_name"));
        return r;
    }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, Role entity) throws SQLException {
        // Campos sem @Id: role_name
        stmt.setString(1, entity.getRoleName());
    }
}