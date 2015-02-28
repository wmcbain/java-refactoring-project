package edgeconvert;

import edgeconvert.input.FileParser;
import edgeconvert.output.DDLBuilder;
import edgeconvert.view.DefineRelationsView;
import edgeconvert.view.DefineTablesView;
import edgeconvert.view.EdgeConvertView;
import edgeconvert.view.events.FileSaver;
import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JOptionPane;

/**
 * Mediator for the EdgeConvert program. Allows state to be maintained across
 * the application without passing values back and forth and for distinct
 * components to communicate without knowing about each other. Singleton.
 */
public class EdgeConvertMediator implements Observer {

    private static EdgeConvertMediator instance;

    private final PluginManager pluginManager;
    private Schema schema;
    private EdgeConvertView defineTablesView, defineRelationsView;
    private boolean schemaModified;
    private File parseFile, saveFile;

    /**
     * Construct a mediator. This method is private because the mediator is
     * accessed via the static getInstance() method.
     */
    private EdgeConvertMediator() {
        this.pluginManager = new PluginManager();
        this.pluginManager.loadPlugins();
        this.schemaModified = false;
    }

    /**
     * Get the singleton instance of the mediator.
     *
     * @return An instance of EdgeConvertMediator
     */
    public static EdgeConvertMediator getInstance() {
        if (instance == null) {
            instance = new EdgeConvertMediator();
        }
        return instance;
    }

    /**
     * Get the schema currently open in the program.
     *
     * @return The active schema
     */
    public Schema getSchema() {
        return this.schema;
    }

    /**
     * Get a list of all known FileParsers.
     *
     * @return Every file parser found.
     */
    public List<FileParser> getParsers() {
        return this.pluginManager.getParsers();
    }

    /**
     * Get a list of all known DDLBuilders.
     *
     * @return every builder found
     */
    public List<DDLBuilder> getBuilders() {
        return this.pluginManager.getBuilders();
    }

    /**
     * Set the schema currently open in the program.
     *
     * @param schema The new active schema
     */
    public void setSchema(Schema schema) {
        if (this.schema != null) {
            this.schema.deleteObserver(this);
        }
        this.schema = schema;
        if (this.schema != null) {
            this.schema.addObserver(this);
            defineTablesView.populate();
            defineRelationsView.populate();
        }
        schemaModified = false;
    }

    /**
     * Determine whether or not the schema has been modified since it became
     * active.
     *
     * @return Whether or not the schema is modified
     */
    public boolean isSchemaModified() {
        return schemaModified;
    }

    /**
     * Set the DefineTablesView for the program.
     *
     * @param defineTablesView The DefineTablesView to show
     */
    public void setDefineTablesView(DefineTablesView defineTablesView) {
        this.defineTablesView = defineTablesView;
    }

    /**
     * Set the DefineRelationsView for the program.
     *
     * @param defineRelationsView The DefineRelationsView to show
     */
    public void setDefineRelationsView(DefineRelationsView defineRelationsView) {
        this.defineRelationsView = defineRelationsView;
    }

    /**
     * Show the DefineTablesView.
     */
    public void showDefineTablesView() {
        this.defineRelationsView.setVisible(false);
        this.defineTablesView.setVisible(true);
    }

    /**
     * Show the DefineRelationsView.
     */
    public void showDefineRelationsView() {
        this.defineTablesView.setVisible(false);
        this.defineRelationsView.setVisible(true);
    }

    /**
     * Set the title of the DefineTablesView.
     *
     * @param title The view's new title
     */
    public void setDefineTablesTitle(String title) {
        this.defineTablesView.setTitle("Define Tables - " + title);
    }

    /**
     * Set the title of the DefineRelationsView.
     *
     * @param title The view's new title
     */
    public void setDefineRelationsTitle(String title) {
        this.defineRelationsView.setTitle("Define Relations - " + title);
    }

    /**
     * Get the directory from which plugins are being loaded.
     *
     * @return The current plugin directory
     */
    public File getPluginDir() {
        return pluginManager.getPluginPath();
    }

    /**
     * Get the directory from which plugins are loaded and load plugins from it.
     *
     * @param pluginDir The new plugin directory
     */
    public void setPluginDir(File pluginDir) {
        pluginManager.setPluginPath(pluginDir);
        pluginManager.loadPlugins();
        defineTablesView.populateOpenMenu();
        defineRelationsView.populateOpenMenu();
    }

    /**
     * Get the file opened by the program.
     *
     * @return The last file the program opened
     */
    public File getParseFile() {
        return parseFile;
    }

    /**
     * Set the file opened by the program.
     *
     * @param parseFile The last file the program opened
     */
    public void setParseFile(File parseFile) {
        this.parseFile = parseFile;
    }

    /**
     * Get the location of the save file last saved or loaded.
     *
     * @return The save file's location
     */
    public File getSaveFile() {
        return saveFile;
    }

    /**
     * Set the location of the save file.
     *
     * @param saveFile The save file's location
     */
    public void setSaveFile(File saveFile) {
        this.saveFile = saveFile;
    }

    /**
     * If the schema being observed by the mediator changes, set the
     * schemaModified attribute to true.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        schemaModified = true;
    }

    /**
     * Exit the program. If there are unsaved changes, prompt the user if they
     * want to save, not save, or cancel.
     */
    public void close() {
        if (isSchemaModified()) {
            int response = JOptionPane.showOptionDialog(
                    null,
                    "You currently have unsaved data. Would you like to save?",
                    "Are you sure?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    null
            );

            switch (response) {
                case JOptionPane.YES_OPTION:
                    // Only exit after a successful save
                    if (save()) {
                        System.exit(0);
                    }
                    break;
                case JOptionPane.NO_OPTION:
                    System.exit(0);
                case JOptionPane.CANCEL_OPTION:
                // Do nothing
                default:
                // Do nothing
            }
        } else {
            System.exit(0);
        }
    }

    /**
     * Save the currently open schema.
     *
     * @return Whether or not the schema was saved successfully
     */
    public boolean save() {
        boolean success = new FileSaver().save();
        // If successful, schemaModified is false. Else, schemaModified is left alone.
        schemaModified &= !success;
        return success;
    }
}
