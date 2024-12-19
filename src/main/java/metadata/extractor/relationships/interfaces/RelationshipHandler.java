package metadata.extractor.relationships.interfaces;

import java.lang.reflect.Field;

import metadata.TableMetadata;
import metadata.extractor.MetadataExtractor;

public interface RelationshipHandler {

	boolean handleRelationship(Field field, TableMetadata tm, MetadataExtractor extractor);
	
}
