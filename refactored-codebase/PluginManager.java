package edgeconvert;

import edgeconvert.input.FileParser;
import edgeconvert.output.DDLBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import net.xeoh.plugins.base.util.uri.ClassURI;

/**
 * Class responsible for loading in plugins for reading and writing files.
 */
public class PluginManager {

    private File pluginPath;
    private List<DDLBuilder> builders;
    private List<FileParser> parsers;

    /**
     * Construct a new PluginManager with a default plugin path.
     */
    public PluginManager() {
        // Get the path relative to the jar, no matter what
        String path = PluginManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String pluginLocation = new File(path).getParentFile().getAbsolutePath() + "/plugins";
        this.pluginPath = new File(pluginLocation);
        this.builders = new ArrayList<>();
        this.parsers = new ArrayList<>();
    }

    /**
     * Set the path from which plugins are loaded.
     * @param pluginPath The new location where plugins are found
     */
    public void setPluginPath(File pluginPath) {
        this.pluginPath = pluginPath;
    }

    /**
     * Get the path from which plugins are loaded.
     * @return The location where plugins are found
     */
    public File getPluginPath() {
        return this.pluginPath;
    }

    /**
     * Load plugins from the classpath and the plugin path and store them.
     */
    public void loadPlugins() {
        net.xeoh.plugins.base.PluginManager pm = PluginManagerFactory.createPluginManager();
        // Load plugins from the classpath and the plugin path
        pm.addPluginsFrom(ClassURI.CLASSPATH);
        pm.addPluginsFrom(pluginPath.toURI());
        PluginManagerUtil pmUtil = new PluginManagerUtil(pm);
        // Get all DDLBuilder plugins
        Collection<DDLBuilder> builderPlugins = pmUtil.getPlugins(DDLBuilder.class);
        this.builders = new ArrayList<>(builderPlugins);
        // Get all FileParser plugins
        Collection<FileParser> parserPlugins = pmUtil.getPlugins(FileParser.class);
        this.parsers = new ArrayList<>(parserPlugins);
    }

    /**
     * Get a list containing all DDLBuilder plugins found by the PluginManager.
     * @return All available DDLBuilders
     */
    public List<DDLBuilder> getBuilders() {
        return Collections.unmodifiableList(builders);
    }
    
    /**
     * Get a list containing all FileParser plugins found by the PluginManager.
     * @return All available FileParsers
     */
    public List<FileParser> getParsers() {
        return Collections.unmodifiableList(parsers);
    }
}
