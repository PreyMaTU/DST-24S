package dst.ass1.jooq.dao;

import java.util.List;

public interface GenericDAO<T> {
    T findById(Long id);

    List<T> findAll();

    T insert(T model);

    void delete(Long id);

}
