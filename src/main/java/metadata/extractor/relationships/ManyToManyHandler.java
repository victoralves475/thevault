package metadata.extractor.relationships;

import java.lang.reflect.Field;

import annotations.relationships.ManyToMany;
import metadata.JoinTableMetadata;
import metadata.TableMetadata;
import metadata.extractor.MetadataExtractor;
import metadata.extractor.relationships.interfaces.RelationshipHandler;

public class ManyToManyHandler implements RelationshipHandler {
	
	@Override
	public boolean handleRelationship(Field field, TableMetadata tm, MetadataExtractor extractor) {
		ManyToMany manyToManyAnn = field .getAnnotation(ManyToMany.class);
		if (manyToManyAnn == null) {
			return false; //Se não for ManyToMany
		}
		
		Class<?> target = manyToManyAnn.targetEntity();
		String joinTable = manyToManyAnn.joinTable();
		String joinColumn = manyToManyAnn.joinColumn();
		String inverseJoinColumn = manyToManyAnn.inverseJoinColumn();
		
		TableMetadata targetTM = extractor.extractMetadata(target);
		
		JoinTableMetadata joinTM = new JoinTableMetadata();
		joinTM.setJoinTableName(joinTable);
		joinTM.setSourceTable(tm.getTableName());
		joinTM.setTargetTable(targetTM.getTableName());
		joinTM.setSourceJoinColumn(joinColumn);
		joinTM.setTargetJoinColumn(inverseJoinColumn);
		
		tm.addJoinTable(joinTM);
		
		return true;
		
	}

}
