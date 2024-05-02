package dst.ass2.aop.management;

import dst.ass2.aop.IPluginExecutable;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
public class ManagementAspect {

    private final ConcurrentHashMap<IPluginExecutable, TimerTask> timers= new ConcurrentHashMap<>();
    private final Timer timer = new Timer();

    @Pointcut("execution(void dst.ass2.aop.IPluginExecutable.execute()) && @annotation(dst.ass2.aop.management.Timeout)")
    public void timeExecutionMethodPointcut() {}

    @Before("ManagementAspect.timeExecutionMethodPointcut()")
    public void startTimerBeforeExecutionMethod(JoinPoint joinPoint) {
        // No checks needed because we know that the method has the annotation
        final var plugin= (IPluginExecutable) joinPoint.getTarget();
        final var signature= (MethodSignature) joinPoint.getSignature();
        final var annotation= signature.getMethod().getAnnotation( Timeout.class );
        final var timeoutValue= annotation.value();

        // Create a new task that interrupts the plugin
        final var task= new TimerTask() {
            public void run() {
                plugin.interrupted();
                timers.remove( plugin );
            }
        };

        // Schedule the task
        timers.put( plugin, task );
        timer.schedule( task, timeoutValue );
    }

    @After("ManagementAspect.timeExecutionMethodPointcut()")
    public void stopTimerBeforeExecutionMethod(JoinPoint joinPoint) {
        // Cancel the interrupt task
        final var plugin= (IPluginExecutable) joinPoint.getTarget();
        final var task= timers.remove( plugin );
        if( task != null ) {
            task.cancel();
        }
    }
}
