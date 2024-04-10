package dst.ass2.ioc.tests.di;

import dst.ass2.ioc.di.IObjectContainerFactory;
import dst.ass2.ioc.di.ObjectCreationException;
import dst.ass2.ioc.di.TypeConversionException;
import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.di.annotation.Property;
import dst.ass2.ioc.di.annotation.Scope;
import dst.ass2.ioc.di.impl.ObjectContainerFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.*;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class PropertyInjectionTest {

    private IObjectContainerFactory factory;

    @Before
    public void setUp() {
        factory = new ObjectContainerFactory();
    }

    @Component
    static class StringPropertyClass {
        @Property("my_string")
        String value;
    }

    @Test
    public void simple_string_value_injection() {
        var properties = new Properties();
        properties.setProperty("my_string", "my string value");
        var container = factory.newObjectContainer(properties);

        StringPropertyClass obj = container.getObject(StringPropertyClass.class);
        assertNotNull("getObject(StringPropertyClass.class) returned null", obj);
        assertEquals("my string value", obj.value);
    }

    @Test(expected = ObjectCreationException.class)
    public void wireMissingProperty_throwsException() {
        var container = factory.newObjectContainer(new Properties());
        container.getObject(StringPropertyClass.class);
    }

    @Component
    static class IntegerPropertyClass {
        @Property("my_integer")
        Integer value;
    }

    @Test
    public void simple_integer_value_injection() {
        var properties = new Properties();
        properties.setProperty("my_integer", "42");
        var container = factory.newObjectContainer(properties);

        IntegerPropertyClass obj = container.getObject(IntegerPropertyClass.class);
        assertNotNull("getObject(IntegerPropertyClass.class) returned null", obj);
        assertEquals(Integer.valueOf(42), obj.value);
    }

    @Test(expected = TypeConversionException.class)
    public void wireInvalidPropertyType_throwsException() {
        var properties = new Properties();
        properties.setProperty("my_integer", "fourtytwo");
        var container = factory.newObjectContainer(properties);

        IntegerPropertyClass obj = container.getObject(IntegerPropertyClass.class);
        assertNotNull("getObject(IntegerPropertyClass.class) returned null", obj);
        assertEquals(Integer.valueOf(42), obj.value);
    }

    @Component
    static class IntegerPrimitivePropertyClass {
        @Property("my_integer")
        int value;
    }

    @Test
    public void simple_integer_primitive_value_injection() {
        var properties = new Properties();
        properties.setProperty("my_integer", "42");
        var container = factory.newObjectContainer(properties);

        IntegerPrimitivePropertyClass obj = container.getObject(IntegerPrimitivePropertyClass.class);
        assertNotNull("getObject(IntegerPrimitivePropertyClass.class) returned null", obj);
        assertEquals(42, obj.value);
    }

    @Component
    static class MultiplePropertyClass {
        @Property("my_string")
        String stringValue;

        @Property("my_integer")
        int intValue;

        @Property("my_integer")
        Integer integerValue;

        @Property("my_float")
        Float floatValue;
    }

    @Test
    public void simple_multiple_property_injection() {
        var properties = new Properties();
        properties.setProperty("my_string", "my string value");
        properties.setProperty("my_integer", "42");
        properties.setProperty("my_float", "3.14");
        var container = factory.newObjectContainer(properties);

        MultiplePropertyClass obj = container.getObject(MultiplePropertyClass.class);
        assertNotNull("getObject(MultiplePropertyClass.class) returned null", obj);

        assertEquals("my string value", obj.stringValue);
        assertEquals(42, obj.intValue);
        assertEquals(Integer.valueOf(42), obj.integerValue);
        assertEquals(Float.valueOf(3.14f), obj.floatValue);
    }

    @Component
    static class InheritedMultiplePropertyClass extends MultiplePropertyClass {
        @Property("my_float")
        Float theSameFloatValue;
    }

    @Test
    public void multiple_property_injection_with_inheritance() {
        var properties = new Properties();
        properties.setProperty("my_string", "my string value");
        properties.setProperty("my_integer", "42");
        properties.setProperty("my_float", "3.14");
        var container = factory.newObjectContainer(properties);

        InheritedMultiplePropertyClass obj = container.getObject(InheritedMultiplePropertyClass.class);
        assertNotNull("getObject(InheritedMultiplePropertyClass.class) returned null", obj);

        assertEquals(Float.valueOf(3.14f), obj.theSameFloatValue);

        // inherited values
        assertEquals("Property not set correctly through inheritance", "my string value", obj.stringValue);
        assertEquals("Property not set correctly through inheritance", 42, obj.intValue);
        assertEquals("Property not set correctly through inheritance", Integer.valueOf(42), obj.integerValue);
        assertEquals("Property not set correctly through inheritance", Float.valueOf(3.14f), obj.floatValue);
    }

    @Component(scope = Scope.PROTOTYPE)
    static class PrototypePropertyClass {

        @Property("my_string")
        String stringValue;

    }

    @Test
    public void getPrototypeObject_usesLatestPropertyValue() {
        Properties properties = new Properties();
        properties.setProperty("my_string", "initial value");
        var container = factory.newObjectContainer(properties);

        var object1 = container.getObject(PrototypePropertyClass.class);
        assertEquals("initial value", object1.stringValue);

        // Properties returned by IObjectContainerFactory should be mutable
        container.getProperties().setProperty("my_string", "changed value");

        var object2 = container.getObject(PrototypePropertyClass.class);
        assertEquals("properties should be mutable", "changed value", object2.stringValue);
        assertEquals("initial value", object1.stringValue);
    }

    @SuppressWarnings("unused")
    @Component
    static class ManyValuesClass {
        @Property("my_string")
        String stringValue01;
        @Property("my_integer")
        Integer value01;
        @Property("my_integer")
        Integer value02;
        @Property("my_integer")
        Integer value03;
        @Property("my_integer")
        Integer value04;
        @Property("my_integer")
        Integer value05;
        @Property("my_string")
        String stringValue02;
        @Property("my_integer")
        Integer value06;
        @Property("my_integer")
        Integer value07;
        @Property("my_integer")
        Integer value08;
        @Property("my_integer")
        Integer value09;
        @Property("my_integer")
        Integer value10;
        @Property("my_string")
        String stringValue03;
        @Property("my_integer")
        Integer value11;
        @Property("my_integer")
        Integer value12;
        @Property("my_integer")
        Integer value13;
        @Property("my_integer")
        Integer value14;
        @Property("my_integer")
        Integer value15;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mutatePropertiesConcurrently_verifyThreadSafety() throws Exception {
        var container = factory.newObjectContainer(new Properties());

        Properties properties = container.getProperties();
        properties.setProperty("my_integer", "1");
        properties.setProperty("my_string", "some string value");

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(10);

        executor.execute(() -> {
            for (int i = 0; i < 100000; i++) {
                latch.countDown();
                properties.setProperty("my_integer", Integer.toString(i));
            }
        });
        Future<ManyValuesClass> getObject = executor.submit(() -> {
            latch.await(); // wait until the property set loop has started and warmed up
            return container.getObject(ManyValuesClass.class);
        });

        ManyValuesClass object = getObject.get();
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertNotEquals("updated property value was not used", Integer.valueOf(1), object.value01);
        assertThat("property values are inconsistent indicating non-thread-safe property injection",
            object.value01, allOf(
                is(object.value01), is(object.value02), is(object.value03), is(object.value04),
                is(object.value05), is(object.value06), is(object.value07), is(object.value08),
                is(object.value09), is(object.value10), is(object.value11), is(object.value12),
                is(object.value13), is(object.value14), is(object.value15)
            ));

    }


}
