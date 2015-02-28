package edgeconvert.view;

import edgeconvert.DataType;
import edgeconvert.EdgeConvertMediator;
import edgeconvert.Field;
import edgeconvert.Schema;
import edgeconvert.Table;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * GUI for defining relations between tables.
 */
public class DefineRelationsView extends EdgeConvertView {

    // declarations
    private JPanel panelCenter, panelCenter1, panelCenter2, panelCenter3,
            panelCenter4, panelBottom;
    private TablesWithRelationsModel listModelTablesRelations;
    private RelatedTablesModel listModelTablesRelatedTo;
    private TableFieldListRelationModel listModelFieldsTablesRelations;
    private TableFieldListModel listModelFieldsTablesRelatedTo;
    private JList listTablesRelations, listFieldsTablesRelations, listTablesRelatedTo,
            listFieldsTablesRelatedTo;
    private JScrollPane scrollPaneTablesRelations, scrollPaneFieldsTablesRelations,
            scrollPaneTablesRelatedTo, scrollPaneFieldsTablesRelatedTo;
    private JLabel lblTablesRelations, lblFieldsTablesRelations,
            lblTablesRelatedTo, lblFieldsTablesRelatedTo;
    private JButton btnBindRelation, btnDefineTables, btnCreateDDL;
    private Table tablesWithRelSel, relatedTablesSel;
    private Field fieldsInTablesSel, fieldsInRelTableSel;

    /**
     * Construct a new DefineRelationsView.
     */
    public DefineRelationsView() {
        super();
        this.setTitle(DEFINE_RELATIONS);
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
    private void buildGUI() {

        panelCenter = new JPanel(new GridLayout(2, 2));
        panelCenter1 = new JPanel(new BorderLayout());
        panelCenter2 = new JPanel(new BorderLayout());
        panelCenter3 = new JPanel(new BorderLayout());
        panelCenter4 = new JPanel(new BorderLayout());

        listModelTablesRelations = new TablesWithRelationsModel();
        listTablesRelations = new JList(listModelTablesRelations);
        
        listModelFieldsTablesRelations = new TableFieldListRelationModel();
        listFieldsTablesRelations = new JList(listModelFieldsTablesRelations);
        
        listModelTablesRelatedTo = new RelatedTablesModel();
        listTablesRelatedTo = new JList(listModelTablesRelatedTo);
        
        listModelFieldsTablesRelatedTo = new TableFieldListModel();
        listFieldsTablesRelatedTo = new JList(listModelFieldsTablesRelatedTo);

        scrollPaneTablesRelations = new JScrollPane(listTablesRelations);
        scrollPaneFieldsTablesRelations = new JScrollPane(listFieldsTablesRelations);
        scrollPaneTablesRelatedTo = new JScrollPane(listTablesRelatedTo);
        scrollPaneFieldsTablesRelatedTo = new JScrollPane(listFieldsTablesRelatedTo);
        lblTablesRelations = new JLabel("Tables With Relations", SwingConstants.CENTER);
        lblFieldsTablesRelations = new JLabel("Fields in Tables with Relations", SwingConstants.CENTER);
        lblTablesRelatedTo = new JLabel("Related Tables", SwingConstants.CENTER);
        lblFieldsTablesRelatedTo = new JLabel("Fields in Related Tables", SwingConstants.CENTER);
        panelCenter1.add(lblTablesRelations, BorderLayout.NORTH);
        panelCenter2.add(lblFieldsTablesRelations, BorderLayout.NORTH);
        panelCenter3.add(lblTablesRelatedTo, BorderLayout.NORTH);
        panelCenter4.add(lblFieldsTablesRelatedTo, BorderLayout.NORTH);
        panelCenter1.add(scrollPaneTablesRelations, BorderLayout.CENTER);
        panelCenter2.add(scrollPaneFieldsTablesRelations, BorderLayout.CENTER);
        panelCenter3.add(scrollPaneTablesRelatedTo, BorderLayout.CENTER);
        panelCenter4.add(scrollPaneFieldsTablesRelatedTo, BorderLayout.CENTER);
        panelCenter.add(panelCenter1);
        panelCenter.add(panelCenter2);
        panelCenter.add(panelCenter3);
        panelCenter.add(panelCenter4);
        this.getContentPane().add(panelCenter, BorderLayout.CENTER);
        panelBottom = new JPanel(new GridLayout(1, 3));

        btnDefineTables = new JButton(DEFINE_TABLES);
        
        btnBindRelation = new JButton("Bind/Unbind Relation");
        btnBindRelation.setEnabled(false);
        
        btnCreateDDL = new JButton("Create DDL");
        btnCreateDDL.setEnabled(true);
        
        panelBottom.add(btnDefineTables);
        panelBottom.add(btnBindRelation);
        panelBottom.add(btnCreateDDL);
        this.getContentPane().add(panelBottom, BorderLayout.SOUTH);
    }
    
    /**
     * Add listeners to GUI components.
     */
    private void addListeners() {
        
        // List table relations list
        listTablesRelations.addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent lse) {
                        int selIndex = listTablesRelations.getSelectedIndex();
                        if (selIndex >= 0) {

                            // Get the table and requisite data or other lists
                            tablesWithRelSel = listModelTablesRelations.getElementAt(selIndex);

                            // clear selections
                            listFieldsTablesRelations.clearSelection();
                            listTablesRelatedTo.clearSelection();
                            listFieldsTablesRelatedTo.clearSelection();
                            listModelFieldsTablesRelatedTo.setTable(null);

                            // set table data
                            listModelFieldsTablesRelations.setTable(tablesWithRelSel);
                            listModelTablesRelatedTo.setTable(tablesWithRelSel);

                        }
                    }
                }
        );
        
        // list fields in table with relations list
        listFieldsTablesRelations.addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent lse) {
                        int selIndex = listFieldsTablesRelations.getSelectedIndex();
                        if (selIndex >= 0) {
                            fieldsInTablesSel = listModelFieldsTablesRelations.getElementAt(selIndex);
                            if (fieldsInTablesSel.getForeignField() == null) {
                                listTablesRelatedTo.clearSelection();
                                listFieldsTablesRelatedTo.clearSelection();
                                listModelFieldsTablesRelatedTo.setTable(null);
                            } else {
                                listTablesRelatedTo.setSelectedValue(fieldsInTablesSel.getTable().getName(), true);
                                listFieldsTablesRelatedTo.setSelectedValue(fieldsInTablesSel.getName(), true);
                            }
                        }
                    }
                }
        );
        
        // tables related to list
        listTablesRelatedTo.addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent lse) {
                        int selIndex = listTablesRelatedTo.getSelectedIndex();
                        if (selIndex >= 0) {
                            relatedTablesSel = listModelTablesRelatedTo.getElementAt(selIndex);
                            listModelFieldsTablesRelatedTo.setTable(relatedTablesSel);
                        }
                    }
                }
        );
        
        // list fields in tables related to list
        listFieldsTablesRelatedTo.addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent lse) {
                        int selIndex = listFieldsTablesRelatedTo.getSelectedIndex();
                        if (selIndex >= 0) {
                            fieldsInRelTableSel = listModelFieldsTablesRelatedTo.getElementAt(selIndex);
                            btnBindRelation.setEnabled(true);
                        } else {
                            btnBindRelation.setEnabled(false);
                        }
                    }
                }
        );
        
        // define tables button
        btnDefineTables.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        EdgeConvertMediator edgeConvert = EdgeConvertMediator.getInstance();
                        edgeConvert.showDefineTablesView();
                    }
                }
        );
        
        // bind relations button
        btnBindRelation.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        Field field = getSelectedField();
                        Field relatedField = getSelectedRelatedField();
                        Table table = field.getTable();
                        Table relatedTable = relatedField.getTable();
                        if (field.getForeignField() == relatedField) { //the selected fields are already bound to each other
                            int answer = JOptionPane.showConfirmDialog(null, "Do you wish to unbind the relation on field "
                                    + field.getName() + "?",
                                    "Are you sure?", JOptionPane.YES_NO_OPTION);
                            if (answer == JOptionPane.YES_OPTION) {
                                field.setForeignField(null);
                                table.removeRelatedField(field);
                                listFieldsTablesRelatedTo.clearSelection(); //clear the listbox selection
                            }
                            return;
                        }
                        if (field.getForeignField() != null) { //field is already bound to a different field
                            int answer = JOptionPane.showConfirmDialog(null, "There is already a relation defined on field "
                                    + field.getName() + ", do you wish to overwrite it?",
                                    "Are you sure?", JOptionPane.YES_NO_OPTION);
                            if (answer == JOptionPane.NO_OPTION || answer == JOptionPane.CLOSED_OPTION) {
                                listTablesRelatedTo.setSelectedValue(relatedTable.getName(), true); //revert selections to saved settings
                                listFieldsTablesRelatedTo.setSelectedValue(relatedField.getName(), true); //revert selections to saved settings
                                return;
                            }
                        }
                        if (field.getDataType() != relatedField.getDataType()) { // fields are not the same data type
                            JOptionPane.showMessageDialog(null, "The datatypes of " + table.getName() + "."
                                    + field.getName() + " and " + relatedTable.getName()
                                    + "." + relatedField.getName() + " do not match.  Unable to bind this relation.");
                            return;
                        }
                        if ((field.getDataType() == DataType.VARCHAR) && (relatedField.getDataType() == DataType.VARCHAR)) { // varchar lengths dont match
                            if (field.getCharLength() != relatedField.getCharLength()) {
                                JOptionPane.showMessageDialog(null, "The varchar lengths of " + table.getName() + "."
                                        + field.getName() + " and " + relatedTable.getName()
                                        + "." + relatedField.getName() + " do not match.  Unable to bind this relation.");
                                return;
                            }
                        }
                        table.setRelatedField(field, relatedField); // set related field
                        field.setForeignField(relatedField); // set foreign field
                        JOptionPane.showMessageDialog(null, "Table " + table.getName() + ": native field "
                                + field.getName() + " bound to table " + relatedTable.getName()
                                + " on field " + relatedField.getName()); // success message
                    }
                }
        );
        
        // create ddl button
        btnCreateDDL.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        EdgeConvertMediator mediator = EdgeConvertMediator.getInstance();
                        mediator.save();
                    }
                }
        );
    }

    /**
     * Gets the selected field
     * @return the selected field
     */
    private Field getSelectedField() {
        return fieldsInTablesSel;
    }

    /**
     * Gets the selected related field
     * @return 
     */
    private Field getSelectedRelatedField() {
        return fieldsInRelTableSel;
    }

    /**
     * Gets the schema and initializes the lists with table information
     */
    @Override
    public void populate() {
        Schema schema = EdgeConvertMediator.getInstance().getSchema();
        listModelTablesRelations.setSchema(schema);
        listModelTablesRelatedTo.setTable(null);
        listModelFieldsTablesRelations.setTable(null);
        listModelFieldsTablesRelatedTo.setTable(null);
        boolean validSchema = schema != null;
        btnDefineTables.setEnabled(validSchema);
        btnCreateDDL.setEnabled(validSchema);
    }
}
