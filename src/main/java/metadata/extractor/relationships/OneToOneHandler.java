package metadata.extractor.relationships;

import java.lang.reflect.Field;

import annotations.Column;
import annotations.relationships.OneToOne;
import metadata.ColumnMetadata;
import metadata.ForeignKeyMetadata;
import metadata.TableMetadata;
import metadata.extractor.MetadataExtractor;
import metadata.extractor.relationships.interfaces.RelationshipHandler;

public class OneToOneHandler implements RelationshipHandler{
	
	@Override
	public boolean handleRelationship(Field field, TableMetadata tm, MetadataExtractor extractor) {
		OneToOne oneToOneAnn = field.getAnnotation(OneToOne.class);
		if (oneToOneAnn == null) {
			return false;
		}
		
		Class<?> target = oneToOneAnn.targetEntity();
		
		TableMetadata relatedTM = extractor.extractMetadata(target);
		String refColumn = relatedTM.getPrimaryKey().getName();
		String refTable = relatedTM.getTableName();
		
		String fkName = oneToOneAnn.foreignKeyName();
		if (fkName.isEmpty()) {
			fkName = field.getName() + "_id";
		}
		
		Column col = field.getAnnotation(Column.class);
		
		String sqlType = (col != null && !col.type().isEmpty()) ? col.type(): "INTEGER";

		ColumnMetadata fkCol = new ColumnMetadata();
		fkCol.setName(fkName);
		fkCol.setSqlType(sqlType);
		fkCol.setPrimaryKey(false);
		fkCol.setNullable(col == null || col.nullable());
		
		if (!oneToOneAnn.optional()) {
			fkCol.setNullable(false);
		}
		
		tm.addColumn(fkCol);
		
		ForeignKeyMetadata fk = new ForeignKeyMetadata();
		fk.setColumnName(fkName);
		fk.setReferecedColumn(refColumn);
		fk.setReferecedTable(refTable);
		tm.addForeignKey(fk);
		
		return true;
	}

}
