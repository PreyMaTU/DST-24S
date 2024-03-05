package dst.ass1.jpa.tests;

import dst.ass1.jpa.ORMService;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.util.Constants;
import org.junit.Rule;
import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the constraint for IRider bank data.
 */
public class Ass1_1_2_01Test {

    @Rule
    public ORMService orm = new ORMService();

    @Test
    public void testRiderAccountNoBankCodeConstraint() {
        MessageDigest md = getMessageDigest();

        IRider r1 = orm.getModelFactory().createRider();
        IRider r2 = orm.getModelFactory().createRider();

        r1.setTel("tel1");
        r2.setTel("tel2");
        r1.setPassword(md.digest("password1".getBytes()));
        r2.setPassword(md.digest("password2".getBytes()));

        new UniqueConstraintTester<>(r1, r2, e -> {
            e.setEmail("uniqueVal1");
            e.setName("uniqueVal2");
        }).run(orm.getEntityManager());
    }

    @Test
    public void testRiderAccountNoBankCodeConstraintJdbc() {
        assertTrue(orm.getDatabaseGateway().isIndex(Constants.T_RIDER, Constants.M_RIDER_NAME, false));
        assertTrue(orm.getDatabaseGateway().isIndex(Constants.T_RIDER, Constants.M_RIDER_EMAIL, false));
        assertTrue(orm.getDatabaseGateway().isComposedIndex(Constants.T_RIDER, Constants.M_RIDER_NAME, Constants.M_RIDER_EMAIL));

        assertFalse(orm.getDatabaseGateway().isNullable(Constants.T_RIDER, Constants.M_RIDER_NAME));
        assertFalse(orm.getDatabaseGateway().isNullable(Constants.T_RIDER, Constants.M_RIDER_EMAIL));
    }

    private MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
