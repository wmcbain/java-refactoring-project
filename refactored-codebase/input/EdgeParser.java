package edgeconvert.input;

import edgeconvert.DataType;
import edgeconvert.EdgeConnector;
import edgeconvert.EdgeField;
import edgeconvert.EdgeTable;
import edgeconvert.Field;
import edgeconvert.Schema;
import edgeconvert.Table;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * FileParser for Edge Diagrammer files.
 */
@PluginImplementation
public class EdgeParser implements FileParser {

    // Decalarations
    private File inputFile;
    private BufferedReader br;
    private List<EdgeTable> tables;
    private List<EdgeField> fields;
    private List<EdgeConnector> connectors;
    private String currentLine, errorMessage;

    // static/final attributes
    public static final String PRODUCT_NAME = "EDGE Diagram File"; //first line of .edg files should be this
    public static final String FILE_EXTENSION = "edg";

    /**
     * Construct a new EdgeParser.
     */
    public EdgeParser() {
        inputFile = null;
        br = null;
        tables = new ArrayList<>();
        fields = new ArrayList<>();
        connectors = new ArrayList<>();
        currentLine = null;
        errorMessage = "";
    }

    /**
     * Gets the product name
     * @return the product name
     */
    @Override
    public String getProductName() {
        return PRODUCT_NAME;
    }

    /**
     * Gets the file extension
     * @return the file extension
     */
    @Override
    public String getFileExtension() {
        return FILE_EXTENSION;
    }

    /**
     * Sets the file for parsing
     * @param inputFile 
     */
    @Override
    public void setFile(File inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * Checks to ensure that the proper file type is being used.
     *
     * @return valid/invalid
     */
    public boolean checkFile() {
        if (inputFile == null) { // ensure file exists
            return false;
        }

        try {
            // Instantiate BufferedReader
            br = new BufferedReader(new FileReader(inputFile));
            // Ensure we have the correct file type, and parse
            if (br.readLine().trim().startsWith(PRODUCT_NAME)) {
                return true;
            }
        } catch (IOException ioe) {
            return false;
        }
        return false;
    }

    /**
     * Parses the edge file
     * @return success/failure
     */
    @Override
    public boolean parseFile() {
        br = null;
        tables = new ArrayList<>();
        fields = new ArrayList<>();
        connectors = new ArrayList<>();
        currentLine = null;
        errorMessage = "";

        if (!checkFile()) {
            return false; // check file before beginning
        }
        try {
            // loop through file
            while ((currentLine = br.readLine()) != null) { 
                if (currentLine.startsWith("Figure ")) { // Line is a figure
                    if (!this.parseFigure()) {
                        br.close();
                        return false;
                    }
                }
                if (currentLine.startsWith("Connector ")) { // Line is a connector
                    if (!this.parseConnector()) {
                        br.close();
                        return false;
                    }
                }
            }
            br.close();
            return this.resolveConnectors(); // once read resolve connectors and return success
        } catch (IOException ioe) {
            return false;
        }
    }

    /**
     * Gets the schema
     * 
     * @return the schema
     */
    @Override
    public Schema getSchema() {
        return new Schema(getTables());
    }

    /**
     * Get a list of parsed tables.
     *
     * @return The tables parsed by this parser
     */
    private List<Table> getTables() {
        Map<Integer, Table> parsedTables = new HashMap<>();
        Map<Integer, Field> parsedFields = new HashMap<>();

        for (EdgeTable table : tables) { // iterate through the tables
            Table parsedTable = new Table(table.getName()); // get the table
            
            // Add related tables and attributes
            for (int tableId : table.getRelatedTables()) {
                Table preparsedTable = parsedTables.get(tableId);
                if (preparsedTable != null) {
                    parsedTable.addRelatedTable(preparsedTable);
                    preparsedTable.addRelatedTable(parsedTable);
                }
            }
            
            // Add fields and attributes
            for (EdgeField field : fields) {
                if (table.getNumFigure() == field.getTableID()) {
                    Field parsedField = parsedFields.get(field.getNumFigure());
                    if (parsedField == null) {
                        parsedField = new Field(field.getName());
                        parsedField.setAllowNull(!field.getDisallowNull());
                        parsedField.setIsPrimaryKey(field.getIsPrimaryKey());
                        parsedField.setDataType(DataType.VARCHAR);
                        parsedFields.put(field.getNumFigure(), parsedField);
                    }
                    parsedField.setTable(parsedTable);
                    parsedTable.addField(parsedField);
                }
            }
            parsedTables.put(table.getNumFigure(), parsedTable);
        }
        return new ArrayList<>(parsedTables.values());
    }

    /**
     * Get a list of parsed fields.
     *
     * @return The fields parsed by this parser
     */
    public List<Field> getFields() {
        Map<Integer, Field> parsedFields = new HashMap<>();

        for (EdgeField field : fields) { // iterate throguh the fields
            Field parsedField = parsedFields.get(field.getNumFigure()); // get the field
            if (parsedField == null) { // set attributes if not null
                parsedField = new Field(field.getName());
                parsedField.setAllowNull(!field.getDisallowNull());
                parsedField.setIsPrimaryKey(field.getIsPrimaryKey());
                parsedField.setDataType(DataType.VARCHAR);
                parsedFields.put(field.getNumFigure(), parsedField);
            }
        }
        return new ArrayList<>(parsedFields.values());
    }

    /**
     * Gets the error message
     * @return the error message
     */
    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Creates fields for figures in the file adds them to a list
     *
     * @return Whether or not parsing is going well.
     * @throws IOException
     */
    private boolean parseFigure() throws IOException {
        int figureId = parseId(); //get the Figure id
        readLines(2); // Read over { to Style
        if (!currentLine.startsWith("Style")) { // this is to weed out other Figures, like Labels
            return true;
        } else {
            String style = getParameter(); //get the Style parameter
            if (style.startsWith("Relation")) { //presence of Relations implies lack of normalization
                // TODO: Indicate failure to parse here. Relations present.
                this.errorMessage = "The Edge Diagrammer file\n" + inputFile + "\ncontains relations.  Please resolve them and try again.";
                return false;
            }
            final boolean isEntity = style.startsWith("Entity");
            final boolean isAttribute = style.startsWith("Attribute");
            if (!isEntity && !isAttribute) { //these are the only Figures we're interested in
                return true;
            }
            currentLine = br.readLine().trim(); //this should be Text (Figure name)
            String name = getParameter().replaceAll(" ", ""); //get the Text parameter
            if (name.equals("")) {
                this.errorMessage = "There are entities or attributes with blank names in this diagram.\nPlease provide names for them and try again.";
                return false;
            }

            // Trim off everything after the escape character
            name = (name.indexOf("\\") > 0) ? name.substring(0, name.indexOf("\\")) : name;

            boolean isUnderlined = checkUnderline();

            if (isEntity) { //create a new EdgeTable object and add it to the alTables ArrayList
                if (isDuplicateTable(name)) {
                    this.errorMessage = "There are multiple tables called " + name + " in this diagram.\nPlease rename all but one of them and try again.";
                    return false;
                }
                tables.add(new EdgeTable(name, figureId));
            }
            if (isAttribute) { //create a new EdgeField object and add it to the alFields ArrayList
                EdgeField field = new EdgeField(name, figureId);
                field.setIsPrimaryKey(isUnderlined);
                fields.add(field);
            }
        }
        return true;
    }

    /**
     * Creates a connector
     *
     * @throws IOException
     */
    private boolean parseConnector() throws IOException {
        int connectorId = parseId(); //get the Connector number
        readLines(3); // Read over { and Style to Figure1
        int figureId1 = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1));
        currentLine = br.readLine().trim(); // Figure2
        int figureId2 = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1));
        readLines(5); // Read over EndPoint1, EndPoint2, SuppressEnd1, and SuppressEnd2 to End1
        String endStyle1 = getParameter(); //get the End1 parameter
        currentLine = br.readLine().trim(); // End2
        String endStyle2 = getParameter(); //get the End2 parameter

        // Read to end of block
        do { //advance to end of record
            currentLine = br.readLine().trim();
        } while (!currentLine.equals("}")); // this is the end of a Connector entry

        connectors.add(new EdgeConnector(connectorId, figureId1, figureId2, endStyle1, endStyle2));
        return true;
    }

    /**
     * Resolves the edge connectors
     *
     * @return
     * @throws java.io.IOException
     */
    private boolean resolveConnectors() throws IOException {
        int endPoint1, endPoint2;
        for (EdgeConnector connector : connectors) {
            endPoint1 = connector.getEndPoint1();
            endPoint2 = connector.getEndPoint2();
            EdgeField field = null;
            EdgeTable table1 = null;
            EdgeTable table2 = null;
            for (EdgeField f : fields) { //search fields array for endpoints
                if (endPoint1 == f.getNumFigure()) { //found endPoint1 in fields array
                    connector.setIsEP1Field(true); //set appropriate flag
                    field = f; //identify which element of the fields array that endPoint1 was found in
                }
                if (endPoint2 == f.getNumFigure()) { //found endPoint2 in fields array
                    connector.setIsEP2Field(true); //set appropriate flag
                    field = f; //identify which element of the fields array that endPoint2 was found in
                }
            }
            for (EdgeTable table : tables) { //search tables array for endpoints
                if (endPoint1 == table.getNumFigure()) { //found endPoint1 in tables array
                    table1 = table; //identify which element of the tables array that endPoint1 was found in
                }
                if (endPoint2 == table.getNumFigure()) { //found endPoint1 in tables array
                    table2 = table; //identify which element of the tables array that endPoint2 was found in
                }
            }

            if (connector.getIsEP1Field() && connector.getIsEP2Field()) { //both endpoints are fields, implies lack of normalization
                this.errorMessage = "The Edge Diagrammer file\n" + inputFile + "\ncontains composite attributes. Please resolve them and try again.";
                return false; //stop processing list of Connectors
            }

            if (table1 != null && table2 != null) { //both endpoints are tables
                if ((connector.getEndStyle1().contains("many"))
                        && (connector.getEndStyle2().contains("many"))) { //the connector represents a many-many relationship, implies lack of normalization
                    this.errorMessage = "There is a many-many relationship between tables\n\"" + table1.getName() + "\" and \"" + table2.getName() + "\"" + "\nPlease resolve this and try again.";
                    return false; //stop processing list of Connectors
                } else { //add Figure number to each table's list of related tables
                    table1.addRelatedTable(table2.getNumFigure());
                    table2.addRelatedTable(table1.getNumFigure());
                    continue; //next Connector
                }
            }

            if (field != null && field.getTableID() == 0) { //field has not been assigned to a table yet
                if (table1 != null) { //endpoint1 is the table
                    table1.addNativeField(field.getNumFigure()); //add to the appropriate table's field list
                    field.setTableID(table1.getNumFigure()); //tell the field what table it belongs to
                } else if (table2 != null) { //endpoint2 is the table
                    table2.addNativeField(field.getNumFigure()); //add to the appropriate table's field list
                    field.setTableID(table2.getNumFigure()); //tell the field what table it belongs to
                }
            } else if (field != null) { //field has already been assigned to a table
                this.errorMessage = "The attribute " + field.getName() + " is connected to multiple tables.\nPlease resolve this and try again.";
                return false; //stop processing list of Connectors
            }
        } // connectors for() loop
        return true;
    }

    /**
     * Parse the figure ID on the current line
     *
     * @return The parsed ID
     */
    private int parseId() {
        return Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1));
    }

    /**
     * Gets the parameter from the current line.
     *
     * @return The parsed parameter
     */
    private String getParameter() {
        return currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\""));
    }

    /**
     * Reads a certain number of lines forward
     *
     * @param count The number of lines to skip forward
     * @throws IOException
     */
    private void readLines(int count) throws IOException {
        // Loop through the extra lines quickly
        for (int i = 0; i < count - 1; i++) {
            br.readLine();
        }
        // Trim and store the final line
        currentLine = br.readLine().trim();
    }

    /**
     * Checks for duplicate tables
     *
     * @param tableName
     * @return
     */
    private boolean isDuplicateTable(String tableName) {
        for (EdgeTable table : tables) {
            if (table.getName().equals(tableName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if a particular line is underlined
     *
     * @return
     * @throws IOException
     */
    private boolean checkUnderline() throws IOException {
        boolean isUnderlined = false;
        do { //advance to end of record, look for whether the text is underlined
            currentLine = br.readLine().trim();
            if (currentLine.startsWith("TypeUnderl")) {
                isUnderlined = true;
            }
        } while (!currentLine.equals("}")); // this is the end of a Figure entry
        return isUnderlined;
    }
}
