package edgeconvert.input;

import edgeconvert.Schema;
import java.io.File;
import net.xeoh.plugins.base.Plugin;

/**
 * Interface used with file parsing when a file is selected from the "File" menu
 * 
 * @author wyattmcbain
 */
public interface FileParser extends Plugin {
    
    /**
     * Gets the name of the kind file that can be parsed.
     * @return 
     */
    public String getProductName();
    
    /**
     * Gets the file extension of the type of file that can be parsed.
     * @return 
     */
    public String getFileExtension();
    
    /**
     * Sets the file to be parsed
     * @param inputFile The file to be parsed
     */
    public void setFile(File inputFile);
    
    /**
     * Parses the file after the file is opened.
     * @return Whether or not the file was parsed successfully
     */
    public boolean parseFile();

    /**
     * Get the Schema parsed from the file.
     * @return The schema from the file.
     */
    public Schema getSchema();
    
    /**
     * If a problem occurred while parsing, return the cause of the problem.
     * @return An explanation of why parsing failed
     */
    public String getErrorMessage();
        
}
