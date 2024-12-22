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

    private MetadataExtractor extractor;  // Extrator de metadados que gera informações sobre as entidades

    // Construtor que inicializa o extrator de metadados
    public SchemaGenerator(MetadataExtractor extractor) {
        this.extractor = extractor;
    }

    // Método para criar a tabela no banco de dados, se não existir
    public void createTableIfNotExists(Class<?> entityClass) {
        // Extrai os metadados da entidade para obter informações sobre a tabela e colunas
        TableMetadata tm = extractor.extractMetadata(entityClass);

        // Verifica se a tabela já existe. Se não, cria a tabela principal.
        if (!tableExists(tm.getTableName())) {
            String createSQL = new CreateTableSQLBuilder(tm).build();  // Constrói a SQL para criação da tabela
            executeUpdate(createSQL);  // Executa a criação da tabela
        }

        // Cria as tabelas de junção para relacionamentos ManyToMany, se necessário
        for (JoinTableMetadata jtm : tm.getJoinTables()) {
            // Verifica se a tabela de junção já existe, caso contrário, cria
            if (!tableExists(jtm.getJoinTableName())) {
                String joinSql = new CreateJoinTableSQLBuilder(jtm).build();  // Constrói a SQL para criação da tabela de junção
                executeUpdate(joinSql);  // Executa a criação da tabela de junção
            }
        }
    }

    // Método para verificar se uma tabela existe no banco de dados
    private boolean tableExists(String tableName) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();  // Obtém os metadados do banco de dados
            try (ResultSet rs = meta.getTables(null, null, tableName.toLowerCase(), null)) {
                return rs.next();  // Retorna true se a tabela existir
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência da tabela " + tableName, e);  // Lança exceção em caso de erro
        }
    }

    // Método para executar comandos SQL de atualização, como criação de tabelas
    private void executeUpdate(String sql) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);  // Executa a SQL de atualização
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela. SQL: " + sql, e);  // Lança exceção em caso de erro ao executar o SQL
        }
    }
}
