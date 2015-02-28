package edgeconvert.input;

import edgeconvert.DataType;
import edgeconvert.Field;
import edgeconvert.Schema;
import edgeconvert.Table;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * FileParser for XML Diagrammer files.
 */
@PluginImplementation
public class XMLParser implements FileParser {

    // public static/final attributes
    public static final String PRODUCT_NAME = "XML Diagrammer File";
    public static final String FILE_EXTENSION = "xml";
    
    // private static/final attributes
    private static final String ROOT_ELEMENT = "diagram";
    private static final String STRING = "string";
    private static final String FIXED = "fixed";
    private static final String SIZE = "size";
    private static final String INT = "int";
    
    // Decalarations
    private File inputFile;
    private String errorMessage;
    private Schema schema;

    /**
     * Construct a new XMLParser.
     */
    public XMLParser() {
        inputFile = null;
        errorMessage = "";
    }

    /**
     * Gets the product name
     * @return the product name
     */
    @Override
    public String getProductName() {
        return PRODUCT_NAME;
    }

    /**
     * Gets the file extension
     * @return the file extension
     */
    @Override
    public String getFileExtension() {
        return FILE_EXTENSION;
    }

    /**
     * Sets the file for the parser
     * @param inputFile the file
     */
    @Override
    public void setFile(File inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * Parses the file into a readable format for the GUI
     * @return success/failure
     */
    @Override
    public boolean parseFile() {
        schema = new Schema();
        if (inputFile == null || !inputFile.exists()) { // check to see that the file is valid
            return false;
        }
        try {
            // Parse the XML into a DOM document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputFile);
            
            Element root = doc.getDocumentElement(); // get the root element
            if (!root.getTagName().equals(ROOT_ELEMENT)) { // check
                errorMessage = "Root element must be \"" + ROOT_ELEMENT + "\".";
                return false;
            }

            List<Element> tablesParentList = getChildrenByName(root, "tables"); // get the parent table elements
            if (tablesParentList.size() != 1) { // check
                errorMessage = "There must be only one \"tables\" element.";
                return false;
            }

            Element tablesElement = tablesParentList.get(0); // get the table elements
            List<Element> tablesList = getChildrenByName(tablesElement, "table");

            if (tablesList.isEmpty()) { // check to ensure that there are tables in the list
                errorMessage = "The file contains no table definitions.";
                return false;
            }

            for (Element tableElement : tablesList) { // parse each table
                if (!parseTable(tableElement)) {
                    return false;
                }
            }

            List<Element> relationshipsParentList = getChildrenByName(root, "relationships"); // get the relationships
            if (relationshipsParentList.size() != 1) { // check
                errorMessage = "There must be only one \"relationships\" element.";
                return false;
            }

            Element relationshipsElement = relationshipsParentList.get(0); // parent relationship element
            List<Element> relationshipsList = getChildrenByName(relationshipsElement, "relation"); // get the relationships in a list

            for (Element relationElement : relationshipsList) { // parse each relation
                if (!parseRelation(relationElement)) {
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Parse the given table element and add the resulting table to the schema.
     *
     * @param tableElement The table element to be parsed
     * @return Whether or not the table was parsed successfully
     */
    private boolean parseTable(Element tableElement) {
        try {
            List<Element> nameElements = getChildrenByName(tableElement, "name"); // get the name elements
            if (nameElements.size() != 1) { // check to ensure there is only one
                errorMessage = "Each table must have exactly one \"name\" element.";
                return false;
            }

            String tableName = nameElements.get(0).getTextContent().trim(); // get the table name
            if (isTableNameDuplicate(tableName)) { // check for duplicates
                errorMessage = "Each table must have a unique name.";
                return false;
            }

            Table table = new Table(tableName); // create the table

            List<Element> fieldsElements = getChildrenByName(tableElement, "fields"); // get the field elements
            if (fieldsElements.size() != 1) { // check to ensure there is only one field element
                errorMessage = "Each table must have exactly one \"fields\" element.";
                return false;
            }

            Element fieldsElement = fieldsElements.get(0); // create the field element

            List<Element> fieldElements = getChildrenByName(fieldsElement, "field"); // get children of field elements
            if (fieldElements.isEmpty()) { // check to ensure there are fields
                errorMessage = "Each table must have at least one field.";
                return false;
            }

            for (Element fieldElement : fieldElements) { // parse each field
                if (!parseField(table, fieldElement)) {
                    return false;
                }
            }

            schema.addTable(table); // add the table to the schema
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Parse the given field element and add the resulting field to the given
     * table.
     *
     * @param table The table to add the parsed field to
     * @param fieldElement The field element to be parsed
     * @return Whether or not the field was parsed successfully
     */
    private boolean parseField(Table table, Element fieldElement) {
        try {
            String fieldName = fieldElement.getTextContent().trim(); // get the field name
            for (Field field : table.getFields()) { // ensure the field name doesnt already exist in the table
                if (field.getName().equalsIgnoreCase(fieldName)) {
                    errorMessage = "No two fields in the same table may have the same name";
                    return false;
                }
            }
            Field field = new Field(fieldName); // create the field

            String type = fieldElement.getAttribute("type").trim(); // get the type
            switch (type) { // switch for different data types
                case STRING:
                    String fixed = fieldElement.getAttribute(FIXED).trim();
                    if (fixed.equals("true")) {
                        field.setDataType(DataType.CHAR);
                    } else {
                        field.setDataType(DataType.VARCHAR);
                    }
                    String size = fieldElement.getAttribute(SIZE).trim();
                    field.setCharLength(Integer.parseInt(size));
                    break;
                case INT:
                    field.setDataType(DataType.INTEGER);
                    String autoIncrement = fieldElement.getAttribute("autoincrement").trim();
                    field.setAutoIncrement(autoIncrement.equals("true"));
                    break;
                default:
                    errorMessage = "The datatype \"" + type + "\" is not supported by this parser.";
                    return false;
            }

            String pkey = fieldElement.getAttribute("pkey").trim(); // get primary key status
            field.setIsPrimaryKey(pkey.equals("true"));

            String allowNull = fieldElement.getAttribute("null").trim(); // check to see if field allows null values
            field.setAllowNull(allowNull.equals("true"));

            field.setTable(table); // set the fields table
            table.addField(field); // add the table to the field
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Parse the given relation element and make the appropriate changes to the
     * schema.
     *
     * @param relationElement The relation element to be parsed
     * @return Whether or not the relation was parsed successfully
     */
    private boolean parseRelation(Element relationElement) {
        try {
            List<Element> nameElements = getChildrenByName(relationElement, "name"); // get the relation names
            if (nameElements.size() != 1) { // check to ensure the relation has a name
                errorMessage = "Each relation must have exactly one \"name\" element.";
                return false;
            }

            Element nameElement = nameElements.get(0); // get the name element
            String name = nameElement.getTextContent().trim(); // get the name

            List<Element> parentElements = getChildrenByName(relationElement, "parent");
            if (parentElements.size() != 1) {
                errorMessage = "Each relation must have exactly one \"parent\" element.";
                return false;
            }

            Element parentElement = parentElements.get(0); // get the parent 
            String parentCardinality = parentElement.getAttribute("cardinality").trim(); // get the cardinality

            List<Element> parentTablenameElements = getChildrenByName(parentElement, "tablename"); // get the table name
            if (parentTablenameElements.size() != 1) { // check to ensure the relationship parent has a table name
                errorMessage = "Each parent must have exactly one \"tablename\" element.";
                return false;
            }

            Element parentTablenameElement = parentTablenameElements.get(0); // get the parent tablename
            String parentTablename = parentTablenameElement.getTextContent().trim();
            Table parentTable = schema.getTableByName(parentTablename); // get the table
            if (parentTable == null) { // check to ensure the table exists
                errorMessage = "The relation \"" + name + "\" references nonexistent table \""
                        + parentTablename + "\"";
                return false;
            }

            List<Element> childElements = getChildrenByName(relationElement, "child"); // get the child elements
            if (childElements.size() != 1) { // ensure that the relation has a child element
                errorMessage = "Each relation must have exactly one \"child\" element.";
                return false;
            }

            Element childElement = childElements.get(0); // get the child element and cardinality
            String childCardinality = childElement.getAttribute("cardinality").trim();

            if (parentCardinality.equals("many") && childCardinality.equals("many")) { // check to see if many to many and change error
                errorMessage = "Many-to-many relations are not supported by this software.";
                return false;
            }

            List<Element> childTablenameElements = getChildrenByName(childElement, "tablename"); // get name of child element
            if (childTablenameElements.size() != 1) { // ensure it has a name
                errorMessage = "Each child must have exactly one \"tablename\" element.";
                return false;
            }

            Element childTablenameElement = childTablenameElements.get(0); // get name of child element
            String childTablename = childTablenameElement.getTextContent().trim();
            Table childTable = schema.getTableByName(childTablename); // get table by name
            if (childTable == null) { //ensure table exists
                errorMessage = "The relation \"" + name + "\" references nonexistent table \""
                        + childTablename + "\"";
                return false;
            }

            List<Element> childForeignkeyElements = getChildrenByName(childElement, "foreignkey"); // get foreign key element
            if (childForeignkeyElements.size() != 1) { // ensure there is foreign key element
                errorMessage = "Each child must have exactly one \"foreignkey\" element.";
                return false;
            }

            Element childForeignkeyElement = childForeignkeyElements.get(0); // get the foreign key reference
            String childForeignkey = childForeignkeyElement.getTextContent().trim();
            String childForeignkeyReference = childForeignkeyElement.getAttribute("references").trim();

            Field childField = childTable.getFieldByName(childForeignkey); // get the field
            if (childField == null) { // check to ensure field exists
                errorMessage = "The relation \"" + name + "\" references nonexistent field \""
                        + childTablename + "." + childForeignkey + "\"";
                return false;
            }

            Field parentField = parentTable.getFieldByName(childForeignkeyReference); // get the parent field
            if (parentField == null) { //ensure it exists
                errorMessage = "The relation \"" + name + "\" references nonexistent field \""
                        + parentTablename + "." + childForeignkeyReference + "\"";
                return false;
            }

            childTable.addRelatedTable(parentTable); // add related table to child table
            childTable.setRelatedField(childField, parentField); // set the related fields
            childField.setForeignField(parentField); // set the foreign key field

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the current schema
     * @return the schema
     */
    @Override
    public Schema getSchema() {
        return schema;
    }

    /**
     * Gets the error messaage
     * @return the error message
     */
    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Get all immediate child Elements with the given name from the given
     * parent Element. Returns null if none found.
     *
     * @param element Parent element to get children of
     * @param childName Name of children to get
     * @return Child elements with the given name or empty list
     */
    private List<Element> getChildrenByName(Element element, String childName) {
        List<Element> elements = new ArrayList<>();
        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element) {
                if (child.getNodeName().equals(childName)) {
                    elements.add((Element) child);
                }
            }
        }
        return elements;
    }

    /**
     * Determine whether a table with the given name has already been parsed.
     *
     * @param name The name to check for duplicates of
     * @return Whether or not there are duplicate tables with the given name
     */
    private boolean isTableNameDuplicate(String name) {
        for (Table table : schema.getTables()) {
            if (table.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
