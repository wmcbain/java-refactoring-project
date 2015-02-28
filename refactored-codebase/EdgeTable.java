package edgeconvert;

import java.util.*;

public class EdgeTable {

    private int numFigure;
    private String name;
    private List<Integer> alRelatedTables, alNativeFields;
    private int[] relatedTables, relatedFields, nativeFields;
    
    /**
     * Construct a new EdgeTable with a given name and numeric identifier.
     * @param name This table's name
     * @param number A number identifying this table
     */
    public EdgeTable(String name, int number) {
        this.name = name;
        this.numFigure = number;
        this.alRelatedTables = new ArrayList<>();
        this.alNativeFields = new ArrayList<>();
    }

    /**
     * Get the numeric identifier for the table.
     * @return This table's identifying number
     */
    public int getNumFigure() {
        return numFigure;
    }

    /**
     * Get the name of this table.
     * @return This table's name
     */
    public String getName() {
        return name;
    }

    /**
     * Add a table to this table's list of related tables.
     * @param relatedTable The table to be added
     */
    public void addRelatedTable(int relatedTable) {
        alRelatedTables.add(relatedTable);
    }

    public List<Integer> getRelatedTables() {
        return Collections.unmodifiableList(alRelatedTables);
    }
    
    /**
     * Get an array of table numbers for tables related to this one.
     * @return Tables related to this one
     */
    public int[] getRelatedTablesArray() {
        return relatedTables;
    }

    /**
     * Get an array of field numbers for fields related to this table.
     * @return Fields related to this table
     */
    public int[] getRelatedFieldsArray() {
        return relatedFields;
    }

    /**
     * Add a field to this table's related fields in a specific position
     * corresponding to a field in this table.
     * @param index The position to add the related field
     * @param relatedValue Field to be related to this table
     */
    public void setRelatedField(int index, int relatedValue) {
        relatedFields[index] = relatedValue;
    }

    /**
     * Get an array of field numbers for fields in this table
     * @return Fields in this table
     */
    public int[] getNativeFieldsArray() {
        return nativeFields;
    }

    /**
     * Add the field identified by the provided number to this table.
     * @param value The field to be added
     */
    public void addNativeField(int value) {
        alNativeFields.add(value);
    }

    /**
     * Reposition field at the given index, moving it one place closer to the
     * beginning of the list of fields.
     * @param index The field to move
     */
    public void moveFieldUp(int index) { //move the field closer to the beginning of the list
        swapFields(index, index-1);
    }

    /**
     * Reposition field at the given index, moving it one place closer to the
     * end of the list of fields.
     * @param index The field to move
     */
    public void moveFieldDown(int index) { //move the field closer to the end of the list
        swapFields(index, index+1);
    }

    /**
     * Swap the positions of two fields.
     * @param source One of the fields to be swapped
     * @param destination The other field to be swapped
     */
    private void swapFields(int source, int destination) {
        // Make sure both the source and destination are valid array elements
        final boolean sourceValid = source < nativeFields.length && source >= 0;
        final boolean destinationValid = destination < nativeFields.length && destination >= 0;
        // If both are valid, swap them
        if (sourceValid && destinationValid) {
            // Swap using a temporary variable
            int tempNative = nativeFields[source];
            nativeFields[source] = nativeFields[destination];
            nativeFields[destination] = tempNative;
            int tempRelated = relatedFields[source];
            relatedFields[source] = relatedFields[destination];
            relatedFields[destination] = tempRelated;
        }
    }

    // TODO: This should probably be removed. It exposes too much internal logic.
    /**
     * Populate the arrays with values from the lists.
     */
    public void makeArrays() {
        
        nativeFields = integerListToArray(alNativeFields);
        relatedTables = integerListToArray(alRelatedTables);
        
        relatedFields = new int[nativeFields.length];
        for (int i = 0; i < relatedFields.length; i++) {
            relatedFields[i] = 0;
        }
    }
    
    /**
     * Convert a List<Integer> an int[].
     * @param list The list to convert to an array
     * @return An array containing the int values from the List
     */
    private int[] integerListToArray(List list) {
        Integer[] temp;
        temp = (Integer[]) list.toArray(new Integer[list.size()]);
        // Iterate over the Integer array and store the values in an int array
        int[] array = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            array[i] = temp[i];
        }
        return array;
    }
}
