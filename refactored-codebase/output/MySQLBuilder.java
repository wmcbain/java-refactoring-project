package edgeconvert.output;

import edgeconvert.DataType;
import edgeconvert.EdgeConvertMediator;
import edgeconvert.Field;
import edgeconvert.Schema;
import edgeconvert.Table;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * DDLBuilder for MySQL.
 */
@PluginImplementation
public class MySQLBuilder implements DDLBuilder {
    
    // private static/final attributes
    private static final String PRODUCT_NAME = "MySQL";
    private static final String FILE_EXTENSION = "sql";
    
    // declarations
    private Schema schema;
    private EdgeConvertMediator edgeConvert;
    private List<Table> tables;
    private String databaseName;

    /**
     * Gets the product name
     * @return the product name
     */
    @Override
    public String getProductName() {
        return this.PRODUCT_NAME;
    }

    /**
     * Gets the file extension
     * @return the file extension
     */
    @Override
    public String getFileExtension() {
        return this.FILE_EXTENSION;
    }

    /**
     * Sets the tables to be built
     * @param tables the tables to be built
     */
    @Override
    public void setTables(List<Table> tables) {
        this.tables = tables;
        sortTables();
    }

    /**
     * Sets the database name
     * @param dbName the database name
     */
    @Override
    public void setDatabaseName(String dbName) {
        this.databaseName = dbName;
    }

    /**
     * Builds the ddl
     * @return 
     */
    @Override
    public String buildDDL() {
        StringBuilder sb = new StringBuilder(); // create the string builder

        // Create database and switch to database, file headers
        sb.append("CREATE DATABASE " + databaseName + ";\r\n");
        sb.append("USE " + databaseName + ";\r\n");

        //process tables in order from least dependent (least number of foreign keys) to most dependent
        for (Table table : tables) {
            sb.append(buildTable(table));
        }
        return sb.toString();
    }

    /**
     * Sort the tables by number of related fields, ascending. This alleviates
     * problems with dependencies.
     */
    private void sortTables() {
        Collections.sort(tables, new Comparator<Table>() {

            @Override
            public int compare(Table table1, Table table2) { // compare the two tables
                final int t1RelatedFields = table1.getRelatedFields().size();
                final int t2RelatedFields = table2.getRelatedFields().size();
                // The first table has fewer related fields than the second.
                if (t1RelatedFields < t2RelatedFields) {
                    return -1;
                } // The first table has more related fields than the second.
                else if (t1RelatedFields > t2RelatedFields) {
                    return 1;
                }
                // Both tables have the same number of related fields.
                return 0;
            }
        });
    }

    /**
     * Convert a string representation of a boolean to an int value.
     *
     * @param strBoolean The string to be converted to an int
     * @return The int representation of the provided booelan string
     */
    private int convertStrBooleanToInt(String strBoolean) {
        if (strBoolean.equalsIgnoreCase("true")) {
            return 1;
        }
        return 0;
    }

    /**
     * Create the DDL for a single table.
     *
     * @param table The table to create the DDL for
     * @return The DDL for the provided table
     */
    private String buildTable(Table table) {
        StringBuilder sb = new StringBuilder();

        // create table
        sb.append("CREATE TABLE " + table.getName() + " (\r\n");

        // get native and related fields
        List<Field> fields = table.getFields();
        List<Field> pkFields = table.getPrimaryKeyFields();
        Map<Field, Field> relatedFields = table.getRelatedFields();

        // print out the fields
        for (Field field : fields) {
            sb.append(buildField(field));
            if(fields.indexOf(field) != fields.size() - 1) {
                sb.append(",\r\n");
            }
        }
        //table has primary key(s)
        if (!pkFields.isEmpty()) {
            sb.append(",\r\n\tCONSTRAINT "); // constarint 
            sb.append(table.getName());
            sb.append("_PK PRIMARY KEY ("); // primary key
            int numPrimaryKey = pkFields.size(); // get number of primary keys
            
            for (Field pk : pkFields) { // iterate and add
                sb.append(pk.getName()); // get primary key name
                numPrimaryKey--;
                if (numPrimaryKey > 0) {
                    sb.append(", ");
                }
                else {
                    sb.append(")");
                }
            }
        }
        
        // Add a comma if there are foreign keys
        if (!relatedFields.isEmpty()) {
            sb.append(",\r\n");
        }
        else {
            sb.append("\r\n);\r\n\r\n");
        }

        //table has foreign keys
        if (!relatedFields.isEmpty()) {
            int currentFK = 1;
            int numForeignKey = relatedFields.size(); // get the number of foreign keys
            
            for (Field nativeField : relatedFields.keySet()) { // iterate and add
                Field relatedField = relatedFields.get(nativeField);
                Table relatedTable = relatedField.getTable();
                if (relatedField != null) {
                    sb.append("\tCONSTRAINT "); 
                    sb.append(table.getName()); // table name
                    sb.append("_FK"); 
                    sb.append(currentFK);// current foreign key
                    sb.append(" FOREIGN KEY("); 
                    sb.append(nativeField.getName()); // native field name
                    sb.append(") REFERENCES ");
                    sb.append(relatedTable.getName()); // related table name
                    sb.append("(");
                    sb.append(relatedField.getName()); // related field name
                    sb.append(")");
                    if (currentFK < numForeignKey) {
                        sb.append(",\r\n");
                    }
                    currentFK++;
                }
            }
            sb.append("\r\n);\r\n\r\n");
        }
        return sb.toString();
    }

    /**
     * Create the DDL for a single field.
     *
     * @param field The field to create the DDL for
     * @return The DDL for the provided field
     */
    private String buildField(Field field) {
        StringBuilder sb = new StringBuilder();

        // get the field, append to string builder
        sb.append("\t");
        sb.append(field.getName());
        sb.append(" ");
        sb.append(field.getDataType().toString());

        if (field.getDataType() == DataType.VARCHAR || field.getDataType() == DataType.CHAR) {
            //append char length in () if data type is varchar or char
            sb.append("(");
            sb.append(field.getCharLength());
            sb.append(")");
        }
        // not null
        if (!field.isAllowNull()) {
            sb.append(" NOT NULL");
        }
        // get default value
        if (!field.getDefaultValue().equals("")) {
            if (field.getDataType() == DataType.BOOLEAN) { //boolean data type
                sb.append(" DEFAULT ");
                sb.append(convertStrBooleanToInt(field.getDefaultValue()));
            } else { //any other data type
                sb.append(" DEFAULT ");
                sb.append(field.getDefaultValue());
            }
        }
        // auto increment
        if(field.isAutoIncrement()) {
            sb.append(" AUTO_INCREMENT");
        }
        return sb.toString();
    }

    @Override
    public boolean requiresDatabaseName() {
        // A database name is required for MySQL
        return true;
    }
}
