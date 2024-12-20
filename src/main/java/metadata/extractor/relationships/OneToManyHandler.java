package metadata.extractor.relationships;

import java.lang.reflect.Field;

import annotations.relationships.ManyToOne;
import annotations.relationships.OneToMany;
import metadata.TableMetadata;
import metadata.extractor.MetadataExtractor;
import metadata.extractor.relationships.interfaces.RelationshipHandler;

public class OneToManyHandler implements RelationshipHandler{

		@Override
		public boolean handleRelationship(Field field, TableMetadata tm, MetadataExtractor extractor) {
			OneToMany oneToManyAnn = field.getAnnotation(OneToMany.class);
			if (oneToManyAnn == null) {
				return false;
				
			}
			
			Class<?> target = oneToManyAnn.targetEntity();
			String mappedBy = oneToManyAnn.mappedBy();
			
			TableMetadata manySideTM = extractor.extractMetadata(target);
			
			boolean found = false;
			for (Field f : target.getDeclaredFields()) {
				ManyToOne mto = f.getAnnotation(ManyToOne.class);
				//Verificar o tm.getClass (gpt sugeriu o tm.getEntityClass)
				if (mto != null && mto.targetEntity().equals(tm.getEntityClass())) {
					if (f.getName().equals(mappedBy)) {
						found = true;
						break;
					}
				}
				
				
			}
			
			if (!found) {
				throw new RuntimeException("Relacionamento @OneToMany inconsistente: " + "não foi encontrado o campo '"+ mappedBy + "' com @ ManyToOne apontando para " + tm.getTableName() + " na entidade " + target.getName());
			}

			return true;
			
		}
	
}
