package dst.ass2.aop.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.InaccessibleObjectException;
import java.util.logging.Logger;

@Aspect
public class LoggingAspect {

    @Pointcut("execution(void dst.ass2.aop.IPluginExecutable.execute()) && !@annotation(dst.ass2.aop.logging.Invisible)")
    public void visibleExecutionMethodPointcut() {}

    @Before("LoggingAspect.visibleExecutionMethodPointcut()")
    public void logBeforeVisibleExecutionMethod(JoinPoint joinPoint) {
        log(joinPoint, "started to execute");
    }

    @After("LoggingAspect.visibleExecutionMethodPointcut()")
    public void logAfterVisibleExecutionMethod(JoinPoint joinPoint) {
        log(joinPoint, "is finished");
    }

    private void log(JoinPoint joinPoint, String message) {
        // Build the full log message
        final var clazz= joinPoint.getTarget().getClass();
        final var logText= clazz.getCanonicalName() + " " + message;

        // Try to get the class's logger or just fall back to println
        final var logger= getClassLogger( joinPoint.getTarget() );
        if( logger != null ) {
            logger.info( logText );
        } else {
            System.out.println(logText);
        }
    }

    private Logger getClassLogger(Object target) {
        // Step through class hierarchy and see if anyone has a logger
        var currentType= target.getClass();
        while( currentType != null ) {

            // Iterate over all fields and try to find a logger
            for( final var field : currentType.getDeclaredFields() ) {
                try {
                    // Field implements logger interface?
                    field.setAccessible( true );
                    if (!Logger.class.isAssignableFrom(field.getType())) {
                        continue;
                    }

                    return (Logger) field.get(target);

                } catch( InaccessibleObjectException | IllegalArgumentException | IllegalAccessException e ) {
                    throw new RuntimeException(String.format("Could not access logger in '%s'", field.getName()), e);
                }
            }

            currentType= currentType.getSuperclass();
        }

        return null;
    }

}
