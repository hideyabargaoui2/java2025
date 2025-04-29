package services;

import java.util.List;

public interface Iservice<T>{
    boolean ajouter(T t);
    void modifier(T t);
    void  supprimer(T t);
    List<T> getA();

}
