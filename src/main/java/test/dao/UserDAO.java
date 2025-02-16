package test.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dao.AbstractDAO;
import dao.exceptions.DataAccessException;
import database.DatabaseConnection;
import test.model.Product;
import test.model.User;

public class UserDAO extends AbstractDAO<User, Integer> {

    // Construtor que passa a classe User e o nome da tabela para o construtor da classe pai
    public UserDAO() {
        super(User.class, "users");
    }

    // Método para mapear o ResultSet para a entidade User
    @Override
    protected User mapResultSetToEntity(ResultSet rs) throws SQLException {
        User u = new User();  // Cria uma nova instância de User
        u.setId(rs.getInt("id"));  // Mapeia o ID do usuário
        u.setName(rs.getString("name"));  // Mapeia o nome do usuário
        return u;  // Retorna o usuário mapeado
    }

    // Método para definir os parâmetros da consulta SQL para inserção ou atualização
    @Override
    protected void setStatementParameters(PreparedStatement stmt, User entity) throws SQLException {
        // Define o valor do parâmetro 'name' (campo não anotado com @Id)
        stmt.setString(1, entity.getName());
    }

    // Método para adicionar roles a um usuário
    public void addRoles(User user, List<Integer> roleIds) throws DataAccessException {
        // Verifica se o ID do usuário é nulo, caso contrário, lança exceção
        if (user.getId() == null) {
            throw new DataAccessException("ID do usuário é nulo. Não é possível adicionar roles.");
        }
        
        // Se a lista de roles estiver vazia, não faz nada
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }

        // Define a consulta SQL para inserir os registros na tabela de junção (users_roles)
        String sql = "INSERT INTO users_roles (user_id, role_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Itera sobre os IDs das roles e executa a inserção
            for (Integer roleId : roleIds) {
                // Verifica se o ID da role é nulo
                if (roleId == null) {
                    throw new DataAccessException("ID da role é nulo.");
                }
                stmt.setInt(1, user.getId());  // Define o ID do usuário
                stmt.setInt(2, roleId);  // Define o ID da role
                stmt.executeUpdate();  // Executa a inserção no banco de dados
            }
        } catch (SQLException e) {
            // Lança exceção em caso de erro ao adicionar roles
            throw new DataAccessException("Erro ao adicionar roles ao usuário", e);
        }
    }

    // Método para obter os IDs das roles de um usuário
    public List<Integer> getUserRolesIds(int userId) throws DataAccessException {
        List<Integer> roleIds = new java.util.ArrayList<>();
        
        // Define a consulta SQL para buscar os IDs das roles do usuário
        String sql = "SELECT role_id FROM users_roles WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);  // Define o ID do usuário
            try (ResultSet rs = stmt.executeQuery()) {
                // Itera sobre os resultados e adiciona os IDs das roles na lista
                while (rs.next()) {
                    roleIds.add(rs.getInt("role_id"));
                }
            }
        } catch (SQLException e) {
            // Lança exceção em caso de erro ao buscar roles
            throw new DataAccessException("Erro ao buscar roles do usuário", e);
        }
        
        return roleIds;  // Retorna a lista de IDs das roles
    }
     
    
    
}
