package edgeconvert.view;

import edgeconvert.Schema;
import edgeconvert.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractListModel;

/**
 * List model that, when given to a list, displays all of the tables
 * in a given schema.
 * 
 */
public class SchemaTableListModel extends AbstractListModel<Table> implements Observer {

    // declarations
    private Schema schema;
    private List<Table> tables;
    
    /**
     * Construct a list model with a null schema (empty).
     */
    public SchemaTableListModel() {
        this.schema = null;
        updateTables();
    }
    
    /**
     * Set the schema this list model displays the tables of.
     * @param schema The schema this list model represents
     */
    public void setSchema(Schema schema) {
        if(this.schema != null) {
            this.schema.deleteObserver(this); // remove observer
        }
        this.schema = schema;
        if(this.schema != null) {
            this.schema.addObserver(this); // add observer
        }
        updateTables(); // update the tables
    }
    
    /**
     * Update the list of tables to reflect the schema's
     * current state.
     */
    private void updateTables() {
        // If the schema has been set, the list contains its tables
        if(schema != null) {
            this.tables = schema.getTables(); // add the tables from the schema
        }
        // Otherwise, the list is empty
        else {
            this.tables = new ArrayList<>();
        }
        // Notify the JList that the list model has changed
        fireContentsChanged(this, 0, getSize() - 1);
    }
    
    /**
     * Gets the number of tables
     * @return the number of tables
     */
    @Override
    public int getSize() {
        return tables.size();
    }

    /**
     * Gets the table at the specified index
     * @param index the index
     * @return the table at the index
     */
    @Override
    public Table getElementAt(int index) {
        return tables.get(index);
    }

    /**
     * Called whenever the schema changes. Allows changes in the
     * schema to be seen in the JList immediately.
     * @param o
     * @param arg 
     */
    @Override
    public void update(Observable o, Object arg) {
        // When the schema changes, update the tables displayed
        updateTables();
    }
    
}
