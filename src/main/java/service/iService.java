package service;

import java.sql.SQLException;
import java.util.List;

public interface iService <T> {
    void ajouter(T t) throws SQLException;
    void modifier(T t) throws SQLException;
    void supprimer(T t) throws SQLException;
    List<T> getA() throws SQLException;
}
