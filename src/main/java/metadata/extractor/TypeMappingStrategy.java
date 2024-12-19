package metadata.extractor;

public interface TypeMappingStrategy {

	String mapJavaTypeToSQL(Class<?> javaType);

}
