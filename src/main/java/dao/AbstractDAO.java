package dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import annotations.Column;
import annotations.Id;
import dao.exceptions.DataAccessException;
import dao.query.DefaultQueryBuilder;
import dao.query.QueryBuilder;
import database.DatabaseConnection;
import validation.Validator;
import validation.exceptions.ValidationException;

public abstract class AbstractDAO<T, ID> implements DAO<T, ID> {

    protected DatabaseConnection databaseConnection;
    protected Class<T> entityClass;
    protected String tableName;
    protected QueryBuilder queryBuilder;

    public AbstractDAO(Class<T> entityClass, String tableName) {
        this.databaseConnection = DatabaseConnection.getInstance();
        this.entityClass = entityClass;
        this.tableName = tableName;
        this.queryBuilder = new DefaultQueryBuilder(entityClass, tableName);
    }

    @Override
    public void insert(T entity) throws DataAccessException {
        // Validar campos antes da inserção
        try {
            Validator.validateNotBlankFields(entity);
        } catch (ValidationException | javax.xml.bind.ValidationException ve) {
            throw new DataAccessException("Validação falhou: " + ve.getMessage(), ve);
        }

        String sql = queryBuilder.buildInsertQuery();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setStatementParameters(stmt, entity);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setGeneratedId(entity, generatedKeys.getObject(1));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erro na inserção", e);
        }
    }

    @Override
    public T findById(ID id) throws DataAccessException {
        String sql = queryBuilder.buildFindByIdQuery();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Entidade com o Id informado não encontrada", e);
        }
        return null;
    }

    @Override
    public List<T> findAll() throws DataAccessException {
        String sql = queryBuilder.buildFindAllQuery();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<T> entities = new java.util.ArrayList<>();
            while (rs.next()) {
                entities.add(mapResultSetToEntity(rs));
            }
            return entities;

        } catch (SQLException e) {
            throw new DataAccessException("Erro ao buscar todas as entidades", e);
        }
    }

    @Override
    public void update(T entity) throws DataAccessException {
        // Validar campos antes do update também
        try {
            Validator.validateNotBlankFields(entity);
        } catch (ValidationException | javax.xml.bind.ValidationException ve) {
            throw new DataAccessException("Validação falhou: " + ve.getMessage(), ve);
        }

        String sql = queryBuilder.buildUpdateQuery();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStatementParameters(stmt, entity);
            stmt.setObject(getColumnCount() + 1, getIdValue(entity));
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Erro ao atualizar entidade", e);
        }
    }

    @Override
    public void delete(ID id) throws DataAccessException {
        String sql = queryBuilder.buildDeleteQuery();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao deletar entidade", e);
        }
    }

    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
    protected abstract void setStatementParameters(PreparedStatement stmt, T entity) throws SQLException;

    // Métodos auxiliares para ID
    private Object getIdValue(T entity) {
        try {
            Field idField = entityClass.getDeclaredField("id");
            idField.setAccessible(true);
            return idField.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Erro ao obter o valor do ID", e);
        }
    }

    private void setGeneratedId(T entity, Object id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Erro ao definir ID gerado", e);
        }
    }

    protected int getColumnCount() {
        return (int) Arrays.stream(entityClass.getDeclaredFields())
                .filter(f -> !f.isAnnotationPresent(Id.class) && f.isAnnotationPresent(Column.class))
                .count();
    }
}
