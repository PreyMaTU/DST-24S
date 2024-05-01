package dst.ass2.ioc.lock;

import dst.ass2.ioc.di.annotation.Component;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class LockingInjector implements ClassFileTransformer {

    private CtClass loadClass( String className, byte[] classfileBuffer ) {
        final var classPool= ClassPool.getDefault();
        try {
            return classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
        } catch( IOException e ) {
            throw new RuntimeException( String.format("Could not read class '%s'", className) , e);
        }
    }

    private String readLockAnnotation( CtMethod method ) {
        try {
            final var annotation= (Lock) method.getAnnotation( Lock.class );
            if( annotation == null ) {
                return null;
            }

            return annotation.value();

        } catch( ClassNotFoundException e ) {
            throw new RuntimeException( String.format("Could not access annotation of method '%s'", method.getName()), e );
        }
    }

    private void patchLockingMethod( CtMethod method, String lockName ) {
        try {
            method.insertBefore(
                    String.format("dst.ass2.ioc.lock.LockManager.getInstance().getLock(\"%s\").lock();", lockName)
            );
            method.insertAfter(
                    String.format("dst.ass2.ioc.lock.LockManager.getInstance().getLock(\"%s\").unlock();", lockName),
                    true
            );
        } catch( CannotCompileException e ) {
            throw new RuntimeException( String.format("Cannot compile injected locking code in method '%s'", method.getName()), e);
        }
    }

    @Override
    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer
    ) throws IllegalClassFormatException {

        final var clazz= loadClass( className, classfileBuffer );

        // Ignore non-component classes
        if( !clazz.hasAnnotation( Component.class ) ) {
            return classfileBuffer;
        }

        // Patch all methods with a @Lock annotation
        for( final var method : clazz.getDeclaredMethods() ) {
            final var lockName= readLockAnnotation( method );
            if( lockName != null ) {
                patchLockingMethod( method, lockName );
            }
        }

        // Compile class and emit the transformed bytecode
        try {
            return clazz.toBytecode();
        } catch( IOException | CannotCompileException e ) {
            throw new RuntimeException( String.format("Could not emit transformed bytecode for class '%s'", className), e);
        }
    }

}
