/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeconvert.view.events;

import edgeconvert.EdgeConvertMediator;
import edgeconvert.output.DDLBuilder;
import edgeconvert.view.help.HelpViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Listener for the common menu bar.
 */
public class MainMenuListener implements ActionListener {

    // declarations
    private JFrame frame;

    /**
     * Default constructor, initializes frame
     * @param frame the frame to be initialized
     */
    public MainMenuListener(JFrame frame) {
        this.frame = frame;
    }

    /**
     * Overrides the action performed method
     * Takes an action and decides what method to call
     * 
     * @param e the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) { // swicth for the command executed
            case "Exit":
                this.exit();
                break;
            case "Set Plugin Directory...":
                this.setPluginLocation();
                break;
            case "Show Database Products Available":
                this.showProducts();
                break;
            case "Help Desk":
                this.showHelp();
                break;
            case "About":
                this.showAbout();
                break;
            default:
                break;
        }
    }

    /**
     * Called when exiting, closes the mediator class
     */
    public void exit() {
        EdgeConvertMediator mediator = EdgeConvertMediator.getInstance();
        mediator.close();
    }

    /**
     * Sets the plug in location, for user added plug ins
     */
    public void setPluginLocation() {
        EdgeConvertMediator mediator = EdgeConvertMediator.getInstance(); // gets the mediator
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(mediator.getPluginDir());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File directory = fileChooser.getSelectedFile();
            mediator.setPluginDir(directory); // sets the plug in directory
        }
    }

    /**
     * Shows the available products
     */
    public void showProducts() {
        EdgeConvertMediator mediator = EdgeConvertMediator.getInstance(); // calls the mediator
        String message = "The available products to create DDL statements are:\n";
        for (DDLBuilder builder : mediator.getBuilders()) { // get the builders and create a list
            message += builder.getProductName() + "\n";
        }
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Launches the help system
     */
    public void showHelp() {
        try {
            new HelpViewer();
            HelpViewer.initAndShowGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches the about screen
     */
    public void showAbout() {
        JOptionPane.showMessageDialog(null, "EdgeConvert ERD To DDL Conversion Tool\n"
                + "by Stephen A. Capperell\n"
                + "\u00A9 2007-2008\n"
                + "Refactored and improved by David Crocker and Wyatt McBain\n"
                + "2014"
        );
    }

    /**
     * Displayed when the user tries to exit the program with unsaved changes
     * @return approve/cancel
     */
    public int showUnsavedChangesDialog() {
        return JOptionPane.showOptionDialog(null,
                "You currently have unsaved data. Would you like to save?",
                "Are you sure?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, null, null);
    }
}
