/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeconvert.view;

import edgeconvert.Field;
import edgeconvert.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractListModel;

/**
 * Model for the Fields in the Table with Relations list in Define Relations
 */
public class TableFieldListRelationModel extends AbstractListModel<Field> implements Observer {

    // declarations
    private Table table;
    private List<Field> fields;

    /**
     * Default constructor
     * Instantiates the table object with null value
     * Updates the fields when created
     */
    public TableFieldListRelationModel() {
        this.table = null;
        updateFields();
    }
    
    /**
     * Sets the table to be used in the list
     * @param table the table
     */
    public void setTable(Table table) {
        if (this.table != null) {
            this.table.deleteObserver(this); // remove observer
        }
        this.table = table; // initialize the table
        if(this.table != null) {
            this.table.addObserver(this); // add observer
        }
        updateFields(); //update the fields
    }

    /**
     * Updates the fields in the list
     */
    private void updateFields() {
        if(table != null) {
            this.fields = new ArrayList<>();
            List<Field> tempFields = table.getFields(); // get the fields
            Map<Field, Field> mappedFields = table.getRelatedFields(); // get the related fields
            
            // if there are no related fields show all the fields
            if(mappedFields.isEmpty()) {
                this.fields = tempFields;
            } else {
                for (int i = 0; i < tempFields.size(); i++) {
                    if (mappedFields.containsKey(tempFields.get(i))) {
                        fields.add(tempFields.get(i)); // add related fields to the list
                    }
                }
            }
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
        updateFields();
    }
}
