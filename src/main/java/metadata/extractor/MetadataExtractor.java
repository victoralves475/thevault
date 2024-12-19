package metadata.extractor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import annotations.Column;
import annotations.Entity;
import annotations.Id;
import metadata.ColumnMetadata;
import metadata.TableMetadata;
import metadata.extractor.relationships.interfaces.RelationshipHandler;

public class MetadataExtractor {

	private final TypeMappingStrategy typeMapping;
	private final List<RelationshipHandler> relationshipHandlers;

	public MetadataExtractor(TypeMappingStrategy typeMapping, List<RelationshipHandler> relationshipHandlers) {
		this.typeMapping = typeMapping;
		this.relationshipHandlers = relationshipHandlers;
	}

	public TableMetadata extractMetadata(Class<?> entityClass) {
		if (!entityClass.isAnnotationPresent(Entity.class)) {
			throw new IllegalArgumentException("Classe " + entityClass.getName() + "não está anotada como '@Entity'");
		}

		Entity entityAnn = entityClass.getAnnotation(Entity.class);
		TableMetadata tm = new TableMetadata();
		tm.setTableName(entityAnn.tableName());

		for (Field field : entityClass.getDeclaredFields()) {
			field.setAccessible(true);
			boolean handled = false;
			for (RelationshipHandler handler : relationshipHandlers) {
				if (handler.handleRelationship(field, tm, this)) {
					handled = true;
					break;
				}
			}
			if (!handled) {
				ColumnMetadata cm = createColumnMetadata(field);
				if (cm != null) {
					tm.addColumn(cm);
					if (cm.isPrimaryKey()) {
						tm.setPrimaryKey(cm);
					}
				}
			}
		}

		/*
		 * Arrays.stream(entityClass.getDeclaredFields()).forEach(field -> {
		 * ColumnMetadata cm = createColumnMetadata(field); if (cm != null) {
		 * tm.addColumn(cm); if (cm.isPrimaryKey()) { tm.setPrimaryKey(cm); } } });
		 */

		return tm;
	}

	private ColumnMetadata createColumnMetadata(Field field) {
		boolean isId = field.isAnnotationPresent(Id.class);
		Column col = field.getAnnotation(Column.class);

		if (!isId && col == null) {
			// Não é coluna nem PK, nem relacionamento - ignorar
			return null;
		}

		String columnName = field.getName();
		if (col != null && !col.name().isEmpty()) {
			columnName = col.name();
		}

		String sqlType;
		if (col != null && !col.type().isEmpty()) {
			sqlType = col.type();
		} else {
			sqlType = typeMapping.mapJavaTypeToSQL(field.getType());
		}

		ColumnMetadata cm = new ColumnMetadata();
		cm.setName(columnName);
		cm.setSqlType(sqlType);
		cm.setPrimaryKey(isId);
		cm.setNullable(col == null || col.nullable());
		return cm;
	}
	/*
	 * field.setAccessible(true); String columnName = field.getName(); boolean
	 * primaryKey = field.isAnnotationPresent(Id.class);
	 * 
	 * Column col = field.getAnnotation(Column.class); if (col != null &&
	 * !col.name().isEmpty()) { columnName = col.name(); }
	 * 
	 * String sqlType; if (col != null && !col.type().isEmpty()) { sqlType =
	 * col.type(); } else { sqlType = typeMapping.mapJavaTypeToSQL(field.getType());
	 * }
	 * 
	 * ColumnMetadata cm = new ColumnMetadata(); cm.setName(columnName);
	 * cm.setSqlType(sqlType); cm.setPrimaryKey(primaryKey); cm.setNullable(col ==
	 * null || col.nullable());
	 * 
	 * return cm; }
	 */

}
