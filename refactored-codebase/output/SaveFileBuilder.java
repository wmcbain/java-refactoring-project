package edgeconvert.output;

import edgeconvert.Field;
import edgeconvert.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * DDLBuilder for EdgeConvert save files.
 */
@PluginImplementation
public class SaveFileBuilder implements DDLBuilder {

    // private static/final attributes
    private static final String PRODUCT_NAME = "Edge Convert Save";
    private static final String FILE_EXTENSION = "sav";

    // declarations
    private Map<Table, Integer> tableIds;
    private Map<Field, Integer> fieldIds;

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
     * Sets the data to be built
     * @param tables the tables to be built
     */
    @Override
    public void setTables(List<Table> tables) {
        tableIds = new HashMap<>();
        fieldIds = new HashMap<>();
        int idIncrement = 1;
        
        for (Table table : tables) { // iterate through the tables
            tableIds.put(table, idIncrement); // add the table to the table ids
            idIncrement++;
            
            List<Field> fields = table.getFields(); // get the fields
            for (Field field : fields) { // iterate through the fields
                fieldIds.put(field, idIncrement); // put the fields
                idIncrement++;
            }
        }
    }

    /**
     * Sets the database name
     * No functionality in this class
     * 
     * @param dbName 
     */
    @Override
    public void setDatabaseName(String dbName) {
        // No action needed
    }

    /**
     * Build the DDL
     * 
     * @return the ddl string
     */
    @Override
    public String buildDDL() {
        StringBuilder sb = new StringBuilder(); // create a stringbuilder
        sb.append("EdgeConvert Save File\n"); // append the header
        sb.append("#Tables#\n"); // append tables header
        
        List<Table> tables = new ArrayList<>(tableIds.keySet()); // get the tables
        for (Table table : tables) { // iterate through the tables
            sb.append(buildTable(table)); // build table string
            sb.append("\n");
        }
        sb.append("#Fields#\n"); // append field 
        
        List<Field> fields = new ArrayList<>(fieldIds.keySet()); // get the fields
        for (Field field : fields) { // iterate through the fields
            sb.append(buildField(field)); // build field string
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Build the DDL for a given table.
     * @param table The table to build DDL for
     * @return The table's DDL
     */
    private String buildTable(Table table) {
        StringBuilder sb = new StringBuilder(); // create the string builder
        
        sb.append("Table: " + tableIds.get(table) + "\n{\n"); // add table header and id
        sb.append("TableName: " + table.getName()); // add table name
        sb.append("\nNativeFields: "); // add native fields header
        
        List<Field> fields = table.getFields(); // get the fields
        int numFields = fields.size();
        
        for (int i = 0; i < numFields; i++) { // iterate and append the ids to the header
            Field field = fields.get(i);
            sb.append(fieldIds.get(field).toString());
            if (i != numFields - 1) {
                sb.append("|"); // delimiter
            }
        }
        
        sb.append("\nRelatedTables: "); // add related tables header
        List<Table> relatedTables = table.getRelatedTables(); // gt related tables
        int numRelatedTables = relatedTables.size();
        
        for (int i = 0; i < numRelatedTables; i++) { // iterate and add ids to the header
            Table relatedTable = relatedTables.get(i);
            sb.append(tableIds.get(relatedTable).toString());
            if (i != numRelatedTables - 1) {
                sb.append("|"); // delimiter
            }
        }
        
        sb.append("\nRelatedFields: "); // add related fields to the header
        Map<Field, Field> relatedFields = table.getRelatedFields(); // get related fields
        
        for (int i = 0; i < numFields; i++) { // add related fields to the header
            Field field = fields.get(i);
            Field relatedField = relatedFields.get(field);
            if (relatedField == null) { // if null append 0 for a "null" value
                sb.append("0");
            } else {
                sb.append(fieldIds.get(relatedField).toString());
            }
            if (i != numFields - 1) {
                sb.append("|"); // delimiter
            }
        }
        sb.append("\n}");
        return sb.toString();
    }

    /**
     * Build the DDL for a given field.
     * @param field The field to build DDL for
     * @return The field's DDL
     */
    private String buildField(Field field) {
        StringBuilder sb = new StringBuilder(); // create stringbuilder and stub for foreign field
        Field forField;
        
        sb.append(fieldIds.get(field).toString()); // the field id
        sb.append("|");
        sb.append(field.getName()); // the field name
        sb.append("|");
        sb.append(tableIds.get(field.getTable()).toString()); // the table id
        sb.append("|");
        
        if ((forField = field.getForeignField()) == null) { // check to see if the foreign field is null
            sb.append("null|null");
        } else { // if not null
            sb.append(tableIds.get(field.getForeignField().getTable()).toString()); // foreign field id
            sb.append("|");
            sb.append(forField.getTable().toString()); // table id for foreign field
        }
        sb.append("|");
        sb.append(field.getDataType().ordinal()); // the data type
        sb.append("|");
        sb.append(field.getCharLength()); // the length
        sb.append("|");
        sb.append(field.isPrimaryKey()); // primary key
        sb.append("|");
        sb.append(!field.isAllowNull()); // alllows null
        sb.append("|");
        sb.append(field.getDefaultValue()); // default value
        return sb.toString();
    }

    /**
     * Check to see if a database name is required for this build type
     * @return 
     */
    @Override
    public boolean requiresDatabaseName() {
        // A database name is not requred for the save file
        return false;
    }

}
