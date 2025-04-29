package services;

import java.sql.SQLException;
import java.util.List;

public interface IService <T>{
    void add(T t) throws SQLException;
    void update(T t);
    void delete(T t);
    List<T> getAll() throws SQLException;



}
