package metadata.extractor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotations.Column;
import annotations.Entity;
import annotations.Id;
import metadata.ColumnMetadata;
import metadata.TableMetadata;
import metadata.extractor.relationships.interfaces.RelationshipHandler;

public class MetadataExtractor {

    private final TypeMappingStrategy typeMapping;  // Estratégia de mapeamento de tipos Java para tipos SQL
    private final List<RelationshipHandler> relationshipHandlers;  // Lista de manipuladores de relacionamentos

    // Cache para armazenar metadados de entidades já processadas, evitando reprocessamento
    private Map<Class<?>, TableMetadata> metadataCache = new HashMap<>();

    public MetadataExtractor(TypeMappingStrategy typeMapping, List<RelationshipHandler> relationshipHandlers) {
        // Inicializa com a estratégia de mapeamento e a lista de manipuladores de relacionamentos
        this.typeMapping = typeMapping;
        this.relationshipHandlers = relationshipHandlers;
    }

    public TableMetadata extractMetadata(Class<?> entityClass) {
        // Verifica se os metadados já foram extraídos para esta classe. Se sim, retorna o cache.
        if (metadataCache.containsKey(entityClass)) {
            return metadataCache.get(entityClass);
        }

        // Verifica se a classe está anotada com @Entity, caso contrário, lança uma exceção
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Classe " + entityClass.getName() + " não está anotada como '@Entity'");
        }

        // Obtém a anotação @Entity para pegar o nome da tabela
        Entity entityAnn = entityClass.getAnnotation(Entity.class);
        TableMetadata tm = new TableMetadata();  // Cria um objeto de metadados de tabela
        tm.setEntityClass(entityClass);  // Opcional: Guarda a classe da entidade
        tm.setTableName(entityAnn.tableName());  // Define o nome da tabela

        // Armazena no cache antes de processar os campos para evitar loops
        metadataCache.put(entityClass, tm);

        // Processa todos os campos da classe para extrair os metadados de colunas e relacionamentos
        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);  // Torna o campo acessível, mesmo que seja privado
            boolean handled = false;  // Flag para verificar se o campo foi tratado como relacionamento
            for (RelationshipHandler handler : relationshipHandlers) {
                // Tenta tratar o campo como um relacionamento
                if (handler.handleRelationship(field, tm, this)) {
                    handled = true;  // Marca como tratado
                    break;
                }
            }
            if (!handled) {
                // Se o campo não for tratado como relacionamento, trata como coluna
                ColumnMetadata cm = createColumnMetadata(field);
                if (cm != null) {
                    tm.addColumn(cm);  // Adiciona os metadados da coluna à tabela
                    if (cm.isPrimaryKey()) {
                        tm.setPrimaryKey(cm);  // Define a chave primária, caso seja o caso
                    }
                }
            }
        }

        return tm;  // Retorna os metadados da tabela
    }

    private ColumnMetadata createColumnMetadata(Field field) {
        // Verifica se o campo é anotado com @Id (para indicar chave primária)
        boolean isId = field.isAnnotationPresent(Id.class);
        // Obtém a anotação @Column, se presente, para configurar a coluna no banco de dados
        Column col = field.getAnnotation(Column.class);

        // Se não for uma coluna, chave primária ou relacionamento, ignora o campo
        if (!isId && col == null) {
            return null;
        }

        // Obtém o nome da coluna, se especificado na anotação @Column
        String columnName = field.getName();
        if (col != null && !col.name().isEmpty()) {
            columnName = col.name();
        }

        // Mapeia o tipo Java para o tipo SQL correspondente
        String sqlType;
        if (col != null && !col.type().isEmpty()) {
            sqlType = col.type();  // Tipo SQL explicitamente definido na anotação
        } else {
            sqlType = typeMapping.mapJavaTypeToSQL(field.getType());  // Mapeia automaticamente o tipo Java para SQL
        }

        // Cria um objeto de metadados da coluna e define suas propriedades
        ColumnMetadata cm = new ColumnMetadata();
        cm.setName(columnName);  // Nome da coluna no banco de dados
        cm.setSqlType(sqlType);  // Tipo SQL da coluna
        cm.setPrimaryKey(isId);  // Se for a chave primária
        cm.setNullable(col == null || col.nullable());  // Se a coluna pode ser nula, ou se a anotação não especifica
        return cm;  // Retorna os metadados da coluna
    }
}
