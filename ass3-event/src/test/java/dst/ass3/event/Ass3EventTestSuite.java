package dst.ass3.event;

import dst.ass3.event.tests.Ass3_3_1Test;
import dst.ass3.event.tests.Ass3_3_2Test;
import dst.ass3.event.tests.Ass3_3_3Test;
import dst.ass3.event.tests.Ass3_3_4Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Ass3EventTestSuite.
 */
@RunWith(Suite.class)
@SuiteClasses({
    Ass3_3_1Test.class,
    Ass3_3_2Test.class,
    Ass3_3_3Test.class,
    Ass3_3_4Test.class
})
public class Ass3EventTestSuite {
}
