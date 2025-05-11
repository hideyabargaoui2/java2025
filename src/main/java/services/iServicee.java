package services;
import java.sql.SQLException;
import java.util.List;

public interface iServicee<T> {
    boolean ajouter(T t) throws SQLException;
    boolean modifier(T t) throws SQLException;
    boolean supprimer(T t) throws SQLException;
    List<T> getA() throws SQLException;
}