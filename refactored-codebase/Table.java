package edgeconvert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Class representing a generic database table. Notifies observers when changes
 * occur in any of the included fields, letting them know that the table has
 * changed.
 */
public class Table extends Observable implements Observer {

    private String name;
    private List<Field> fields;
    private List<Table> relatedTables;
    private Map<Field, Field> relatedFields;
    private Map<String, Field> indexes;

    /**
     * Construct a new table with the given name.
     *
     * @param name The table's new name
     */
    public Table(String name) {
        this.name = name;
        fields = new ArrayList<>();
        relatedTables = new ArrayList<>();
        relatedFields = new HashMap<>();
        indexes = new HashMap<>();
    }

    /**
     * Get the name of this table.
     *
     * @return This table's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Add the given field to this table.
     *
     * @param field The field to be added to the table
     */
    public void addField(Field field) {
        if (!fields.contains(field)) {
            fields.add(field);
            field.addObserver(this);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Add a table to this table's list of related tables.
     *
     * @param table The table that is related to this table
     */
    public void addRelatedTable(Table table) {
        if (!relatedTables.contains(table)) {
            relatedTables.add(table);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Set a foreign key relationship between a field in this table and a field
     * in another table.
     *
     * @param nativeField This table's field
     * @param foreignField Another table's field
     */
    public void setRelatedField(Field nativeField, Field foreignField) {
        if (fields.contains(nativeField)) {
            relatedFields.put(nativeField, foreignField);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Set an index with the given name on the given field.
     *
     * @param index The name of the index to be set
     * @param field The field to make an index on
     */
    public void setIndex(String index, Field field) {
        if (fields.contains(field)) {
            indexes.put(index, field);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Remove the given field from this table's related fields.
     *
     * @param nativeField The field to be "unrelated" from this table
     */
    public void removeRelatedField(Field nativeField) {
        relatedFields.remove(nativeField);
        setChanged();
        notifyObservers();
    }

    /**
     * Get a list of all fields contained by this table.
     *
     * @return All of this table's fields
     */
    public List<Field> getFields() {
        return new ArrayList<>(fields);
    }

    /**
     * Get a list of all of this field's tables that are primary keys.
     *
     * @return This table's primary key fields
     */
    public List<Field> getPrimaryKeyFields() {
        List<Field> pkFields = new ArrayList<>();
        for (Field field : fields) {
            if (field.isPrimaryKey()) {
                pkFields.add(field);
            }
        }
        return pkFields;
    }

    /**
     * Get a list of tables that are related to this table.
     *
     * @return This table's related tables
     */
    public List<Table> getRelatedTables() {
        return Collections.unmodifiableList(relatedTables);
    }

    /**
     * Get a list of fields that are related to this table.
     *
     * @return This table's related fields
     */
    public Map<Field, Field> getRelatedFields() {
        return Collections.unmodifiableMap(relatedFields);
    }

    /**
     * Get all of the indexes on this table.
     *
     * @return This table's indexes
     */
    public Map<String, Field> getIndexes() {
        return Collections.unmodifiableMap(indexes);
    }

    /**
     * Move the given field up in the table.
     *
     * @param field The field to be moved up.
     */
    public void moveFieldUp(Field field) {
        final int source = fields.indexOf(field);
        final int dest = source - 1;
        swapFields(source, dest);
    }

    /**
     * Move the given field down in the table.
     *
     * @param field The field to be moved down.
     */
    public void moveFieldDown(Field field) {
        final int source = fields.indexOf(field);
        final int dest = source + 1;
        swapFields(source, dest);
    }

    /**
     * Swap the positions of two fields in this table.
     *
     * @param source One of the fields to be swapped
     * @param dest The other field to be swapped
     */
    private void swapFields(int source, int dest) {
        final boolean validSource = source >= 0 && source < fields.size();
        final boolean validDest = dest >= 0 && dest < fields.size();
        if (validSource && validDest) {
            Collections.swap(fields, source, dest);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Get the field with the given name from this table. If a field with the
     * given name cannot be found, return null.
     *
     * @param name The name of the field to be gotten
     * @return The field with the given name or null
     */
    public Field getFieldByName(String name) {
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Called whenever a field in this table is modified.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers();
    }

    /**
     * Get a string representation of this table (its name).
     * @return The name of this table
     */
    @Override
    public String toString() {
        return this.name;
    }
}
