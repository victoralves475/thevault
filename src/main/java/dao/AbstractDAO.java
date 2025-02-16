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

    protected DatabaseConnection databaseConnection;  // Conexão com o banco de dados
    protected Class<T> entityClass;  // Classe da entidade
    protected String tableName;  // Nome da tabela
    protected QueryBuilder queryBuilder;  // Construtor de consultas SQL

    public AbstractDAO(Class<T> entityClass, String tableName) {
        // Inicializa a conexão, a classe da entidade, o nome da tabela e o construtor de consultas
        this.databaseConnection = DatabaseConnection.getInstance();
        this.entityClass = entityClass;
        this.tableName = tableName;
        this.queryBuilder = new DefaultQueryBuilder(entityClass, tableName);
    }

    @Override
    public void insert(T entity) throws DataAccessException {
        // Validar campos antes da inserção
        try {
            Validator.validateNotBlankFields(entity);  // Valida se os campos não estão em branco
        } catch (ValidationException ve) {
            // Caso ocorra erro na validação, lança uma exceção
            throw new DataAccessException("Validação falhou: " + ve.getMessage(), ve);
        }

        String sql = queryBuilder.buildInsertQuery();  // Constrói a query de inserção

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setStatementParameters(stmt, entity);  // Define os parâmetros para a query de inserção
            stmt.executeUpdate();  // Executa a inserção

            // Obtém o ID gerado automaticamente (caso exista)
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setGeneratedId(entity, generatedKeys.getObject(1));  // Define o ID gerado na entidade
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erro na inserção", e);  // Lança exceção em caso de erro na inserção
        }
    }

    @Override
    public T findById(ID id) throws DataAccessException {
        String sql = queryBuilder.buildFindByIdQuery();  // Constrói a query para buscar por ID
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);  // Define o ID na query
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);  // Mapeia o resultado da consulta para a entidade
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Entidade com o Id informado não encontrada", e);  // Lança exceção em caso de erro
        }
        return null;  // Retorna null caso a entidade não seja encontrada
    }

    @Override
    public List<T> findAll() throws DataAccessException {
        String sql = queryBuilder.buildFindAllQuery();  // Constrói a query para buscar todas as entidades
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<T> entities = new java.util.ArrayList<>();
            while (rs.next()) {
                entities.add(mapResultSetToEntity(rs));  // Mapeia os resultados para as entidades
            }
            return entities;  // Retorna a lista de entidades

        } catch (SQLException e) {
            throw new DataAccessException("Erro ao buscar todas as entidades", e);  // Lança exceção em caso de erro
        }
    }

    @Override
    public void update(T entity) throws DataAccessException {
        // Validar campos antes do update também
        try {
            Validator.validateNotBlankFields(entity);  // Valida se os campos não estão em branco
        } catch (ValidationException ve) {
            // Caso ocorra erro na validação, lança uma exceção
            throw new DataAccessException("Validação falhou: " + ve.getMessage(), ve);
        }

        String sql = queryBuilder.buildUpdateQuery();  // Constrói a query de atualização
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStatementParameters(stmt, entity);  // Define os parâmetros para a query de atualização
            stmt.setObject(getColumnCount() + 1, getIdValue(entity));  // Define o valor do ID na query
            stmt.executeUpdate();  // Executa a atualização

        } catch (SQLException e) {
            throw new DataAccessException("Erro ao atualizar entidade", e);  // Lança exceção em caso de erro
        }
    }

    @Override
    public void delete(ID id) throws DataAccessException {
        String sql = queryBuilder.buildDeleteQuery();  // Constrói a query de exclusão
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);  // Define o ID na query
            stmt.executeUpdate();  // Executa a exclusão
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao deletar entidade", e);  // Lança exceção em caso de erro
        }
    }

    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;  // Método abstrato para mapear o ResultSet para a entidade
    protected abstract void setStatementParameters(PreparedStatement stmt, T entity) throws SQLException;  // Método abstrato para definir os parâmetros da query

    // Métodos auxiliares para ID
    private Object getIdValue(T entity) {
        try {
            Field idField = entityClass.getDeclaredField("id");  // Obtém o campo 'id' da entidade
            idField.setAccessible(true);  // Torna o campo acessível
            return idField.get(entity);  // Retorna o valor do ID
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Erro ao obter o valor do ID", e);  // Lança exceção em caso de erro
        }
    }

    private void setGeneratedId(T entity, Object id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");  // Obtém o campo 'id' da entidade
            idField.setAccessible(true);  // Torna o campo acessível
            idField.set(entity, id);  // Define o valor do ID na entidade
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Erro ao definir ID gerado", e);  // Lança exceção em caso de erro
        }
    }

    protected int getColumnCount() {
        return (int) Arrays.stream(entityClass.getDeclaredFields())  // Conta o número de campos na entidade
                .filter(f -> !f.isAnnotationPresent(Id.class) && f.isAnnotationPresent(Column.class))  // Filtra apenas os campos anotados com @Column, ignorando os anotados com @Id
                .count();  // Retorna a quantidade de campos
    }
}
