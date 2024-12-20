package test.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dao.AbstractDAO;
import dao.exceptions.DataAccessException;
import database.DatabaseConnection;
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
        // Campos sem @Id: name
        stmt.setString(1, entity.getName());
    }

    public void addRoles(User user, List<Integer> roleIds) throws DataAccessException {
        if (user.getId() == null) {
            throw new DataAccessException("ID do usuário é nulo. Não é possível adicionar roles.");
        }
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO users_roles (user_id, role_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Integer roleId : roleIds) {
                if (roleId == null) {
                    throw new DataAccessException("ID da role é nulo.");
                }
                stmt.setInt(1, user.getId());
                stmt.setInt(2, roleId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao adicionar roles ao usuário", e);
        }
    }

    public List<Integer> getUserRolesIds(int userId) throws DataAccessException {
        List<Integer> roleIds = new java.util.ArrayList<>();
        String sql = "SELECT role_id FROM users_roles WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roleIds.add(rs.getInt("role_id"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao buscar roles do usuário", e);
        }
        return roleIds;
    }
}
