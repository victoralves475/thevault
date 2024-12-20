package test.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.AbstractDAO;
import dao.exceptions.DataAccessException;
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
        // Neste momento, não carregamos roles automaticamente.
        // Poderíamos, porém, criar um método para carregar roles da join table.
        return u;
    }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, User entity) throws SQLException {
        // Campos sem @Id são: name
        stmt.setString(1, entity.getName());
    }

    // Método opcional para lidar com a join table:
    // Ao inserir ou atualizar o usuário, poderíamos inserir registros em users_roles.
    public void addRoles(User user, List<Integer> roleIds) throws DataAccessException {
        // Certificar-se de que o ID do usuário não é nulo
        if (user.getId() == null) {
            throw new DataAccessException("ID do usuário é nulo. Não é possível adicionar roles.");
        }

        // Verificar se a lista de roleIds não é nula ou vazia
        if (roleIds == null || roleIds.isEmpty()) {
            // Dependendo da lógica, você pode apenas retornar
            // ou lançar uma exceção se isso não for esperado
            return; 
        }

        String sql = "INSERT INTO users_roles (user_id, role_id) VALUES (?, ?)";
        try (Connection conn = database.DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Integer roleId : roleIds) {
                // Verificar se o roleId não é nulo
                if (roleId == null) {
                    throw new DataAccessException("ID da role é nulo. Não é possível adicionar ao usuário " + user.getId());
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
        List<Integer> roleIds = new ArrayList<>();
        String sql = "SELECT role_id FROM users_roles WHERE user_id = ?";
        try (Connection conn = database.DatabaseConnection.getInstance().getConnection();
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
