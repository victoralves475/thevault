package metadata.extractor;

public class PostgresTypeMapping implements TypeMappingStrategy {

	@Override
	public String mapJavaTypeToSQL(Class<?> javaType) {
		
		if (javaType.equals(String.class)) {
			return "varchar(255)";
		} 
		else if (javaType.equals(Integer.class) || javaType.equals(int.class)) {
			return "INTEGER";
		} 
		else if (javaType.equals(Double.class) || javaType.equals(double.class) || javaType.equals(Float.class)
				|| javaType.equals(float.class)) {
			return "REAL";
		} 
		else if (javaType.equals(Boolean.class) || javaType.equals(boolean.class)) {
			return "BOOLEAN";
		} 
		else if (javaType.equals(java.util.Date.class) || javaType.equals(java.sql.Date.class)) {
			return "DATE";
		}
		
		throw new RuntimeException("Tipo não suportado: " + javaType.getName());
	}

}
