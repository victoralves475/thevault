package dao.query;

import java.lang.reflect.Field;
import java.util.Arrays;

import annotations.Column;
import annotations.Id;

public class DefaultQueryBuilder implements QueryBuilder {

    private Class<?> entityClass;
    private String tableName;

    public DefaultQueryBuilder(Class<?> entityClass, String tableName) {
        this.entityClass = entityClass;
        this.tableName = tableName;
    }
    
    @Override
    public String buildFindWithWhere(String whereClause) {
        // Ex: "SELECT * FROM tableName WHERE " + whereClause
        return "SELECT * FROM " + tableName + " WHERE " + whereClause;
    }

    @Override
    public String buildInsertQuery() {
        return "INSERT INTO " + tableName + " (" + getColumnNames() + ") VALUES (" + getPlaceholders() + ")";
    }

    @Override
    public String buildUpdateQuery() {
        return "UPDATE " + tableName + " SET " + getUpdateSetClause() + " WHERE id = ?";
    }

    @Override
    public String buildDeleteQuery() {
        return "DELETE FROM " + tableName + " WHERE id = ?";
    }

    @Override
    public String buildFindByIdQuery() {
        return "SELECT * FROM " + tableName + " WHERE id = ?";
    }

    @Override
    public String buildFindAllQuery() {
        return "SELECT * FROM " + tableName;
    }

    private boolean isColumnField(Field field) {
        return !field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(Column.class);
    }

    private String getColumnNameForField(Field field) {
        field.setAccessible(true);
        Column col = field.getAnnotation(Column.class);
        if (col != null && !col.name().isEmpty()) {
            return col.name();
        }
        return field.getName();
    }

    private String getColumnNames() {
        StringBuilder columns = new StringBuilder();
        for (Field field : entityClass.getDeclaredFields()) {
            if (isColumnField(field)) {
                String columnName = getColumnNameForField(field);
                columns.append(columnName).append(", ");
            }
        }
        if (columns.length() > 2) {
            columns.setLength(columns.length() - 2);
        }
        return columns.toString();
    }

    private String getPlaceholders() {
        StringBuilder placeholders = new StringBuilder();
        int count = getColumnCount();
        for (int i = 0; i < count; i++) {
            placeholders.append("?, ");
        }
        if (placeholders.length() > 2) {
            placeholders.setLength(placeholders.length() - 2);
        }
        return placeholders.toString();
    }

    private String getUpdateSetClause() {
        StringBuilder setClause = new StringBuilder();
        for (Field field : entityClass.getDeclaredFields()) {
            if (isColumnField(field)) {
                String columnName = getColumnNameForField(field);
                setClause.append(columnName).append(" = ?, ");
            }
        }
        if (setClause.length() > 2) {
            setClause.setLength(setClause.length() - 2);
        }
        return setClause.toString();
    }

    private int getColumnCount() {
        return (int) Arrays.stream(entityClass.getDeclaredFields())
                .filter(this::isColumnField)
                .count();
    }
}
