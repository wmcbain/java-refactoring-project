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
 * List model that lists all the related tables
 */
public class RelatedTablesModel extends AbstractListModel<Table> implements Observer {

    // declarations
    private Table table;
    private List<Table> tables;
    
    /**
     * Default constructor that instantiates a null table, and updates the list
     */
    public RelatedTablesModel() {
        this.table = null;
        updateTables();
    }
    
    /**
     * Sets the table to be listed
     * @param table the table
     */
    public void setTable(Table table) {
        if(this.table != null) {
            this.table.deleteObserver(this); // remove observer
        }
        this.table = table; // intiialize the table
        if(this.table != null) {
            this.table.addObserver(this); // remove observer
        }
        updateTables(); // update
    }
    
    /**
     * Updates the tables in the list
     */
    private void updateTables() {
        if(table != null) {
            this.tables = table.getRelatedTables(); // get the related tables
        }
        else {
            this.tables = new ArrayList<>();
        }
        fireContentsChanged(this, 0, getSize() - 1); // fire event
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
