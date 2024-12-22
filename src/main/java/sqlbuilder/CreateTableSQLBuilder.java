package sqlbuilder;

import metadata.ColumnMetadata;
import metadata.TableMetadata;

public class CreateTableSQLBuilder {

    private TableMetadata tableMetadata;  // Metadados da tabela que contêm as colunas e suas propriedades

    // Construtor que recebe os metadados da tabela
    public CreateTableSQLBuilder(TableMetadata tm) {
        this.tableMetadata = tm;
    }

    // Método para construir a SQL de criação da tabela
    public String build() {
        // StringBuilder para construir a consulta SQL de criação da tabela
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        
        sb.append(tableMetadata.getTableName()).append(" (");  // Nome da tabela

        // Itera sobre as colunas da tabela e adiciona as informações de cada uma
        for (ColumnMetadata cm : tableMetadata.getColumns()) {
            // Nome da coluna e tipo de dado (SQL)
            sb.append(cm.getName()).append(" ").append(cm.getSqlType());

            // Se a coluna não pode ser nula, adiciona a restrição NOT NULL
            if (!cm.isNullable()) {
                sb.append(" NOT NULL");
            }

            // Se a coluna for uma chave primária, adiciona a restrição PRIMARY KEY
            if (cm.isPrimaryKey()) {
                sb.append(" PRIMARY KEY");
            }

            sb.append(", ");  // Adiciona uma vírgula entre as definições das colunas
        }

        // Remove a última vírgula e espaço adicionados após a última coluna
        sb.setLength(sb.length() - 2);

        // Fecha a definição da tabela
        sb.append(");");

        return sb.toString();  // Retorna a SQL gerada para criar a tabela
    }
}
