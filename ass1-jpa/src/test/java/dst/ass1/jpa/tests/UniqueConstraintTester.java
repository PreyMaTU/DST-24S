package dst.ass1.jpa.tests;

import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.fail;

/**
 * Tests a unique constraint by creating two instances of an entity, calling the same value setter on each entity,
 * and finally trying to persist them.
 *
 * @param <E> the entity type
 */
public class UniqueConstraintTester<E> {

    private Consumer<E> valueSetter;
    private E e1;
    private E e2;

    public UniqueConstraintTester(E e1, E e2, Consumer<E> valueSetter) {
        this.e1 = e1;
        this.e2 = e2;
        this.valueSetter = valueSetter;
    }

    public UniqueConstraintTester(Supplier<E> entityFactory, Consumer<E> valueSetter) {
        this(entityFactory.get(), entityFactory.get(), valueSetter);
    }

    public void run(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }

        valueSetter.accept(e1);
        em.persist(e1);
        em.flush();

        try {
            valueSetter.accept(e2);
            em.persist(e2);
            em.flush();
            fail("Missing unique constraint in " + e2.getClass());
        } catch (PersistenceException e) {
            Throwable cause = e.getCause();
            if (!(cause instanceof ConstraintViolationException)) {
                fail("Missing unique constraint in " + e2.getClass());
            }
        }
    }
}
