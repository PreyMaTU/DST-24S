package dst.ass1.jpa.tests;

import dst.ass1.jpa.ORMService;
import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.util.Constants;
import org.hibernate.PropertyValueException;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

/**
 * Tests the IDriver tel not null constraint.
 */
public class Ass1_1_1_04Test {

    @Rule
    public ORMService orm = new ORMService();

    @Test
    public void testConstraint() {
        IDriver e1 = orm.getModelFactory().createDriver();
        e1.setTel(null);
        var e = assertThrows(PersistenceException.class, () -> {
            orm.em().getTransaction().begin();
            orm.em().persist(e1);
            orm.em().flush();
        });
        assertThat(e.getMessage(), containsString("not-null property"));
        assertThat(e.getCause(), is(instanceOf(PropertyValueException.class)));
    }

    @Test
    public void testConstraintJdbc() {
        assertFalse(orm.getDatabaseGateway().isNullable(Constants.T_DRIVER, Constants.M_DRIVER_TEL));
    }

}
