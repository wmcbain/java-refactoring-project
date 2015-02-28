package edgeconvert.view;

import edgeconvert.EdgeConvertMediator;
import edgeconvert.Schema;
import edgeconvert.input.FileParser;
import edgeconvert.view.events.FileOpen;
import edgeconvert.view.events.MainMenuListener;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Class extended by all views in the program. Defines common behaviors and
 * implements a common menu bar.
 */
public abstract class EdgeConvertView extends JFrame {

    // public static/final attributes
    public static final int HORIZ_SIZE = 805;
    public static final int VERT_SIZE = 400;
    public static final int HORIZ_LOC = 100;
    public static final int VERT_LOC = 100;
    public static final String DEFINE_TABLES = "Define Tables";
    public static final String DEFINE_RELATIONS = "Define Relations";

    // declarations
    private JMenuBar menuBar;
    private JMenu menuFile, menuOptions, menuHelp, menuOpen;
    private JMenuItem menuItemExit, menuItemPluginLocation, menuItemShowProducts,
            menuItemAbout, menuItemHelpDesk;

    /**
     * Build the common menu bar and add it to the view.
     */
    public EdgeConvertView() {
        this.setSize(HORIZ_SIZE, VERT_SIZE);
        this.setLocation(HORIZ_LOC, VERT_LOC);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        buildGUI();
        addListeners();
        populateOpenMenu();
    }
    
    /**
     * Populates the open menu with each input file type
     */
    public final void populateOpenMenu() {
        menuOpen.removeAll();
        // Get a list containing a JMenuItem for each type of input file.
        List<JMenuItem> openItems = getOpenItems();
        // Add each item in the list to the "Open" menu.
        for (JMenuItem item : openItems) {
            menuOpen.add(item);
        }
    }

    /**
     * Instantiate all GUI components and build the GUI.
     */
    private void buildGUI() {
        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);

        menuBar.add(menuFile);

        menuOpen = new JMenu("Open");

        menuItemExit = new JMenuItem("Exit");
        menuItemExit.setMnemonic(KeyEvent.VK_X);

        menuFile.add(menuOpen);
        menuFile.add(menuItemExit);

        menuOptions = new JMenu("Options");
        menuOptions.setMnemonic(KeyEvent.VK_O);

        menuBar.add(menuOptions);

        menuItemPluginLocation = new JMenuItem("Set Plugin Directory...");
        menuItemPluginLocation.setMnemonic(KeyEvent.VK_S);

        menuItemShowProducts = new JMenuItem("Show Database Products Available");
        menuItemShowProducts.setMnemonic(KeyEvent.VK_H);
        menuItemShowProducts.setEnabled(true);

        menuOptions.add(menuItemPluginLocation);
        menuOptions.add(menuItemShowProducts);

        menuHelp = new JMenu("Help");
        menuHelp.setMnemonic(KeyEvent.VK_H);

        menuBar.add(menuHelp);

        menuItemAbout = new JMenuItem("About");
        menuItemAbout.setMnemonic(KeyEvent.VK_A);

        menuItemHelpDesk = new JMenuItem("Help Desk");
        menuItemHelpDesk.setMnemonic(KeyEvent.VK_H);

        menuHelp.add(menuItemAbout);
        menuHelp.add(menuItemHelpDesk);
    }

    /**
     * Add listeners to GUI components.
     */
    private void addListeners() {
        this.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent we) {
                        EdgeConvertMediator mediator = EdgeConvertMediator.getInstance();
                        mediator.close();
                    }
                }
        );

        MainMenuListener menuListener = new MainMenuListener(this);
        menuItemExit.addActionListener(menuListener);
        menuItemPluginLocation.addActionListener(menuListener);
        menuItemShowProducts.addActionListener(menuListener);
        menuItemAbout.addActionListener(menuListener);
        menuItemHelpDesk.addActionListener(menuListener);
    }

    /**
     * Get a list containing a JMenuItem, complete with listener, for each
     * supported input file type.
     *
     * @return The list of JMenuItems
     */
    private List<JMenuItem> getOpenItems() {
        EdgeConvertMediator mediator = EdgeConvertMediator.getInstance();
        List<JMenuItem> menuItems = new ArrayList<>();
        List<FileParser> parsers = mediator.getParsers();
        // Make a JMenuItem for each FileParser
        for (FileParser parser : parsers) {
            JMenuItem item = new JMenuItem(parser.getProductName());
            ActionListener listener = new FileOpen(parser);
            item.addActionListener(listener);
            menuItems.add(item);
        }
        return menuItems;
    }

    /**
     * Update the information displayed on the view based on the state of the
     * mediator.
     */
    public abstract void populate();
}
