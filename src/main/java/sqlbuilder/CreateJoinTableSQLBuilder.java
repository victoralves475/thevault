package sqlbuilder;

import metadata.JoinTableMetadata;

public class CreateJoinTableSQLBuilder {

    private JoinTableMetadata jt;  // Metadados da tabela de junção

    // Construtor que recebe os metadados da tabela de junção
    public CreateJoinTableSQLBuilder(JoinTableMetadata jt) {
        this.jt = jt;
    }

    // Método para construir a SQL de criação da tabela de junção
    public String build() {
        // StringBuilder para construir a SQL
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        
        sb.append(jt.getJoinTableName()).append(" (");  // Nome da tabela de junção
        sb.append(jt.getSourceJoinColumn()).append(" INTEGER NOT NULL, ");  // Coluna de junção da entidade de origem
        sb.append(jt.getTargetJoinColumn()).append(" INTEGER NOT NULL, ");  // Coluna de junção da entidade de destino
        
        // Adiciona a restrição de chave estrangeira para a entidade de origem
        sb.append("FOREIGN KEY(").append(jt.getSourceJoinColumn()).append(") REFERENCES ")
          .append(jt.getSourceTable()).append("(id), ");
        
        // Adiciona a restrição de chave estrangeira para a entidade de destino
        sb.append("FOREIGN KEY(").append(jt.getTargetJoinColumn()).append(") REFERENCES ")
          .append(jt.getTargetTable()).append("(id), ");
        
        // Define a chave primária composta pelas colunas de junção
        sb.append("PRIMARY KEY(").append(jt.getSourceJoinColumn()).append(", ")
          .append(jt.getTargetJoinColumn()).append(")");
        
        sb.append(");");  // Fecha a SQL de criação da tabela
        
        return sb.toString();  // Retorna a SQL gerada
    }
}
