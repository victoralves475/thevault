package dao;

import java.util.List;

import dao.exceptions.DataAccessException;

public interface DAO <T, ID> {

	void insert(T entity) throws DataAccessException;
	void update(T entity) throws DataAccessException;
	void delete(ID id) throws DataAccessException;
	List<T> findAll() throws DataAccessException;
	T findById(ID id) throws DataAccessException;
	
}
