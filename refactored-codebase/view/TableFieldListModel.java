package edgeconvert.view;

import edgeconvert.Field;
import edgeconvert.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractListModel;

/**
 * List model that, when given to a list, displays all of the fields
 * in a given table.
 * 
 */
public class TableFieldListModel extends AbstractListModel<Field> implements Observer {

    // declarations
    private Table table;
    private List<Field> fields;

    /**
     * Construct a list model with a null table (empty).
     */
    public TableFieldListModel() {
        this.table = null;
        updateFields();
    }
    
    /**
     * Set the table that this list model displays the fields of.
     * @param table The table that this list model represents
     */
    public void setTable(Table table) {
        if (this.table != null) {
            this.table.deleteObserver(this); // remove observer
        }
        this.table = table;
        if(this.table != null) {
            this.table.addObserver(this); // add observer
        }
        updateFields();
    }

    /**
     * Update the list of fields to reflect the table's
     * current state.
     */
    private void updateFields() {
        if(table != null) {
            this.fields = table.getFields(); // add fields to the list
        }
        else {
            this.fields = new ArrayList<>();
        }
        
        fireContentsChanged(this, 0, getSize() - 1); // fire event
    }

    /**
     * Gets the number of fields
     * @return the number of fields
     */
    @Override
    public int getSize() {
        return fields.size();
    }

    /**
     * Gets the element at a specified index
     * @param index the index
     * @return the field at the index
     */
    @Override
    public Field getElementAt(int index) {
        return fields.get(index);
    }

    /**
     * Called whenever the table changes. Allows changes in the
     * table to be seen in the JList immediately.
     * @param o
     * @param arg 
     */
    @Override
    public void update(Observable o, Object arg) {
        // When the table changes, update the fields displayed
        updateFields();
    }
}
