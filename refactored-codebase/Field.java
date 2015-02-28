package edgeconvert;

import java.util.Observable;

/**
 * Class representing a field in a table. Notifies observers
 * whenever modified.
 */
public class Field extends Observable {

    public static final int VARCHAR_DEFAULT_LENGTH = 1;

    private DataType dataType;
    private Table table;
    private Field foreignField;
    private boolean allowNull, isPrimaryKey, autoIncrement;
    private String name, defaultValue;
    private int charLength;

    /**
     * Construct a new Field with the given name and set default values.
     * @param name The field's name
     */
    public Field(String name) {
        this.name = name;
        this.dataType = DataType.VARCHAR;
        this.defaultValue = "";
        this.charLength = 1;
        allowNull = true;
        isPrimaryKey = false;
        autoIncrement = false;
    }

    /**
     * Set the table that this field belongs to.
     *
     * @param table The table this field belongs to.
     */
    public void setTable(Table table) {
        this.table = table;
        setChanged();
        notifyObservers();
    }

    /**
     * Set the table from another field that this field references.
     *
     * @param field The foreign field referenced by this field
     */
    public void setForeignField(Field field) {
        this.foreignField = field;
        setChanged();
        notifyObservers();
    }

    /**
     * Set the data type of this field.
     *
     * @param dataType The data type this field holds
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
        if (dataType != DataType.INTEGER) {
            this.autoIncrement = false;
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Set the default value for this field.
     *
     * @param defaultValue This field's default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        setChanged();
        notifyObservers();
    }

    /**
     * Set whether or not the field is allowed to be null.
     * @param allowNull Whether or not the field can be null
     */
    public void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
        setChanged();
        notifyObservers();
    }

    /**
     * Set whether or not the field is a primary key
     * @param isPrimaryKey Whether or not the field is a primary key
     */
    public void setIsPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
        setChanged();
        notifyObservers();
    }

    /**
     * Set whether or not the field increments automatically.
     * If the field is not of type integer, set type to integer.
     * @param autoIncrement Whether or not the field should auto increment.
     */
    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
        if (autoIncrement) {
            this.dataType = DataType.INTEGER;
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Set the length of character values in this field.
     * @param length The maximum length of stored characters
     */
    public void setCharLength(int length) {
        this.charLength = length;
        setChanged();
        notifyObservers();
    }

    /**
     * Get the name of this field.
     * @return This field's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the table this field is a part of.
     * @return This field's table
     */
    public Table getTable() {
        return this.table;
    }

    /**
     * Get the field that this field is related to.
     * @return This field this field is related to
     */
    public Field getForeignField() {
        return this.foreignField;
    }

    /**
     * Get the data type of this field.
     * @return This field's data type
     */
    public DataType getDataType() {
        return this.dataType;
    }

    /**
     * Get the default value of this field.
     * @return This field's default value
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Determine whether or not this field is allowed to be null.
     * @return Whether or not this field can be null
     */
    public boolean isAllowNull() {
        return this.allowNull;
    }

    /**
     * Determine whether or not this field is a primary key.
     * @return Whether or not this field is a primary key
     */
    public boolean isPrimaryKey() {
        return this.isPrimaryKey;
    }

    /**
     * Determine whether or not this field increments automatically.
     * @return Whether or not this field auto increments
     */
    public boolean isAutoIncrement() {
        return this.autoIncrement;
    }

    /**
     * Get the maximum number of characters this field can hold.
     * @return This fields max amount of characters
     */
    public int getCharLength() {
        return this.charLength;
    }
    
    /**
     * Get a string representation of this field (its name).
     * @return The name of this field
     */
    @Override
    public String toString() {
        return this.name;
    }
}
