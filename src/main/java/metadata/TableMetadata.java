package metadata;

import java.util.ArrayList;
import java.util.List;

public class TableMetadata {

    private String tableName;
    private ColumnMetadata primaryKey;
    private List<ColumnMetadata> columns = new ArrayList<>();
    private List<ForeignKeyMetadata> foreignKeys = new ArrayList<>();
    private List<JoinTableMetadata> joinTables = new ArrayList<>();

    // Novo campo para armazenar a classe da entidade
    private Class<?> entityClass;

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public ColumnMetadata getPrimaryKey() {
        return this.primaryKey;
    }

    public void setPrimaryKey(ColumnMetadata primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<ColumnMetadata> getColumns() {
        return this.columns;
    }

    public void addColumn(ColumnMetadata column) {
        this.columns.add(column);
    }

    public List<ForeignKeyMetadata> getForeignKeys() {
        return this.foreignKeys;
    }

    public void addForeignKey(ForeignKeyMetadata fk) {
        foreignKeys.add(fk);
    }
    
    public List<JoinTableMetadata> getJoinTables() {
        return joinTables;
    }
    
    public void addJoinTable(JoinTableMetadata jt) {
        joinTables.add(jt);
    }

    // Getter e Setter para entityClass
    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }
}
