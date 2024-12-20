package schema;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import database.DatabaseConnection;
import metadata.JoinTableMetadata;
import metadata.TableMetadata;
import metadata.extractor.MetadataExtractor;
import sqlbuilder.CreateJoinTableSQLBuilder;
import sqlbuilder.CreateTableSQLBuilder;

public class SchemaGenerator {

    private MetadataExtractor extractor;

    public SchemaGenerator(MetadataExtractor extractor) {
        this.extractor = extractor;
    }

    public void createTableIfNotExists(Class<?> entityClass) {
        // Extrair metadados da entidade
        TableMetadata tm = extractor.extractMetadata(entityClass);

        // Criar a tabela principal, se não existir
        if (!tableExists(tm.getTableName())) {
            String createSQL = new CreateTableSQLBuilder(tm).build();
            executeUpdate(createSQL);
        }

        // Criar as tabelas de junção (ManyToMany), se houver
        for (JoinTableMetadata jtm : tm.getJoinTables()) {
            if (!tableExists(jtm.getJoinTableName())) {
                String joinSql = new CreateJoinTableSQLBuilder(jtm).build();
                executeUpdate(joinSql);
            }
        }
    }

    private boolean tableExists(String tableName) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, null, tableName.toLowerCase(), null)) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência da tabela " + tableName, e);
        }
    }

    private void executeUpdate(String sql) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela. SQL: " + sql, e);
        }
    }
}
