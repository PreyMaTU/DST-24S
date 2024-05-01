package dst.ass2.ioc.tests.di;

import dst.ass2.ioc.di.IObjectContainer;
import dst.ass2.ioc.di.IObjectContainerFactory;
import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.di.annotation.Initialize;
import dst.ass2.ioc.di.impl.ObjectContainerFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class CustomInitializeTest {

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
    static class Component1A {
        public int initA= 0;
        public int initB= 0;
        public int initC= 0;

        @Initialize
        public void initializeA() { initA++; }

        @Initialize
        protected void initializeB() { initB++; }

        @Initialize
        private void initializeC() { initC++; }
    }

    @Component
    static class Component1B extends Component1A {
        public int initD= 0;
        public int initE= 0;
        public int initF= 0;

        @Initialize
        public void initializeD() { initD++; }

        @Initialize
        protected void initializeE() { initE++; }

        @Initialize
        protected void initializeF() { initF++; }
    }

    @Test
    public void getObject_runsAllInitializeMethods() {
        final var component = container.getObject( Component1B.class );
        assertEquals( 1, component.initA );
        assertEquals( 1, component.initB );
        assertEquals( 1, component.initC );
        assertEquals( 1, component.initD );
        assertEquals( 1, component.initE );
        assertEquals( 1, component.initF );
    }



    @Component
    static class Component2A {
        int initA= 0;

        @Initialize
        public void initialize() { initA++; }
    }

    @Component
    static class Component2B extends Component2A {
        int initB= 0;

        @Initialize
        @Override
        public void initialize() { initB++; }
    }

    @Component
    static class Component2C extends Component2B {
        int initC= 0;

        @Initialize
        @Override
        public void initialize() {  initC++; }
    }

    @Test
    public void getObject_runsOnlyMostSpecializedInitializeMethods() {
        final var component = container.getObject( Component2C.class );
        assertEquals( 0, component.initA );
        assertEquals( 0, component.initB );
        assertEquals( 1, component.initC );
    }



    @Component
    static class Component3A {
        int initA= 0;

        @Initialize
        private void initialize() { initA++; }
    }

    @Component
    static class Component3B extends Component3A {
        int initB= 0;

        @Initialize
        private void initialize() { initB++; }
    }

    @Test
    public void getObject_runsEachPrivateInitializeMethods() {
        final var component = container.getObject( Component3B.class );
        assertEquals( 1, component.initA );
        assertEquals( 1, component.initB );
    }



    @Component
    static class Component4A {
        String trail= "";

        @Initialize
        void initializeA() { trail+= "A"; }
    }

    @Component
    static class Component4B extends Component4A {
        @Initialize
        void initializeB() { trail+= "B"; }
    }

    @Component
    static class Component4C extends Component4B {
        @Initialize
        void initializeC() { trail+= "C"; }
    }

    @Test
    public void getObject_runsInitializeMethodsInOrder() {
        final var component = container.getObject( Component4C.class );
        assertEquals( "ABC", component.trail );
    }
}
