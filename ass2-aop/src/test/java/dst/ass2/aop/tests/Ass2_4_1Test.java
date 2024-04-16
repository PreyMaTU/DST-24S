package dst.ass2.aop.tests;

import dst.ass2.aop.IPluginExecutor;
import dst.ass2.aop.PluginExecutorFactory;
import dst.ass2.aop.event.Event;
import dst.ass2.aop.event.EventBus;
import dst.ass2.aop.event.EventType;
import dst.ass2.aop.util.PluginUtils;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class Ass2_4_1Test {
    static final String SIMPLE_PLUGIN = "dst.ass2.aop.sample.SimplePluginExecutable";
    IPluginExecutor executor;
    EventBus eventBus = EventBus.getInstance();

    @BeforeClass
    public static void beforeClass() {
        Assert.assertEquals("Cannot create temporary plugin directory: " + PluginUtils.PLUGINS_DIR.getAbsolutePath(),
            true, PluginUtils.PLUGINS_DIR.isDirectory() || PluginUtils.PLUGINS_DIR.mkdirs());
    }

    @AfterClass
    public static void afterClass() throws IOException {
        FileUtils.forceDeleteOnExit(PluginUtils.PLUGINS_DIR);
    }

    @Before
    public void before() {
        PluginUtils.cleanPluginDirectory();
        executor = PluginExecutorFactory.createPluginExecutor();
        executor.monitor(PluginUtils.PLUGINS_DIR);
        executor.start();
        eventBus.reset();
    }

    @After
    public void after() {
        executor.stop();
        eventBus.reset();
        PluginUtils.cleanPluginDirectory();
    }

    /**
     * Executing plugin copied to plugin directory.
     */
    @Test(timeout = PluginUtils.PLUGIN_TEST_TIMEOUT)
    public void copiedPlugin_isExecutedCorrectly() throws Exception {
        // Preparing new plugin
        PluginUtils.preparePlugin(PluginUtils.SIMPLE_FILE);

        // Periodically check for the plugin to be executed
        while (eventBus.size() != 2) {
            Thread.sleep(100);
        }

        // Verify that the plugin was started and stopped orderly
        assertTrue(SIMPLE_PLUGIN + " was not started properly.", eventBus.has(SIMPLE_PLUGIN, EventType.PLUGIN_START));
        assertTrue(SIMPLE_PLUGIN + " did not finish properly.", eventBus.has(SIMPLE_PLUGIN, EventType.PLUGIN_END));
    }

    /**
     * Checking that each plugin JAR uses its own ClassLoader.
     */
    @Test(timeout = PluginUtils.PLUGIN_TEST_TIMEOUT)
    public void samePlugins_useSeparateClassLoaders() throws Exception {
        // Preparing two plugins
        PluginUtils.preparePlugin(PluginUtils.SIMPLE_FILE);
        PluginUtils.preparePlugin(PluginUtils.SIMPLE_FILE);

        // Periodically check for the plugins to be executed
        while (eventBus.size() != 4) {
            Thread.sleep(100);
        }

        /*
         * Verify that the plugins were loaded by different classloaders.
         * This can be checked by comparing the ClassLoaders or comparing the classes themselves.
         * In other words, if a class is loaded by two different ClassLoaders, it holds that
         * a.getClass() != b.getClass() even if the byte code is identical.
         */
        List<Event> events = eventBus.getEvents(EventType.PLUGIN_START);
        String msg = "Both plugins where loaded by the same ClassLoader";
        assertNotSame(msg, events.get(0).getPluginClass().getClassLoader(), events.get(1).getPluginClass().getClassLoader());
        assertNotSame(msg, events.get(0).getPluginClass(), events.get(1).getPluginClass());
    }

    /**
     * Checking whether two plugins in a single JAR are executed concurrently.
     */
    @Test(timeout = PluginUtils.PLUGIN_TEST_TIMEOUT)
    public void allPlugins_executeConcurrently() throws Exception {
        // Start a plugin containing two IPluginExecutable classes
        PluginUtils.preparePlugin(PluginUtils.ALL_FILE);

        // Periodically check for the plugins to be executed
        while (eventBus.size() != 4) {
            Thread.sleep(100);
        }

        // Check that there is exactly one start and end event each
        List<Event> starts = eventBus.getEvents(EventType.PLUGIN_START);
        List<Event> ends = eventBus.getEvents(EventType.PLUGIN_END);
        assertEquals("EventBus must contain exactly 2 start events.", 2, starts.size());
        assertEquals("EventBus must contain exactly 2 end events.", 2, ends.size());

        // Verify that the plugins were started concurrently
        String msg = "All plugins should have been started before the first ended - %d was after %d.";
        for (Event end : ends) {
            for (Event start : starts) {
                assertTrue(String.format(msg, start.getTime(), end.getTime()), start.getTime() < end.getTime());
            }
        }
    }

    /**
     * Checking whether two plugin JARs are executed concurrently.
     */
    @Test(timeout = PluginUtils.PLUGIN_TEST_TIMEOUT)
    public void multiplePlugins_executeConcurrently() throws Exception {
        // Start two plugins at once
        PluginUtils.preparePlugin(PluginUtils.SIMPLE_FILE);
        PluginUtils.preparePlugin(PluginUtils.SIMPLE_FILE);

        // Periodically check for the plugins to be executed
        while (eventBus.size() != 4) {
            Thread.sleep(100);
        }

        // Check that there is exactly one start and end event each
        List<Event> starts = eventBus.getEvents(EventType.PLUGIN_START);
        List<Event> ends = eventBus.getEvents(EventType.PLUGIN_END);
        assertEquals("EventBus must contain exactly 2 start events.", 2, starts.size());
        assertEquals("EventBus must contain exactly 2 end events.", 2, ends.size());

        // Verify that the plugins were started concurrently.
        String msg = "All plugins should have been started before the first ended - %d was after %d.";
        for (Event end : ends) {
            for (Event start : starts) {
                assertTrue(String.format(msg, start.getTime(), end.getTime()), start.getTime() < end.getTime());
            }
        }
    }
}
