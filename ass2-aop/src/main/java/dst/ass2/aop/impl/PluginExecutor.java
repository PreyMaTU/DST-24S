package dst.ass2.aop.impl;

import dst.ass2.aop.IPluginExecutable;
import dst.ass2.aop.IPluginExecutor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarFile;

public class PluginExecutor implements IPluginExecutor {

    private final HashMap<File, WatchKey> watchKeys = new HashMap<>();
    private WatchService watchService;
    private Thread watchThread;

    private final ExecutorService pluginThreadPool = Executors.newCachedThreadPool();

    private WatchKey watchDirectory( File dir ) {
        try {
            return dir.toPath().register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY
            );
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("Could not register watch event for %s", dir.getAbsolutePath()), e
            );
        }
    }

    private void ensureWatchService() {
        if( watchService != null ) {
            return;
        }

        try {
            watchService= FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException( "Could not creat file watch service", e);
        }

        // If there are files already stored from a watch service that was previously closed
        // we re-watch them with the new service
        watchKeys.replaceAll( (dir, watchKey) -> this.watchDirectory( dir ) );
    }

    @Override
    public void monitor(File dir) {
        ensureWatchService();
        watchKeys.computeIfAbsent( dir, this::watchDirectory );
    }

    @Override
    public void stopMonitoring(File dir) {
        final var watchKey= watchKeys.remove(dir);
        if( watchKey != null ) {
            watchKey.cancel();
        }
    }

    @Override
    public void start() {
        if( watchThread != null ) {
            return;
        }

        ensureWatchService();

        watchThread= new Thread( this::watchFilesAndHandleEvents );
        watchThread.start();
    }

    @Override
    public void stop() {
        if( watchThread == null ) {
            return;
        }

        try {
            watchService.close();
            watchService= null;
            watchThread= null;
        } catch( IOException e ) {
            throw new RuntimeException( "Could not close watch service", e );
        }
    }

    private URL fileToURL( File file ) {
        try {
            return file.toURI().toURL();
        } catch( MalformedURLException e ) {
            throw new RuntimeException(
                    String.format("Could not convert file path '%s' to URL", file.getAbsolutePath()), e
            );
        }
    }

    private List<Runnable> loadPluginTasks( File file ) {
        final var url= fileToURL( file );
        try(
                final var classLoader = new URLClassLoader( new URL[]{ url } );
                final var jarFile= new JarFile( file )
        ) {

            final var tasks= new ArrayList<Runnable>();

            // Iterate over all entries in the JAR file
            final var entryIt= jarFile.entries().asIterator();
            while( entryIt.hasNext() ) {
                final var entry= entryIt.next();

                // Only consider files that end with '.class'
                if( entry.isDirectory() || !entry.getName().toLowerCase().endsWith(".class") ) {
                    continue;
                }

                // Convert the path in the JAR (zip) file to a qualified class name
                final var entryName = entry.getName();
                final var fullClassName = entryName
                        .substring(0, entryName.length() - 6)
                        .replace('/', '.');

                // Load the class
                try {
                    final var clazz = classLoader.loadClass(fullClassName);

                    // Only consider IPluginExecutable classes
                    if (!IPluginExecutable.class.isAssignableFrom(clazz)) {
                        continue;
                    }

                    // Get the plugin's constructor
                    final var executableClazz = (Class<? extends IPluginExecutable>) clazz;
                    final var constructor = executableClazz.getConstructor();

                    // Add task that creates and executes the plugin
                    tasks.add(() -> {
                        try {
                            constructor.newInstance().execute();
                        } catch( ReflectiveOperationException e ) {
                            throw new RuntimeException(
                                    String.format("Could not create object of '%s'", fullClassName), e
                            );
                        }
                    });

                } catch( ClassNotFoundException e ) {
                    throw new RuntimeException( String.format("Could not load class entry '%s' from JAR file '%s'", fullClassName, file.getAbsolutePath()), e);

                } catch( NoSuchMethodException e ) {
                    throw new RuntimeException( String.format("Could not load constructor for class entry '%s' from JAR file '%s'", fullClassName, file.getAbsolutePath()), e);
                }
            }

            return tasks;

        } catch (IOException e) {
            throw new RuntimeException( String.format("Could not open JAR file '%s'", file.getAbsolutePath()), e);
        }
    }

    private void watchFilesAndHandleEvents() {
        final var lastModifiedTimes= new HashMap<Path, Long>();

        while( true ) {

            // Wait for the next
            WatchKey watchKey;
            try {
                watchKey= watchService.take();
            } catch( InterruptedException | ClosedWatchServiceException e ) {
                return;
            }

            // Check all available events
            final var basePath= (Path) watchKey.watchable();
            for( final var event: watchKey.pollEvents() ) {

                // Only handle JAR files
                final var path= basePath.resolve( (Path) event.context() );
                final var file= path.toFile();
                if( !file.isFile() || !file.getName().toLowerCase().endsWith(".jar") ) {
                    continue;
                }

                // Only handle file events when the file was not seen before, or the file was modified since last time
                if( lastModifiedTimes.containsKey(path) && lastModifiedTimes.get(path) >= file.lastModified() ) {
                    continue;
                }

                // Update last modified date
                lastModifiedTimes.put(path, file.lastModified());

                // Load all plugin tasks and run them in thr thread pool
                loadPluginTasks( file ).forEach( pluginThreadPool::submit );
            }

            watchKey.reset();
        }
    }
}
