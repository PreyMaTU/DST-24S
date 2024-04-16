package dst.ass2.aop.sample;

import dst.ass2.aop.IPluginExecutable;
import dst.ass2.aop.event.EventBus;
import dst.ass2.aop.event.EventType;
import dst.ass2.aop.logging.Invisible;
import dst.ass2.aop.management.Timeout;
import org.springframework.aop.support.AopUtils;

public class InterruptedPluginExecutable implements IPluginExecutable {
    private boolean interrupted = false;

    @Override
    @Invisible
    @Timeout(2000)
    public void execute() {
        EventBus eventBus = EventBus.getInstance();
        eventBus.add(EventType.PLUGIN_START, this, AopUtils.getTargetClass(this).getSimpleName() + " is executed!");

        while (!interrupted) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Should not happen but is not critical so the stack trace is printed to grab some attention ;-)
                e.printStackTrace();
            }
        }

        eventBus.add(EventType.PLUGIN_END, this, AopUtils.getTargetClass(this).getSimpleName() + " is finished!");
    }

    @Override
    public void interrupted() {
        interrupted = true;
    }
}
