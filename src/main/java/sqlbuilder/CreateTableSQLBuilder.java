package sqlbuilder;

import metadata.ColumnMetadata;
import metadata.TableMetadata;

public class CreateTableSQLBuilder {

	private TableMetadata tableMetadata;

	public CreateTableSQLBuilder(TableMetadata tm) {
		this.tableMetadata = tm;
	}

	public String build() {
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(tableMetadata.getTableName()).append(" (");

		for (ColumnMetadata cm : tableMetadata.getColumns()) {
			// Nome da coluna e tipo primeiro
			sb.append(cm.getName()).append(" ").append(cm.getSqlType());

			// Se não pode ser nulo
			if (!cm.isNullable()) {
				sb.append(" NOT NULL");
			}

			// Se é chave primária
			if (cm.isPrimaryKey()) {
				sb.append(" PRIMARY KEY");
			}

			sb.append(", ");
		}

		// Remover a última ", "
		sb.setLength(sb.length() - 2);
		sb.append(");");
		return sb.toString();
	}
}
