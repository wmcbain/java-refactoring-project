package edgeconvert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Class representing a generic database schema.
 * Notifies observers when changes occur in any of the included
 * tables, letting them know that the schema has changed.
 */
public class Schema extends Observable implements Observer {

    private List<Table> tables;
    
    /**
     * Construct a new schema with no tables.
     */
    public Schema() {
        this.tables = new ArrayList<>();
    }
    
    /**
     * Construct a new schema with the given tables.
     * @param tables The schema's new tables
     */
    public Schema(List<Table> tables) {
        this.tables = new ArrayList<>();
        for(Table table : tables) {
            addTable(table);
        }
    }
    
    /**
     * Get a list of all tables contained in the schema.
     * @return The schema's tables
     */
    public List<Table> getTables() {
        return new ArrayList<>(tables);
    }
    
    /**
     * Add a table to the schema.
     * @param table The table to be added to the schema
     */
    public void addTable(Table table) {
        if(table != null && !tables.contains(table)) {
            tables.add(table);
            table.addObserver(this);
        }
    }
    
    /**
     * Get the table from the schema with the given name.
     * If a table with the given name is not found, return null.
     * @param name The name of the table to get
     * @return The table with the given name or null
     */
    public Table getTableByName(String name) {
        for(Table table : tables) {
            if(table.getName().equals(name)) {
                return table;
            }
        }
        return null;
    }
    
    /**
     * Called whenever one of the tables in the schema is modified.
     * @param o
     * @param arg 
     */
    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers();
    }
    
}
