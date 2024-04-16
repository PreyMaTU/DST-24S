package dst.ass2.ioc.tests;

import dst.ass2.ioc.tests.di.DependencyInjectionTest;
import dst.ass2.ioc.tests.di.HierarchyTest;
import dst.ass2.ioc.tests.di.InitializeTest;
import dst.ass2.ioc.tests.di.PropertyInjectionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    DependencyInjectionTest.class,
    PropertyInjectionTest.class,
    HierarchyTest.class,
    InitializeTest.class
})
public class Ass2_2_1_Suite {
    // suite
}
