package service;

import java.sql.SQLException;
import java.util.List;

public interface iService <T> {
    boolean ajouter(T t) throws SQLException;
    boolean modifier(T t) throws SQLException;
    boolean supprimer(T t) throws SQLException;
    List<T> getA() throws SQLException;
}
