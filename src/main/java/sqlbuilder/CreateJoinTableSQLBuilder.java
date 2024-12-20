package sqlbuilder;

import metadata.JoinTableMetadata;

public class CreateJoinTableSQLBuilder {

    private JoinTableMetadata jt;

    public CreateJoinTableSQLBuilder(JoinTableMetadata jt) {
        this.jt = jt;
    }

    public String build() {
        // Assumindo que as PKs das entidades são id do tipo INTEGER
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append(jt.getJoinTableName()).append(" (");
        sb.append(jt.getSourceJoinColumn()).append(" INTEGER NOT NULL, ");
        sb.append(jt.getTargetJoinColumn()).append(" INTEGER NOT NULL, ");
        sb.append("FOREIGN KEY(").append(jt.getSourceJoinColumn()).append(") REFERENCES ")
          .append(jt.getSourceTable()).append("(id), ");
        sb.append("FOREIGN KEY(").append(jt.getTargetJoinColumn()).append(") REFERENCES ")
          .append(jt.getTargetTable()).append("(id), ");
        sb.append("PRIMARY KEY(").append(jt.getSourceJoinColumn()).append(", ")
          .append(jt.getTargetJoinColumn()).append(")");
        sb.append(");");
        return sb.toString();
    }
}
