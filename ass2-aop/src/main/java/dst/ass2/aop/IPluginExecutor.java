package dst.ass2.aop;

import java.io.File;

/**
 * The plugin executor interface.
 */
public interface IPluginExecutor {

    /**
     * Adds a directory to monitor.
     * May be called before and also after start has been called.
     *
     * @param dir the directory to monitor.
     */
    void monitor(File dir);

    /**
     * Stops monitoring the specified directory.
     * May be called before and also after start has been called.
     *
     * @param dir the directory which should not be monitored anymore.
     */
    void stopMonitoring(File dir);

    /**
     * Starts the plugin executor.
     * All added directories will be monitored and any .jar file processed.
     * If there are any {@link IPluginExecutable} implementations,
     * they are executed within own threads.
     */
    void start();

    /**
     * Stops the plugin executor.
     * The monitoring of directories and the execution
     * of the plugins should stop as soon as possible.
     */
    void stop();
}
