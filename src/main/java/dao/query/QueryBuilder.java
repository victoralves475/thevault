package dao.query;

import java.lang.reflect.Field;

public interface QueryBuilder {
    String buildInsertQuery();
    String buildUpdateQuery();
    String buildDeleteQuery();
    String buildFindByIdQuery();
    String buildFindAllQuery();
    
    String buildFindWithWhere(String whereClause);
    
}
