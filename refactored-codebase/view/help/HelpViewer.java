package edgeconvert.view.help;

import java.io.File;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 * Launcher and GUI class for the help system
 */
public class HelpViewer {
    
    // declarations
    private static URL url;
    
    /**
     * Default constructor
     * Initializes the url
     */
    public HelpViewer() {
        url = this.getClass().getResource("site/index.html");
    }
    
    /**
     * Initializes the panel and launches the GUI
     */
    public static void initAndShowGUI() {
         JFrame jf = new JFrame("Help Desk");
         jf.setLocationRelativeTo(null);
         jf.setSize(900, 600);
         final JFXPanel fxPanel = new JFXPanel();
         jf.add(fxPanel);
         jf.setVisible(true);
         
         Platform.runLater(new Runnable() {
             @Override
             public void run() {
                 initFX(fxPanel);
             }
         });
    }
    
    /**
     * Inits the JavaFX panel and creates the web view
     * @param fxPanel the JavaFX panel
     */
    public static void initFX(JFXPanel fxPanel) {
        WebView webview = new WebView(); // create the web view
        webview.getEngine().load(url.toString()); // load the local web page
        fxPanel.setScene(new Scene(webview));
    }
}