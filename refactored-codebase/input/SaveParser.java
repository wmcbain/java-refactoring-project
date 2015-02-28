/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeconvert.input;

import edgeconvert.DataType;
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
import java.util.StringTokenizer;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * FileParser for EdgeConvert save files.
 */
@PluginImplementation
public class SaveParser implements FileParser {

    // Decalarations
    private File inputFile;
    private BufferedReader br;
    private List<EdgeTable> tables;
    private List<EdgeField> fields;
    private String currentLine, errorMessage;

    // static/final attributes
    public static final String PARSE_TYPE = "EdgeConvert Save File"; // first line of save files should be this
    public static final String FILE_EXTENSION = "sav"; // totally incorrect, need to look into the save file type
    public static final String DELIM = "|";

    /**
     * Construct a new SaveParser.
     */
    public SaveParser() {
        inputFile = null;
        br = null;
        tables = new ArrayList<>();
        fields = new ArrayList<>();
        currentLine = null;
        errorMessage = "";
    }

    /**
     * Gets the product name
     * @return the product name
     */
    @Override
    public String getProductName() {
        return PARSE_TYPE;
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
     * Sets the file to be parsed
     * @param inputFile the file
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
        // check
        if (inputFile == null) {
            return false;
        }

        try {
            // Instantiate BufferedReader
            br = new BufferedReader(new FileReader(inputFile));
            // Ensure we have the correct file type, and parse
            if (br.readLine().trim().startsWith(PARSE_TYPE)) {
                this.readLines(1); // save file has two lines after the parse type
            }
        } catch (IOException ioe) {
            return false;
        }
        return true;
    }

    /**
     * Parses the save file
     * @return success/failure
     */
    @Override
    public boolean parseFile() {
        br = null;
        tables = new ArrayList<>();
        fields = new ArrayList<>();
        currentLine = null;
        errorMessage = "";

        if (!checkFile()) { // check file for correct type
            return false; 
        }
        try {
            while ((currentLine = br.readLine()).startsWith("Table: ")) { // read and make table while true
                this.makeTable();
            }
            while ((currentLine = br.readLine()) != null) { // then make fields
                this.makeField();
            }
        } catch (IOException ioe) {
            return false;
        }
        return true;
    }

    /**
     * Gets the schema
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
    public List<Table> getTables() {
        Map<Integer, Table> parsedTables = new HashMap<>();
        Map<Integer, Field> parsedFields = new HashMap<>();

        for (EdgeTable table : tables) { // iterate through the tables
            
            Table parsedTable = new Table(table.getName()); // get the name
            
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

        // Add fields and attributes
        for (EdgeField field : fields) {
            Field parsedField = parsedFields.get(field.getNumFigure());
            if (parsedField == null) {
                parsedField = new Field(field.getName());
                parsedField.setAllowNull(!field.getDisallowNull());
                parsedField.setIsPrimaryKey(field.getIsPrimaryKey());
                parsedFields.put(field.getNumFigure(), parsedField);
            }
        }
        return new ArrayList<>(parsedFields.values());
    }

    /**
     * Gets the error message
     * 
     * @return the error message
     */
    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Makes a table from the save file
     *
     * @throws IOException throws up the stack
     */
    public void makeTable() throws IOException {
        int numFigure, numFields, numTables;
        String tableName;
        EdgeTable tempTable;
        StringTokenizer stNatFields, stTables, stRelFields;

        numFigure = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1)); // get the number of figures
        this.readLines(2); // skip two lines
        tableName = currentLine.substring(currentLine.indexOf(" ") + 1); // get the table name
        tempTable = new EdgeTable(tableName, numFigure); // create the table

        stNatFields = this.splitRow(br.readLine()); // native fields
        numFields = stNatFields.countTokens(); // the field count
        for (int i = 0; i < numFields; i++) { // iterate and add to table
            tempTable.addNativeField(Integer.parseInt(stNatFields.nextToken()));
        }

        stTables = this.splitRow(br.readLine()); // related tables
        numTables = stTables.countTokens(); // related tables count
        for (int i = 0; i < numTables; i++) { // iterate and add
            tempTable.addRelatedTable(Integer.parseInt(stTables.nextToken()));
        }
        tempTable.makeArrays(); // make arrays

        stRelFields = this.splitRow(br.readLine()); // related fields
        numFields = stRelFields.countTokens(); // count related fields
        for (int i = 0; i < numFields; i++) { // iterate and add
            tempTable.setRelatedField(i, Integer.parseInt(stRelFields.nextToken()));
        }
        tables.add(tempTable); // add the table to the tables
        this.readLines(1); // skip for next read
    }

    /**
     * Makes a field from the save file
     *
     * @throws IOException throws Exception up the stack
     */
    public void makeField() throws IOException {
        int numFigure;
        String fieldName;
        EdgeField tempField;
        StringTokenizer stField;

        stField = new StringTokenizer(currentLine, DELIM); // split string into tokens using the deliminter 
        if(stField.countTokens() == 0) return; // check to see if tokens generated, return if false
        
        numFigure = Integer.parseInt(stField.nextToken());
        fieldName = stField.nextToken();
        tempField = new EdgeField(fieldName, numFigure);
        tempField.setTableID(Integer.parseInt(stField.nextToken()));
        
        // These values may be null, so read and check
        String fieldBound = stField.nextToken();
        String tableBound = stField.nextToken();
        if(!fieldBound.equals("null")) {
            tempField.setFieldBound(Integer.parseInt(fieldBound));
            tempField.setTableBound(Integer.parseInt(tableBound));
        }
        
        tempField.setDataType(Integer.parseInt(stField.nextToken()));
        tempField.setVarcharValue(Integer.parseInt(stField.nextToken()));
        tempField.setIsPrimaryKey(Boolean.valueOf(stField.nextToken()));
        tempField.setDisallowNull(Boolean.valueOf(stField.nextToken()));
        
        if (stField.hasMoreTokens()) { //Default Value may not be defined
            tempField.setDefaultValue(stField.nextToken());
        }
        fields.add(tempField);
    }

    /**
     * Reads line to a certain point
     *
     * @param count
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
     * Splits the line being read by the delimiter
     *
     * @param currentLine the line being read
     * @return Tokenized string
     */
    private StringTokenizer splitRow(String currentLine) {
        return new StringTokenizer(currentLine.substring(currentLine.indexOf(" ") + 1), DELIM);
    }
}
