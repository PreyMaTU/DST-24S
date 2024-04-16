package dst.ass2.ioc.tests.di;

import dst.ass2.ioc.di.IObjectContainer;
import dst.ass2.ioc.di.IObjectContainerFactory;
import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.di.annotation.Initialize;
import dst.ass2.ioc.di.annotation.Inject;
import dst.ass2.ioc.di.impl.ObjectContainerFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class InitializeTest {

    private IObjectContainerFactory factory;
    private IObjectContainer container;

    @Before
    public void setUp() {
        factory = new ObjectContainerFactory();
        container = factory.newObjectContainer(new Properties());

        if (container == null) {
            throw new NullPointerException("ObjectContainerFactory did not return an ObjectContainer instance");
        }
    }

    @Component
    public static class ComponentClass {

        private AtomicInteger initializeCalls = new AtomicInteger(0);

        @Initialize
        public void myInitMethod() {
            initializeCalls.incrementAndGet();
        }

        public int getInitializeCalls() {
            return initializeCalls.get();
        }
    }

    @Test
    public void getObject_runsInitializeMethodCorrectly() throws Exception {
        ComponentClass component = container.getObject(ComponentClass.class);
        assertEquals("expected exactly one call to myInitMethod", 1, component.getInitializeCalls());
    }

    @Test
    public void getObject_runsInitializeMethodOnlyOnce() throws Exception {
        ComponentClass component = container.getObject(ComponentClass.class);
        assertEquals("expected exactly one call to myInitMethod", 1, component.getInitializeCalls());

        component = container.getObject(ComponentClass.class);
        assertEquals("expected exactly one call to myInitMethod", 1, component.getInitializeCalls());
    }

    @Component
    public static class AnotherComponentClass {

        @Inject
        private ComponentClass dependency;

        boolean wasNull;

        @Initialize
        public void myOtherInitMethod() {
            this.wasNull = dependency == null;
        }

        public ComponentClass getDependency() {
            return dependency;
        }
    }

    @Test
    public void getObject_runsInitializeMethodAfterInjection() throws Exception {
        AnotherComponentClass component = container.getObject(AnotherComponentClass.class);
        assertFalse("Expected dependency to be injected before calls to @Initialize", component.wasNull);
    }

    @Test
    public void getObject_runsInitializeMethodOfDependencyCorrectly() throws Exception {
        AnotherComponentClass component = container.getObject(AnotherComponentClass.class);
        assertEquals("expected exactly one call to myInitMethod", 1, component.getDependency().getInitializeCalls());
    }

    @Component
    public static class ComponentWithPrivateInitMethod {

        private AtomicInteger initializeCalls = new AtomicInteger(0);

        @Initialize
        private void myPrivateInitMethod() {
            initializeCalls.incrementAndGet();
        }

        public int getInitializeCalls() {
            return initializeCalls.get();
        }
    }

    @Test
    public void getObject_runsPrivateInitializeMethodCorrectly() throws Exception {
        ComponentWithPrivateInitMethod component = container.getObject(ComponentWithPrivateInitMethod.class);
        assertEquals("expected exactly one call to myPrivateInitMethod", 1, component.getInitializeCalls());
    }

}
