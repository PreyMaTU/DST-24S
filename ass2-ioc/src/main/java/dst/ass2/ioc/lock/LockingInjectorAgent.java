package dst.ass2.ioc.lock;

import java.lang.instrument.Instrumentation;

public class LockingInjectorAgent {

    public static void premain(String args, Instrumentation inst) {
        final var transformer = new LockingInjector();
        inst.addTransformer(transformer);
    }

}
