package dst.ass1.jpa.dao;

import java.util.List;

public interface GenericDAO<T> {
    T findById(Long id);

    List<T> findAll();
}
