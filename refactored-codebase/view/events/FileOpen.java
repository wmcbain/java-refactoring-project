package edgeconvert.view.events;

import edgeconvert.EdgeConvertMediator;
import edgeconvert.Schema;
import edgeconvert.input.FileParser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * File open listener class. Handles all related logic in file opening
 */
public class FileOpen implements ActionListener {

    // declarations
    private FileParser parser;
    private JFileChooser jfc;
    private String truncFileName;

    /**
     * Default constructor
     * Takes a parser argument to decide how to parse the file
     * @param parser the parser
     */
    public FileOpen(FileParser parser) {
        this.parser = parser;
        jfc = new JFileChooser();
        final String productName = parser.getProductName();
        final String fileExtension = parser.getFileExtension();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(productName, fileExtension);
        jfc.setFileFilter(filter);
    }

    /**
     * Overrides the action performed method
     * Handles file opening
     * @param e the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        EdgeConvertMediator edgeConvert = EdgeConvertMediator.getInstance(); // get mediator instance
        
        if (edgeConvert.isSchemaModified()) { // check for unsaved changes
            if (this.showUnsavedConfirmation() == JOptionPane.NO_OPTION) {
                return;
            }
        }

        // Show file chooser
        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

            File toParse = jfc.getSelectedFile(); 

            parser.setFile(toParse); // set file in parser
            final boolean readSuccess = parser.parseFile(); // parse file

            if (readSuccess) {
                Schema schema = parser.getSchema(); // get the schema from the parser
                edgeConvert.setSchema(schema); // set the schema in the mediator
                truncFileName = toParse.getName().substring(toParse.getName().lastIndexOf(File.separator) + 1); // get name of file
                edgeConvert.setDefineTablesTitle(truncFileName); // set the title
                edgeConvert.setDefineRelationsTitle(truncFileName);
                edgeConvert.showDefineTablesView(); // show the define tables view
            } else {
                showParseError(parser.getErrorMessage());
            }
        }
    }

    /**
     * Displayed if the user tries to exit with unsaved changes
     * @return yes/no
     */
    private int showUnsavedConfirmation() {
        return JOptionPane.showConfirmDialog(null, "You currently have unsaved data. Continue?",
                "Are you sure?", JOptionPane.YES_NO_OPTION);
    }

    /**
     * Shown when there is an error in parsing
     * @param message the message
     */
    private void showParseError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error while parsing file", JOptionPane.ERROR_MESSAGE);
    }
}
