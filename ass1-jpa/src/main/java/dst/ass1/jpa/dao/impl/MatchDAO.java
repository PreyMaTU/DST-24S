package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IMatchDAO;
import dst.ass1.jpa.model.IMatch;
import dst.ass1.jpa.model.impl.Match;

import javax.persistence.EntityManager;
import java.util.List;

public class MatchDAO implements IMatchDAO {
    private final EntityManager entityManager;

    MatchDAO(EntityManager entityManager) {
        this.entityManager= entityManager;
    }

    @Override
    public IMatch findById(Long id) {
        return entityManager.find(Match.class, id);
    }

    @Override
    public List<IMatch> findAll() {
        return entityManager
                .createQuery("SELECT m FROM Match m", IMatch.class)
                .getResultList();
    }
}
