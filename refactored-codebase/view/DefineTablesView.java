package edgeconvert.view;

import edgeconvert.EdgeConvertMediator;
import edgeconvert.Field;
import edgeconvert.Schema;
import edgeconvert.Table;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * The Define tables view, which allows the user to define attributes for tables and fields
 */
public class DefineTablesView extends EdgeConvertView {

    // declarations
    private JButton btnCreateDDL, btnDefineRelations, btnMoveUp, btnMoveDown;
    private JPanel panelBottom, panelCenter, panelMove,
            panelCenter1, panelCenter2;
    private JList<Table> listTables;
    private JList<Field> listFields;
    private SchemaTableListModel listModelTables;
    private TableFieldListModel listModelFields;
    private JScrollPane scrollTables, scrollFields;
    private JLabel lblTables, lblields;
    private EditFieldView editFieldView;

    /**
     * Constructor for the define tables view
     * Initializes the super class, sets the name, and layout.
     * Call to build the gui and adds the listeners
     */
    public DefineTablesView() {
        super();
        this.setName(DEFINE_TABLES);
        this.getContentPane().setLayout(new BorderLayout());

        buildGUI();
        addListeners();
    }

    /**
     * Set the enabled status of the Create DDL button.
     *
     * @param enabled The new enabled status of the Create DDL button
     */
    public void setCreateDDLEnabled(boolean enabled) {
        btnCreateDDL.setEnabled(enabled);
    }

    /**
     * Instantiate all GUI components and build the GUI.
     */
    private void buildGUI() {//create Define Tables screen
        panelBottom = new JPanel(new GridLayout(1, 2));

        btnCreateDDL = new JButton("Create DDL");
        btnCreateDDL.setEnabled(false);
        
        btnDefineRelations = new JButton(DEFINE_RELATIONS);
        btnDefineRelations.setEnabled(false);
        
        panelBottom.add(btnDefineRelations);
        panelBottom.add(btnCreateDDL);
        this.getContentPane().add(panelBottom, BorderLayout.SOUTH);

        panelCenter = new JPanel(new GridLayout(1, 3));
        listModelTables = new SchemaTableListModel();
        listTables = new JList<>(listModelTables);
        listTables.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        
        listModelFields = new TableFieldListModel();
        listFields = new JList<>(listModelFields);
        listFields.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        
        panelMove = new JPanel(new GridLayout(2, 1));
        btnMoveUp = new JButton("^");
        btnMoveUp.setEnabled(false);
        
        btnMoveDown = new JButton("v");
        btnMoveDown.setEnabled(false);
        
        panelMove.add(btnMoveUp);
        panelMove.add(btnMoveDown);

        scrollTables = new JScrollPane(listTables);
        scrollFields = new JScrollPane(listFields);
        panelCenter1 = new JPanel(new BorderLayout());
        panelCenter2 = new JPanel(new BorderLayout());
        lblTables = new JLabel("All Tables", SwingConstants.CENTER);
        lblields = new JLabel("Fields List", SwingConstants.CENTER);
        panelCenter1.add(lblTables, BorderLayout.NORTH);
        panelCenter2.add(lblields, BorderLayout.NORTH);
        panelCenter1.add(scrollTables, BorderLayout.CENTER);
        panelCenter2.add(scrollFields, BorderLayout.CENTER);
        panelCenter2.add(panelMove, BorderLayout.EAST);
        panelCenter.add(panelCenter1);
        panelCenter.add(panelCenter2);

        editFieldView = new EditFieldView();

        panelCenter.add(editFieldView);
        this.getContentPane().add(panelCenter, BorderLayout.CENTER);
        this.validate();
    } //createDTScreen

    /**
     * Add listeners to GUI components.
     */
    private void addListeners() {
        
        // Create DDL button
        btnCreateDDL.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        EdgeConvertMediator mediator = EdgeConvertMediator.getInstance();
                        mediator.save();
                    }
                }
        );
        
        // Define relation button
        btnDefineRelations.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        EdgeConvertMediator mediator = EdgeConvertMediator.getInstance();
                        mediator.showDefineRelationsView();
                        listModelFields.setTable(null);
                        editFieldView.setField(null);
                        listTables.clearSelection();
                        listFields.clearSelection();
                    }
                }
        );
        
        // tables list
        listTables.addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent lse) {
                        listFields.clearSelection();
                        btnMoveUp.setEnabled(false);
                        btnMoveDown.setEnabled(false);
                        Table table = getSelectedTable();
                        listModelFields.setTable(table);
                        editFieldView.setField(null);
                    }
                }
        );
        
        // fields list
        listFields.addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent lse) {
                        int selIndex = listFields.getSelectedIndex();
                        if (selIndex >= 0) {
                            if (selIndex == 0) {
                                btnMoveUp.setEnabled(false);
                            } else {
                                btnMoveUp.setEnabled(true);
                            }
                            if (selIndex == (listModelFields.getSize() - 1)) {
                                btnMoveDown.setEnabled(false);
                            } else {
                                btnMoveDown.setEnabled(true);
                            }
                        }
                        Field field = getSelectedField();
                        editFieldView.setField(field);
                    }
                }
        );
        
        // move button up
        btnMoveUp.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        Table table = getSelectedTable();
                        Field field = getSelectedField();
                        if (table != null && field != null) {
                            table.moveFieldUp(field);
                            listFields.setSelectedValue(field, true);

                        }
                    }
                }
        );
        
        // move button down
        btnMoveDown.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        Table table = getSelectedTable();
                        Field field = getSelectedField();
                        if (table != null && field != null) {
                            table.moveFieldDown(field);
                            listFields.setSelectedValue(field, true);

                        }
                    }
                }
        );
    }
    
    /**
     * Gets the selected table
     * @return the selected table
     */
    private Table getSelectedTable() {
        return listTables.getSelectedValue();
    }

    /**
     * Gets the selected field
     * @return the field
     */
    private Field getSelectedField() {
        return listFields.getSelectedValue();
    }

    /**
     * Populates the tables in the table list
     * Gets the schema for use of the GUI
     */
    @Override
    public void populate() {
        Schema schema = EdgeConvertMediator.getInstance().getSchema();
        listModelTables.setSchema(schema);
        listModelFields.setTable(null);
        editFieldView.setField(null);
        boolean validSchema = schema != null;
        btnDefineRelations.setEnabled(validSchema);
        btnCreateDDL.setEnabled(validSchema);
    }
}
