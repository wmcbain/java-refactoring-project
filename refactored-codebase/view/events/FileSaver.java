package edgeconvert.view.events;

import edgeconvert.EdgeConvertMediator;
import edgeconvert.output.DDLBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Class that handles file saving when creating a ddl is executed
 */
public class FileSaver {

    // declarations
    private String sqlString;
    private DDLBuilder builder;
    private List<String> productNames;
    private List<DDLBuilder> builders;
    private EdgeConvertMediator mediator;

    /**
     * Saves the files
     * @return boolean whether the file was saved or not
     */
    public boolean save() {
        mediator = EdgeConvertMediator.getInstance(); // get the mediator
        getBuilderClasses(); // get the ddl builder classes
        sqlString = getDDL(); // gets the created ddl
        if (sqlString.equals("CANCELLED")) {
            return false;
        }
        return writeDDL(sqlString); // writes the ddl
    }
    
    /**
     * Gets the builder classes from the mediator
     */
    private void getBuilderClasses() {
        productNames = new ArrayList<>();
        builders = mediator.getBuilders();

        for (DDLBuilder ddlBuilder : builders) { // iterate and add to the product name list
            productNames.add(ddlBuilder.getProductName());
        }
    }

    /**
     * Displays a prompt to have the user define the save type
     * @return 
     */
    private String getDDL() {
        String response = (String) JOptionPane.showInputDialog(
                null,
                "Select a product:",
                "Create DDL",
                JOptionPane.PLAIN_MESSAGE,
                null,
                productNames.toArray(),
                null); // prompt

        if (response == null) return "CANCELLED"; // return cancel if cancelled

        int selected;
        for (selected = 0; selected < productNames.size(); selected++) { // get the product name
            if (response.equals(productNames.get(selected))) {
                break;
            }
        }

        builder = builders.get(selected); // get the builder
        builder.setTables(mediator.getSchema().getTables()); // set tables in the builder
        if (builder.requiresDatabaseName()) { // check to see if database name is required
            String databaseName = getDatabaseName(); // ask for database name
            if (databaseName == null) {
                return "CANCELLED";
            }
            builder.setDatabaseName(databaseName);// set the database name
        }
        return builder.buildDDL(); // build ddl
    }

    /**
     * Writes the DDL to a save file
     * @param output the output to be saved
     * @return success/failure
     */
    private boolean writeDDL(String output) {
        JOptionPane.showMessageDialog(null, "Please select the directory and name of the file you'd like to save."); // show message
        
        JFileChooser jfc = new JFileChooser(); // init file chooser
        final String productName = builder.getProductName(); // get product name and extension
        final String fileExtension = builder.getFileExtension();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(productName, fileExtension); // get the file format filter
        jfc.setFileFilter(filter); // set the file filter
        
        if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) { // show the file dialog
            File outputFile = jfc.getSelectedFile();
            String filename = outputFile.getAbsolutePath();
            String extension = "." + builder.getFileExtension();
            if(!filename.endsWith(extension)) { // see if file name ends with extension
                outputFile = new File(filename + extension);
            }
            if (outputFile.exists()) { // check if file already exists, prompt with overwrite dialog if so
                int response = JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "Confirm Overwrite",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.CANCEL_OPTION) {
                    return false;
                }
            }
            try { // write the file
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, false)));
                pw.println(output);
                //close the file
                pw.close();
                JOptionPane.showMessageDialog(null, "Your file has been saved successfully.");
                return true;
            } catch (IOException ioe) {
                System.out.println(ioe);
                return false;
            }
        }
        return false;
    }

    /**
     * gets the database name
     * @return the database name
     */
    private String getDatabaseName() {
        String name = "";
        while (name.length() == 0) {
            name = JOptionPane.showInputDialog("Enter a name for the database");
            if (name == null) {
                return null;
            }
            name = name.trim();
        }
        return name;
    }
}
