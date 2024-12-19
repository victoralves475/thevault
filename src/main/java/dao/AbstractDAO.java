package dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; 
import java.util.ArrayList;
import java.util.List;

import annotations.Column;
import annotations.Id;
import dao.exceptions.DataAccessException;
import database.DatabaseConnection;

public abstract class AbstractDAO<T, ID> implements DAO<T, ID> {

    protected DatabaseConnection databaseConnection;
    protected Class<T> entityClass;
    protected String tableName;

    public AbstractDAO(Class<T> entityClass, String tableName) {
        this.databaseConnection = DatabaseConnection.getInstance();
        this.entityClass = entityClass;
        this.tableName = tableName;
    }

    @Override
    public void insert(T entity) throws DataAccessException {

        String sql = "INSERT INTO " + tableName + " (" + getColumnNames() + ") VALUES (" + getPlaceholders() + ")";

        try (Connection coon = databaseConnection.getConnection();
             PreparedStatement stmt = coon.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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

        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
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

        String sql = "SELECT * FROM " + tableName;
        List<T> entities = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                entities.add(mapResultSetToEntity(rs));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Erro ao buscar todas as entidades", e);
        }

        return entities;
    }

    @Override
    public void update(T entity) throws DataAccessException {
        String sql = "UPDATE " + tableName + " SET " + getUpdateSetClause() + " WHERE id = ?";
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
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            stmt.executeUpdate(); // executeQuery para DELETE não é adequado, use executeUpdate
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao deletar entidade", e);
        }
    }

    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
    protected abstract void setStatementParameters(PreparedStatement stmt, T entity) throws SQLException;

    /**
     * Obtém o nome da coluna levando em conta a anotação @Column.
     * Se @Column(name="...") estiver presente, usa esse nome.
     * Caso contrário, usa o nome do campo.
     */
    private String getColumnNameForField(Field field) {
        field.setAccessible(true);
        if (field.isAnnotationPresent(Column.class)) {
            Column col = field.getAnnotation(Column.class);
            if (!col.name().isEmpty()) {
                return col.name();
            }
        }
        return field.getName();
    }

    private String getColumnNames() {
        StringBuilder columns = new StringBuilder();
        for (Field field : entityClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Id.class)) {
                String columnName = getColumnNameForField(field);
                columns.append(columnName).append(", ");
            }
        }
        columns.setLength(columns.length() - 2);
        return columns.toString();
    }

    private String getPlaceholders() {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < getColumnCount(); i++) {
            placeholders.append("?, ");
        }
        placeholders.setLength(placeholders.length() - 2);
        return placeholders.toString();
    }

    private String getUpdateSetClause() {
        StringBuilder setClause = new StringBuilder();
        for (Field field : entityClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Id.class)) {
                String columnName = getColumnNameForField(field);
                setClause.append(columnName).append(" = ?, ");
            }
        }
        setClause.setLength(setClause.length() - 2);
        return setClause.toString();
    }

    private int getColumnCount() {
        return (int) java.util.Arrays.stream(entityClass.getDeclaredFields())
                .filter(f -> !f.isAnnotationPresent(Id.class))
                .count();
    }

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
}

//package dao;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Field;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
//
//import annotations.Id;
//import dao.exceptions.DataAccessException;
//import database.DatabaseConnection;
//
//public abstract class AbstractDAO<T, ID> implements DAO<T, ID> {
//
//	protected DatabaseConnection databaseConnection;
//	protected Class<T> entityClass;
//	protected String tableName;
//
//	public AbstractDAO(Class<T> entityClass, String tableName) {
//
//		this.databaseConnection = DatabaseConnection.getInstance();
//		this.entityClass = entityClass;
//		this.tableName = tableName;
//
//	}
//
//	@Override
//	public void insert(T entity) throws DataAccessException {
//
//		// INSERT INTO NOME_TABELA (NOME_COLUNA, NOME_COLUNA...) VALUES (VALOR,
//		// VALOR...)
//
//		String sql = "INSERT INTO " + tableName + " (" + getColumnNames() + ") VALUES (" + getPlaceholders() + ")";
//
//		try (Connection coon = databaseConnection.getConnection();
//				PreparedStatement stmt = coon.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//
//			setStatementParameters(stmt, entity);
//			stmt.executeUpdate();
//
//			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
//				if (generatedKeys.next()) {
//					setGeneratedId(entity, generatedKeys.getObject(1));
//				}
//			}
//		} catch (SQLException e) {
//			throw new DataAccessException("Erro na inserção", e);
//		}
//
//	}
//
//	@Override
//	public T findById(ID id) throws DataAccessException {
//
//		// SELECT * FROM TABELA WHERE ID = id
//
//		String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
//		try (Connection conn = databaseConnection.getConnection();
//				PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//			stmt.setObject(1, id);
//			ResultSet rs = stmt.executeQuery();
//			if (rs.next()) {
//				return mapResultSetToEntity(rs);
//			}
//		} catch (SQLException e) {
//			throw new DataAccessException("Entitdade com o Id informado não encontrada", e);
//		}
//		return null;
//
//	}
//
//	@Override
//	public List<T> findAll() throws DataAccessException {
//
//		// SELECT * FROM TABELA
//
//		String sql = "SELECT * FROM " + tableName;
//		List<T> entities = new ArrayList<>();
//
//		try (Connection conn = databaseConnection.getConnection();
//				PreparedStatement stmt = conn.prepareStatement(sql);
//				ResultSet rs = stmt.executeQuery()) {
//
//			while (rs.next()) {
//				entities.add(mapResultSetToEntity(rs));
//			}
//
//		} catch (SQLException e) {
//			throw new DataAccessException("Erro ao buscar todas as entidades", e);
//		}
//
//		return entities;
//
//	}
//
//	@Override
//	public void update(T entity) throws DataAccessException {
//		// UPDATE NOME_TABELA SET ISSO WHERE ID = AQUILO
//
//		String sql = "UPDATE " + tableName + " SET " + getUpdateSetClause() + " WHERE id = ?";
//		try (Connection conn = databaseConnection.getConnection();
//				PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//			setStatementParameters(stmt, entity);
//			stmt.setObject(getColumnCount() + 1, getIdValue(entity));
//			stmt.executeUpdate();
//
//		} catch (SQLException e) {
//			throw new DataAccessException("Erro ao atualizar entidade", e);
//		}
//
//	}
//
//	@Override
//	public void delete(ID id) throws DataAccessException {
//
//		// DELETE FROM NOME_TABELA WHERE ID = ?
//		String sql = "DELETE FROM " + tableName + " WHERE id = ?";
//		try (Connection conn = databaseConnection.getConnection();
//				PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//			stmt.setObject(1, id);
//			stmt.executeQuery();
//		} catch (SQLException e) {
//			throw new DataAccessException("Erro ao deletar entidade", e);
//		}
//
//	}
//
//	protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
//
//	protected abstract void setStatementParameters(PreparedStatement stmt, T entity) throws SQLException;
//
//	private String getColumnNames() {
//		StringBuilder columns = new StringBuilder();
//		for (Field field : entityClass.getDeclaredFields()) {
//			if (!field.isAnnotationPresent((Class<? extends Annotation>) Id.class)) {
//				columns.append(field.getName()).append(", ");
//			}
//		}
//		return columns.substring(0, columns.length() - 2);
//	}
//
//	private String getPlaceholders() {
//		StringBuilder placeholders = new StringBuilder();
//		for (int i = 0; i < getColumnCount(); i++) {
//			placeholders.append("?, ");
//		}
//		return placeholders.substring(0, placeholders.length() - 2);
//	}
//
//	private String getUpdateSetClause() {
//		StringBuilder setClause = new StringBuilder();
//		for (Field field : entityClass.getDeclaredFields()) {
//			if (!field.isAnnotationPresent(Id.class)) {
//				setClause.append(field.getName()).append(" = ?, ");
//			}
//		}
//		return setClause.substring(0, setClause.length() - 2);
//	}
//
//	private int getColumnCount() {
//		return (int) java.util.Arrays.stream(entityClass.getDeclaredFields())
//				.filter(f -> !f.isAnnotationPresent(Id.class)).count();
//	}
//
//	private Object getIdValue(T entity) {
//		try {
//			Field idField = entityClass.getDeclaredField("id");
//			idField.setAccessible(true);
//			return idField.get(entity);
//		} catch (NoSuchFieldException | IllegalAccessException e) {
//			throw new RuntimeException("Erro ao obter o valor do ID", e);
//		}
//	}
//
//	private void setGeneratedId(T entity, Object id) {
//		try {
//			Field idField = entity.getClass().getDeclaredField("id");
//			idField.setAccessible(true);
//			idField.set(entity, id);
//		} catch (NoSuchFieldException | IllegalAccessException e) {
//			throw new RuntimeException("Erro ao definir ID gerado", e);
//		}
//	}
//}
