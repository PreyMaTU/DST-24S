package dst.ass2.ioc.di.impl;

import dst.ass2.ioc.di.*;
import dst.ass2.ioc.di.annotation.*;
import dst.ass2.ioc.di.annotation.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

public class ObjectContainer implements IObjectContainer {

    private final Properties properties;
    private final HashMap<Class<?>, Object> singeltons= new HashMap<>();

    ObjectContainer( Properties properties ) {
        this.properties = properties;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    private <T> T getPropertyAsType( String name, Class<T> type ) {
        final var stringValue= properties.getProperty( name );
        if( stringValue == null ) {
            throw new ObjectCreationException( String.format( "Property '%s' does not exist", name ) );
        }

        try {
            if( type == String.class ) {
                return (T) stringValue;
            }
            if( type == Boolean.class || type == boolean.class ) {
                return (T) Boolean.valueOf( stringValue );
            }
            if( type == Short.class || type == short.class ) {
                return (T) Short.valueOf( stringValue );
            }
            if( type == Integer.class || type == int.class ) {
                return (T) Integer.valueOf( stringValue );
            }
            if( type == Long.class || type == long.class ) {
                return (T) Long.valueOf( stringValue );
            }
            if( type == Float.class || type == float.class ) {
                return (T) Float.valueOf( stringValue );
            }
            if( type == Double.class || type == double.class ) {
                return (T) Double.valueOf( stringValue );
            }
            if( type == Character.class || type == char.class ) {
                if( stringValue.isEmpty() ) {
                    throw new TypeConversionException( String.format("Cannot convert empty string in '%s' to type '%s'", name, type.getName() ) );
                }
                return (T) Character.valueOf( stringValue.charAt( 0 ) );
            }
        } catch( NumberFormatException e ) {
            throw new TypeConversionException( String.format("Cannot convert '%s' to type '%s'", name, type.getName() ), e );
        }

        throw new TypeConversionException( String.format("Unsupported property type '%s'", type.getName() ) );
    }

    private <T> T createObject( Class<T> type ) throws ObjectCreationException {
        try {
            // Get the default constructor (has no parameters)
            final var constructor= type.getDeclaredConstructor();
            constructor.setAccessible( true );
            return constructor.newInstance();

        } catch( InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
            throw new ObjectCreationException( String.format( "Could not create %s", type.getName() ), e );
        }
    }

    private void injectField( Object object, Field field, Class<?> targetType, boolean isOptional ) {
        try {
            // Recursively get the injected component
            final var injectType = (targetType != null && targetType != Void.class) ? targetType : field.getType();
            final var injectedObject = getObject(injectType);

            // Inject the new object
            field.setAccessible(true);
            field.set(object, injectedObject);

        } catch( InjectionException e ) {
            if( !isOptional ) {
                throw e;
            }

        } catch( IllegalAccessException | InaccessibleObjectException | IllegalArgumentException e ) {
            if( !isOptional ) {
                throw new InvalidDeclarationException(
                        String.format( "Could not inject field %s into %s", field.getName(), object.getClass().getName() ), e
                );
            }
        }
    }

    private void injectProperty( Object object, Field field, String name ) {
        try {
            final var propertyValue= getPropertyAsType( name, field.getType() );
            field.setAccessible( true );
            field.set( object, propertyValue );

        } catch( IllegalAccessException | InaccessibleObjectException | IllegalArgumentException e ) {
            throw new RuntimeException(e);
        }
    }

    private void autoWireFields( Object object, Field[] fields ) {
        for( final var field : fields ) {
            // Field has @Inject
            final var injectAnnotation= field.getAnnotation( Inject.class );
            if( injectAnnotation != null ) {
                injectField( object, field, injectAnnotation.targetType(), injectAnnotation.optional() );
                continue;
            }

            // Field has @Property
            final var propertyAnnotation= field.getAnnotation( Property.class );
            if( propertyAnnotation != null ) {
                injectProperty( object, field, propertyAnnotation.value() );
            }
        }
    }

    private void callInitMethods(Object object, List<Class<?>> typeHierarchy, int level, HashSet<String> seenMethods ) {
        final var type= typeHierarchy.get( level );
        final var methods= type.getDeclaredMethods();
        for( final var method : methods ) {
            // Method has @Initialize
            if( !method.isAnnotationPresent( Initialize.class ) ) {
                continue;
            }

            // Ignore methods in the hierarchy that are already marked as seen
            if( seenMethods.contains( method.getName() ) ) {
                continue;
            }

            // Only allow zero parameter init methods
            if( method.getParameterCount() > 0 ) {
                throw new InvalidDeclarationException(
                        String.format("Invalid initialization method '%s' in '%s': No parameters allowed", method.getName(), type.getName())
                );
            }

            // Search for a method overriding this one. A method can only be overridden if it is not private. Call
            // only the most specialized form of the method (bottom up search) once and then mark the name as seen.
            var overridingMethod= method;
            var overridingType= type;
            if( !Modifier.isPrivate( method.getModifiers() ) ) {
                for (int i = 0; i < level; i++) {
                    try {
                        overridingMethod = typeHierarchy.get(i).getDeclaredMethod(method.getName());
                        overridingType= typeHierarchy.get(i);
                        break;
                    } catch (NoSuchMethodException ignored) {
                        continue;
                    }
                }

                seenMethods.add( method.getName() );
            }

            // Invoke the method on the object
            try {
                overridingMethod.setAccessible( true );
                overridingMethod.invoke( object );
            } catch( InaccessibleObjectException | InvocationTargetException | IllegalAccessException e ) {
                throw new ObjectCreationException(
                        String.format("Cannot call initialization method '%s' in '%s'", method.getName(), overridingType.getName()), e
                );
            }
        }
    }

    private <T> T setupNewObject( Class<T> type ) {
        // Step through the type hierarchy and build list
        final var typeHierarchy= new ArrayList<Class<?>>( 20 );
        Class<?> currentType= type;
        while( currentType != null ) {
            typeHierarchy.add( currentType );

            // Go one step up
            currentType= currentType.getSuperclass();
        }

        final var object= createObject( type );

        // Auto wire all fields, with exclusive access to the properties
        synchronized( properties ) {
            for( int i= typeHierarchy.size()- 1; i >= 0; i-- ) {
                autoWireFields( object, typeHierarchy.get(i).getDeclaredFields() );
            }
        }

        // Call all @Initialize methods
        final var seenMethods= new HashSet<String>();
        for( int i= typeHierarchy.size()- 1; i >= 0; i-- ) {
            callInitMethods( object, typeHierarchy, i, seenMethods );
        }

        return object;
    }

    @Override
    public <T> T getObject(Class<T> type) throws InjectionException {
        final var component = type.getAnnotation(Component.class);
        if (component == null) {
            throw new InvalidDeclarationException(String.format("%s is not a @Component", type.getName()));
        }

        // Prototype objects can be created concurrently
        if (component.scope() != Scope.SINGLETON) {
            return setupNewObject( type );
        }

        synchronized( singeltons ) {
            // Try to get cached singleton
            final var singleton = singeltons.get(type);
            if (singleton != null) {
                return (T) singleton;
            }

            // Create new singleton instance
            final var object = setupNewObject(type);

            // Cache the singleton instance
            if (component.scope() == Scope.SINGLETON) {
                singeltons.put(type, object);
            }

            return object;
        }
    }
}
