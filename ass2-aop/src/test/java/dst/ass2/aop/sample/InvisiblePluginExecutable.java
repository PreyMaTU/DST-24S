package dst.ass2.aop.sample;

import dst.ass2.aop.logging.Invisible;

public class InvisiblePluginExecutable extends AbstractPluginExecutable {
    @Override
    @Invisible
    public void execute() {
        super.execute();
    }
}
