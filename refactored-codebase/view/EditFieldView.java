package edgeconvert.view;

import edgeconvert.DataType;
import edgeconvert.EdgeField;
import edgeconvert.Field;
import edgeconvert.view.events.DataTypeRadioListener;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * Edit field view for editing a field
 */
public class EditFieldView extends JPanel implements Observer {

    // declarations
    private JPanel panelLeft, panelRight;
    private ButtonGroup btnGroupDataType;
    private JButton btnDefaultValue, btnCharLength;
    private JCheckBox checkDisallowNull, checkPrimaryKey, checkAutoIncrement;
    private JTextField txtDefaultValue, txtCharLength;
    private Map<DataType, JRadioButton> dataTypeRadios;
    private Field field;

    /**
     * Initializes the edit field view
     * Calls to build the gui
     */
    public EditFieldView() {
        super();
        this.setLayout(new GridLayout(1, 2));
        this.field = null;
        this.buildGUI();
    }

    /**
     * Sets the field to be used
     * @param field the field
     */
    public void setField(Field field) {
        if (this.field != null) {
            this.field.deleteObserver(this);
        }
        this.field = field;
        if (this.field != null) {
            this.field.addObserver(this);
        }
        updateComponents();
    }

    /**
     * Gets the field being used
     * @return the field
     */
    public Field getField() {
        return this.field;
    }

    /**
     * Set the components to match the field.
     */
    private void updateComponents() {
        if (field != null) {
            enableFieldControls(); // enable the field controls
            DataType dataType = field.getDataType(); // get the data type
            if (dataType != null) {
                dataTypeRadios.get(dataType).setSelected(true); //select the appropriate radio button, based on value of dataType
                btnDefaultValue.setEnabled(true); // enable default button
            } else {
                btnGroupDataType.clearSelection();
                btnDefaultValue.setEnabled(false);
            }
            if (dataType == DataType.VARCHAR || dataType == DataType.CHAR) { //field is a Varchar
                btnCharLength.setEnabled(true); //enable the Varchar button
                txtCharLength.setText(Integer.toString(field.getCharLength())); //fill text field with varcharLength
            } else { //field is not a Varchar
                txtCharLength.setText(""); //clear the text field
                btnCharLength.setEnabled(false); //disable the button
            }
            checkPrimaryKey.setSelected(field.isPrimaryKey()); //clear or set Primary Key checkbox
            checkDisallowNull.setSelected(!field.isAllowNull()); //clear or set Disallow Null checkbox
            checkAutoIncrement.setSelected(field.isAutoIncrement()); //clear or set Auto Increment checkbox
            txtDefaultValue.setText(field.getDefaultValue()); //fill text field with defaultValue
        } else {
            disableFieldControls();
        }
    }

    /**
     * Enables the field controls
     */
    private void enableFieldControls() {
        for (DataType dataType : DataType.values()) { // enable radios
            dataTypeRadios.get(dataType).setEnabled(true);
        }
        checkPrimaryKey.setEnabled(true);
        checkDisallowNull.setEnabled(true);
        checkAutoIncrement.setEnabled(true);
        txtCharLength.setEnabled(true);
        txtDefaultValue.setEnabled(true);
    }

    /**
     * Disables the field controls
     */
    private void disableFieldControls() {
        for (DataType dataType : DataType.values()) { // disable the radio buttons
            dataTypeRadios.get(dataType).setEnabled(false);
        }
        btnGroupDataType.clearSelection();
        btnDefaultValue.setEnabled(false);
        checkPrimaryKey.setEnabled(false);
        checkDisallowNull.setEnabled(false);
        checkAutoIncrement.setEnabled(false);
        checkPrimaryKey.setSelected(false);
        checkDisallowNull.setSelected(false);
        checkAutoIncrement.setSelected(false);
        btnDefaultValue.setEnabled(false);
        txtCharLength.setText("");
        txtDefaultValue.setText("");
    }

    /**
     * Builds the GUI and displays it to the user
     */
    private void buildGUI() {
        dataTypeRadios = new HashMap<>(); //create array of JRadioButtons, one for each supported data type
        btnGroupDataType = new ButtonGroup();
        panelLeft = new JPanel(new GridLayout(DataType.values().length, 1));
        for (DataType dataType : DataType.values()) { // iterate through data types
            JRadioButton radioBtn = new JRadioButton(dataType.toString()); //assign label for radio button from String array
            radioBtn.setEnabled(false);
            // TODO: Add listener
            radioBtn.addActionListener(new DataTypeRadioListener(this, dataType)); // add listener
            
            // add to gui
            btnGroupDataType.add(radioBtn);
            panelLeft.add(radioBtn);
            dataTypeRadios.put(dataType, radioBtn);
        }

        // Disallow Null Button
        checkDisallowNull = new JCheckBox("Disallow Null");
        checkDisallowNull.setEnabled(false);
        checkDisallowNull.addItemListener(
                new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent ie) {
                        if (field != null) {
                            boolean disallowNull = checkDisallowNull.isSelected();
                            field.setAllowNull(!disallowNull);
                        }
                    }
                }
        );

        // Primary Key Button
        checkPrimaryKey = new JCheckBox("Primary Key");
        checkPrimaryKey.setEnabled(false);
        checkPrimaryKey.addItemListener(
                new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent ie) {
                        if (field != null) {
                            boolean isPrimaryKey = checkPrimaryKey.isSelected();
                            field.setIsPrimaryKey(isPrimaryKey);
                        }
                    }
                }
        );
        
        // Auto increment button
        checkAutoIncrement = new JCheckBox("Auto Increment");
        checkAutoIncrement.setEnabled(false);
        checkAutoIncrement.addItemListener(
                new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent ie) {
                        if (field != null) {
                            boolean autoIncrement = checkAutoIncrement.isSelected();
                            field.setAutoIncrement(autoIncrement);
                        }
                    }
                }
        );

        // Set default value button
        btnDefaultValue = new JButton("Set Default Value");
        btnDefaultValue.setEnabled(false);
        btnDefaultValue.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        String prev = txtDefaultValue.getText();
                        boolean goodData = false;
                        DataType dataType = field.getDataType();
                        do {
                            String result = (String) JOptionPane.showInputDialog(
                                    null,
                                    "Enter the default value:",
                                    "Default Value",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    prev);

                            if ((result == null)) {
                                txtDefaultValue.setText(prev);
                                return;
                            }
                            switch (dataType) { // swicth on data type, with different requirements for each
                                case CHAR: //char
                                case VARCHAR: //varchar
                                    if (result.length() <= Integer.parseInt(txtCharLength.getText())) {
                                        txtDefaultValue.setText(result);
                                        goodData = true;
                                    } else {
                                        JOptionPane.showMessageDialog(null, "The length of this value must be less than or equal to the Char length specified.");
                                    }
                                    break;
                                case BOOLEAN: //boolean
                                    String newResult = result.toLowerCase();
                                    if (newResult.equals("true") || newResult.equals("false")) {
                                        txtDefaultValue.setText(newResult);
                                        goodData = true;
                                    } else {
                                        JOptionPane.showMessageDialog(null, "You must input a valid boolean value (\"true\" or \"false\").");
                                    }
                                    break;
                                case INTEGER: //Integer
                                    try {
                                        int intResult = Integer.parseInt(result);
                                        txtDefaultValue.setText(result);
                                        goodData = true;
                                    } catch (NumberFormatException nfe) {
                                        JOptionPane.showMessageDialog(null, "\"" + result + "\" is not an integer or is outside the bounds of valid integer values.");
                                    }
                                    break;
                                case DOUBLE: //Double
                                    try {
                                        double doubleResult = Double.parseDouble(result);
                                        txtDefaultValue.setText(result);
                                        goodData = true;
                                    } catch (NumberFormatException nfe) {
                                        JOptionPane.showMessageDialog(null, "\"" + result + "\" is not a double or is outside the bounds of valid double values.");
                                    }
                                    break;
                                case TIMESTAMP: //Timestamp
                                    try {
                                        txtDefaultValue.setText(result);
                                        goodData = true;
                                    } catch (Exception e) {

                                    }
                                    break;
                            }
                        } while (!goodData);
                        field.setDefaultValue(txtDefaultValue.getText());
                    }
                }
        ); //jbDTDefaultValue.addActionListener
        txtDefaultValue = new JTextField();
        txtDefaultValue.setEditable(false);

        // Set char length button
        btnCharLength = new JButton("Set Char Length");
        btnCharLength.setEnabled(false);
        btnCharLength.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        String prev = txtCharLength.getText();
                        String result = (String) JOptionPane.showInputDialog(
                                null,
                                "Enter the varchar length:",
                                "Varchar Length",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                prev);
                        if ((result == null)) {
                            txtCharLength.setText(prev);
                            return;
                        }
                        int varchar;
                        try { // check for validity
                            if (result.length() > 5) {
                                JOptionPane.showMessageDialog(null, "Varchar length must be greater than 0 and less than or equal to 65535.");
                                txtCharLength.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                                return;
                            }
                            varchar = Integer.parseInt(result);
                            if (varchar > 0 && varchar <= 65535) { // max length of varchar is 255 before v5.0.3
                                txtCharLength.setText(Integer.toString(varchar));
                                field.setCharLength(varchar);
                            } else {
                                JOptionPane.showMessageDialog(null, "Varchar length must be greater than 0 and less than or equal to 65535.");
                                txtCharLength.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                                return;
                            }
                        } catch (NumberFormatException nfe) {
                            JOptionPane.showMessageDialog(null, "\"" + result + "\" is not a number");
                            txtCharLength.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                            return;
                        }
                    }
                }
        );
        txtCharLength = new JTextField();
        txtCharLength.setEditable(false);

        // add the buttons and groups to the panel
        panelRight = new JPanel(new GridLayout(7, 1));
        panelRight.add(btnCharLength);
        panelRight.add(txtCharLength);
        panelRight.add(checkPrimaryKey);
        panelRight.add(checkDisallowNull);
        panelRight.add(checkAutoIncrement);
        panelRight.add(btnDefaultValue);
        panelRight.add(txtDefaultValue);
        this.add(panelLeft);
        this.add(panelRight);
    }

    /**
     * Clears the default value
     */
    public void clearDefaultValue() {
        if(field != null) {
            field.setDefaultValue("");
        }
        txtDefaultValue.setText("");
    }
    
    /**
     * Called whenever the table changes. Allows changes in the
     * table to be seen in the JList immediately.
     * @param o
     * @param arg 
     */
    @Override
    public void update(Observable o, Object arg) {
        updateComponents();
    }
}
