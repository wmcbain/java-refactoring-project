package edgeconvert;

/**
 * EdgeField to be used for parsing and writing field objects
 */
public class EdgeField {

    // declarations
    private int numFigure, tableID, tableBound, fieldBound, dataType, varcharValue;
    private String name, defaultValue;
    private boolean disallowNull, isPrimaryKey;
    private static String[] strDataType = {"Varchar", "Boolean", "Integer", "Double"};
    public static final int VARCHAR_DEFAULT_LENGTH = 1;
    
    /**
     * Default constructor
     * Creates the field with default values
     * 
     * @param name field name
     * @param figureId the figure id
     */
    public EdgeField(String name, int figureId) {
        numFigure = figureId;
        this.name = name;
        tableID = 0;
        tableBound = 0;
        fieldBound = 0;
        disallowNull = false;
        isPrimaryKey = false;
        defaultValue = "";
        varcharValue = VARCHAR_DEFAULT_LENGTH;
        dataType = 0;
    }

    /**
     * Gets the number of the figure
     * @return the number
     */
    public int getNumFigure() {
        return numFigure;
    }

    /**
     * Gets the name of the field
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the table id that the field is associated with
     * @return the table id
     */
    public int getTableID() {
        return tableID;
    }

    /**
     * Sets the table id that the field is associated with
     * @param value the id
     */
    public void setTableID(int value) {
        tableID = value;
    }

    /**
     * Gets the table that the field is bound to if it has a relation
     * @return the table id
     */
    public int getTableBound() {
        return tableBound;
    }

    /**
     * Sets the table id that the field is bound to if it has a relation
     * @param value the id
     */
    public void setTableBound(int value) {
        tableBound = value;
    }

    /**
     * Gets the id of the field that the field is bound to if it has a relation
     * @return the id
     */
    public int getFieldBound() {
        return fieldBound;
    }

    /**
     * Sets the id of the field that the field is bound to if it has a relation
     * @param value the id
     */
    public void setFieldBound(int value) {
        fieldBound = value;
    }

    /**
     * Gets the state of the fields allowed null
     * @return true/false
     */
    public boolean getDisallowNull() {
        return disallowNull;
    }

    /**
     * Sets whether or not the field allows null values
     * @param value true/false
     */
    public void setDisallowNull(boolean value) {
        disallowNull = value;
    }

    /**
     * Gets the primary key status for this field
     * @return true/false
     */
    public boolean getIsPrimaryKey() {
        return isPrimaryKey;
    }

    /**
     * Sets whether or not this field is a primary key
     * @param value true/false
     */
    public void setIsPrimaryKey(boolean value) {
        isPrimaryKey = value;
    }

    /**
     * gets the default value for the field
     * @return the default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value for the field
     * @param value the value to be set
     */
    public void setDefaultValue(String value) {
        defaultValue = value;
    }

    /**
     * Gets the varchar length of the field
     * @return the value
     */
    public int getVarcharValue() {
        return varcharValue;
    }

    /**
     * Sets the varchar length for the field
     * @param value the value
     */
    public void setVarcharValue(int value) {
        if (value > 0) {
            varcharValue = value;
        }
    }

    /**
     * gets the data type of the field
     * @return the data type
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * Sets the data type for the field
     * @param value the value
     */
    public void setDataType(int value) {
        if (value >= 0 && value < strDataType.length) {
            dataType = value;
        }
    }

    /**
     * Gets the string array of the data type
     * @return the data type
     */
    public static String[] getStrDataType() {
        return strDataType;
    }
}
