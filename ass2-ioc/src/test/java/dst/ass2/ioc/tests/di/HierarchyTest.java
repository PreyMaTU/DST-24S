package dst.ass2.ioc.tests.di;

import dst.ass2.ioc.di.IObjectContainer;
import dst.ass2.ioc.di.IObjectContainerFactory;
import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.di.annotation.Inject;
import dst.ass2.ioc.di.impl.ObjectContainerFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;


public class HierarchyTest {

    private IObjectContainerFactory factory;
    private IObjectContainer container;

    @Before
    public void setUp() throws Exception {
        factory = new ObjectContainerFactory();
        container = factory.newObjectContainer(new Properties());
    }

    @Component
    public static class SimpleSingleton {
        public String someValue;

        public SimpleSingleton() {
            // just to verify that the constructor has been called correctly
            this.someValue = "42";
        }
    }

    public static abstract class AbstractComponent {
        @Inject
        protected SimpleSingleton superSingleton;
    }

    @Component
    public static class ConcreteComponent extends AbstractComponent {
        @Inject
        private SimpleSingleton privateSingleton;

        public SimpleSingleton getSuperSingleton() {
            return superSingleton;
        }

        public SimpleSingleton getPrivateSingleton() {
            return privateSingleton;
        }
    }

    @Test
    public void getObject_injectsDependencyOfSuperClassCorrectly() throws Exception {
        ConcreteComponent component = container.getObject(ConcreteComponent.class);

        assertNotNull("getObject returned null", component);
        assertNotNull("Dependency of superclass was not injected", component.getSuperSingleton());
        assertNotNull("Private dependency was not injected", component.getPrivateSingleton());
        assertSame("Multiple singleton instances", component.getSuperSingleton(), component.getPrivateSingleton());
    }

    @Component
    public static class ClassA {
        @Inject
        SimpleSingleton singletonA;
    }

    @Component
    public static class ClassB extends ClassA {

        @Inject
        SimpleSingleton singletonB;

        @Inject
        ClassD classD;
    }

    @Component
    public static class ClassC {

        @Inject
        SimpleSingleton singletonC;
    }

    @Component
    public static class ClassD extends ClassC {

        @Inject
        SimpleSingleton singletonD;
    }

    /**
     * Tests the following hierarchy. Getting first D then B should should inject the previously created D into B.
     * <p>
     * +-------+      +-------+
     * |   A   |      |   C   |
     * +-------+      +-------+
     * ^ is a         ^ is a
     * |              |
     * +-------+ uses +-------+
     * |   B   | ---> |   D   |
     * +-------+      +-------+
     */
    @Test
    public void getObject_initializesHierarchyCorrectly() throws Exception {
        ClassD d = container.getObject(ClassD.class);

        assertNotNull("getObject returned null for ClassD", d);
        assertNotNull("ClassD dependency was not injected", d.singletonD);
        assertNotNull("ClassC dependency was not injected when instantiating ClassD", d.singletonC);

        ClassB b = container.getObject(ClassB.class);
        assertNotNull("getObject returned null for ClassB", b);
        assertNotNull("ClassB dependency was not injected", b.singletonB);
        assertNotNull("ClassA dependency was not injected when instantiating ClassB", b.singletonA);

        assertNotNull("ClassD dependency was not injected into ClassB", b.classD);
        assertSame("Container did not re-use already initialized ClassD instance", b.classD, d);
    }
}
