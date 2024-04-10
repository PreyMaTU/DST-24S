package dst.ass2.ioc.tests.di;


import dst.ass2.ioc.di.IObjectContainer;
import dst.ass2.ioc.di.IObjectContainerFactory;
import dst.ass2.ioc.di.InjectionException;
import dst.ass2.ioc.di.InvalidDeclarationException;
import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.di.annotation.Inject;
import dst.ass2.ioc.di.annotation.Scope;
import dst.ass2.ioc.di.impl.ObjectContainerFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;


public class DependencyInjectionTest {

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
    public static class SimpleSingleton {
        public String someValue;

        public SimpleSingleton() {
            // just to verify that the constructor has been called correctly
            this.someValue = "42";
        }
    }

    public static class NotAComponentClass {
        // a plain class shouldn't be autowired

        @Inject
        SimpleSingleton singleton;
    }

    @Test(expected = InvalidDeclarationException.class)
    public void getObject_onNotAnnotatedClass_throwsException() {
        container.getObject(NotAComponentClass.class);
    }


    @Component
    public static abstract class AbstractComponentClass {
        // abstract classes can't be instantiated
    }

    @Test(expected = InjectionException.class)
    public void getObject_onAbstractClass_throwsException() throws Exception {
        container.getObject(AbstractComponentClass.class);
    }

    @Test
    public void getObject_onSimpleSingleton_createsObjectCorrectly() {
        var object = container.getObject(SimpleSingleton.class);
        assertNotNull(object);
        assertThat(object, instanceOf(SimpleSingleton.class));
        assertEquals("constructor was not called correctly", "42", object.someValue);
    }

    @Test
    public void getObject_onSimpleSingleton_returnsSameObject() {
        var object1 = container.getObject(SimpleSingleton.class);
        var object2 = container.getObject(SimpleSingleton.class);

        assertNotNull(object1);
        assertNotNull(object2);
        assertSame(object1, object2);
    }

    @Test
    public void getObject_onDifferentContainer_returnsDifferentObject() {
        var container1 = factory.newObjectContainer(new Properties());
        var object1 = container1.getObject(SimpleSingleton.class);

        var container2 = factory.newObjectContainer(new Properties());
        var object2 = container2.getObject(SimpleSingleton.class);

        assertNotNull(object1);
        assertNotNull(object2);
        assertNotSame(object1, object2);
    }

    @Component(scope = Scope.PROTOTYPE)
    public static class SimplePrototype {

        static final AtomicInteger COUNTER = new AtomicInteger(0);

        public int cnt;

        public SimplePrototype() {
            // just to verify that the constructor has been called correctly
            cnt = SimplePrototype.COUNTER.incrementAndGet();
        }
    }

    @Test
    public void getObject_onSimplePrototype_returnsDifferentObjects() {
        SimplePrototype.COUNTER.set(0);
        var object1 = container.getObject(SimplePrototype.class);
        var object2 = container.getObject(SimplePrototype.class);

        assertNotNull(object1);
        assertNotNull(object2);
        assertNotSame(object1, object2);

        assertEquals(1, object1.cnt);
        assertEquals(2, object2.cnt);
    }

    @Component(scope = Scope.PROTOTYPE)
    public static class CompositePrototype {

        @Inject
        SimpleSingleton simpleSingleton;
    }

    @Test
    public void getObject_onCompositePrototype_createsObjectCorrectly() {
        var object = container.getObject(CompositePrototype.class);
        assertNotNull(object.simpleSingleton);
    }

    @Test
    public void getObject_onCompositePrototype_injectsPreviouslyCreatedSingleton() {
        var singleton = container.getObject(SimpleSingleton.class);

        var object = container.getObject(CompositePrototype.class);
        assertNotNull(object.simpleSingleton);

        assertSame("autowire should use already container managed singletons", singleton, object.simpleSingleton);
    }

    @Component
    public static class CompositeSingleton {
        @Inject
        SimpleSingleton simpleSingleton;
    }

    @Test
    public void getObject_onCompositeSingleton_createsObjectGraphCorrectly() {
        var object = container.getObject(CompositeSingleton.class);

        assertNotNull(object);
        assertThat(object, instanceOf(CompositeSingleton.class));

        assertNotNull("dependency of CompositeSingleton not initialized", object.simpleSingleton);
        assertNotNull("dependency of CompositeSingleton not initialized correctly", object.simpleSingleton.someValue);
    }

    public interface IService {
        int getSomeValue();
    }

    @Component
    public static class ServiceImpl implements IService {
        private int someValue;

        public ServiceImpl() {
            someValue = 42;
        }

        @Override
        public int getSomeValue() {
            return someValue;
        }
    }

    @Component
    public static class ServiceUser {
        @Inject(targetType = ServiceImpl.class)
        IService service;
    }

    @Test
    public void getObject_usingTargetType_createsGraphCorrectly() throws Exception {
        var object = container.getObject(ServiceUser.class);

        assertNotNull(object);
        assertNotNull("dependency of ServiceUser not initialized", object.service);
        assertThat("dependency of ServiceUser not initialized correctly", object.service, instanceOf(ServiceImpl.class));
        assertEquals("dependency of ServiceUser not initialized correctly", 42, object.service.getSomeValue());
    }

    @Component
    public static class InvalidServiceUser {
        // trying to inject an existing but invalid type (a SimplePrototype is not an IService)
        @Inject(targetType = SimplePrototype.class)
        IService service;
    }

    @Test(expected = InvalidDeclarationException.class)
    public void getObject_onInvalidTargetType_throwsException() {
        container.getObject(InvalidServiceUser.class);
    }

}
