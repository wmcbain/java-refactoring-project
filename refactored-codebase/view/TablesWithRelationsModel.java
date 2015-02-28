/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeconvert.view;

import edgeconvert.Schema;
import edgeconvert.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractListModel;

/**
 * Model for the Tables With Relations list
 */
public class TablesWithRelationsModel extends AbstractListModel<Table> implements Observer {

    // Decalarations
    private Schema schema;
    private List<Table> tables;
    
    /**
     * Default constructor
     * Initializes the schema and updates the tables
     */
    public TablesWithRelationsModel() {
        this.schema = null;
        updateTables();
    }

    /**
     * Sets the schema
     * @param schema the schema
     */
    public void setSchema(Schema schema) {
        if(this.schema != null) {
            this.schema.deleteObserver(this); // remove observer
        }
        this.schema = schema; // initialize the schema
        if(this.schema != null) {
            this.schema.addObserver(this); // add the observer
        }
        updateTables(); // update the tables
    }
    
    /**
     * Updates the tables and the list
     */
    private void updateTables() {
        if(schema != null) {
            this.tables = new ArrayList<>();
            List<Table> tempTables = schema.getTables(); // get the tables from the schema
            for(int i = 0; i < tempTables.size(); i++) { // iterate and add
                if(!tempTables.get(i).getRelatedTables().isEmpty()) {
                    this.tables.add(tempTables.get(i));
                }
            }
        }
        else {
            this.tables = new ArrayList<>();
        }
        fireContentsChanged(this, 0, getSize() - 1); // fire event
    }
    
    /**
     * Gets the size
     * @return the amount of tables
     */
    @Override
    public int getSize() {
        return tables.size();
    }

    /**
     * Gets the element at a specified index
     * @param index the index
     * @return the table at the index
     */
    @Override
    public Table getElementAt(int index) {
        return tables.get(index);
    }

    /**
     * Called whenever the table changes. Allows changes in the
     * table to be seen in the JList immediately.
     * @param o
     * @param arg 
     */
    @Override
    public void update(Observable o, Object arg) {
        updateTables();
    }
}
