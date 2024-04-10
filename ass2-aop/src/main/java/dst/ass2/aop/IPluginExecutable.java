package dst.ass2.aop;

/**
 * Implementations of this interface are executable by the IPluginExecutor.
 */
public interface IPluginExecutable {

    /**
     * Called when this plugin is executed.
     */
    void execute();

    /**
     * Called when the execution of the plugin is interrupted
     */
    void interrupted();
}
