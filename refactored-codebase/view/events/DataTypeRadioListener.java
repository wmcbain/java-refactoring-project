package edgeconvert.view.events;

import edgeconvert.DataType;
import edgeconvert.Field;
import edgeconvert.view.EditFieldView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listener for the datatype radio buttons
 */
public class DataTypeRadioListener implements ActionListener {

    // declarations
    private EditFieldView view;
    private DataType dataType;
    
    /**
     * Constructor for the listener
     * Takes in the edit field and datatype as arguments
     * @param view the view
     * @param dataType the datatype
     */
    public DataTypeRadioListener(EditFieldView view, DataType dataType) {
        this.view = view;
        this.dataType = dataType;
    }
    
    /**
     * Listens for events on the radio buttons
     * @param e the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Field field = view.getField();
        field.setDataType(dataType);
        view.clearDefaultValue();
    }
    
}
