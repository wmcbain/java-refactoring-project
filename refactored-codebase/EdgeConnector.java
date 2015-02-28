package edgeconvert;

/**
 * Connector for EDGE
 * @author wyattmcbain
 */
public class EdgeConnector {
    
    // Declarations
    private int numConnector, endPoint1, endPoint2;
    private String endStyle1, endStyle2;
    private boolean isEP1Field, isEP2Field, isEP1Table, isEP2Table;
    
    /**
     * Construct an EdgeConnector with the given information.
     * @param connectorId The id of the connector
     * @param endPoint1 The first entity in the relation
     * @param endPoint2 The second entity in the relation
     * @param endStyle1 The type of entity endPoint1 is
     * @param endStyle2 The type of entity endPoint2 is
     */
    public EdgeConnector(int connectorId, int endPoint1, int endPoint2, String endStyle1, String endStyle2) {
        this.numConnector = connectorId;
        this.endPoint1 = endPoint1;
        this.endPoint2 = endPoint2;
        this.endStyle1 = endStyle1;
        this.endStyle2 = endStyle2;
        isEP1Field = false;
        isEP2Field = false;
        isEP1Table = false;
        isEP2Table = false;
    }

    /**
     * Gets the number of connectors
     * @return the number of connectors
     */
    public int getNumConnector() {
        return numConnector;
    }

    /**
     * Gets the 1st endpoint
     * @return the first endpoint value
     */
    public int getEndPoint1() {
        return endPoint1;
    }

    /**
     * Gets the 2nd endpoint
     * @return the value of the 2nd endpoint
     */
    public int getEndPoint2() {
        return endPoint2;
    }

    /**
     * Gets the first end style
     * @return the string value of the 1st end style
     */
    public String getEndStyle1() {
        return endStyle1;
    }

    /**
     * Gets the second end style
     * @return the string value of the 2ns end style
     */
    public String getEndStyle2() {
        return endStyle2;
    }
    
    /**
     * Checks value of ep1 field
     * @return boolean
     */
    public boolean getIsEP1Field() {
        return isEP1Field;
    }

    /**
     * Checks value of ep12 field
     * @return boolean
     */
    public boolean getIsEP2Field() {
        return isEP2Field;
    }

    /**
     * Checks value of ep1 table
     * @return boolean
     */
    public boolean getIsEP1Table() {
        return isEP1Table;
    }

    /**
     * Checks value of ep2 table
     * @return boolean
     */
    public boolean getIsEP2Table() {
        return isEP2Table;
    }

    /**
     * Sets value of ep1 field
     * @param value boolean
     */
    public void setIsEP1Field(boolean value) {
        isEP1Field = value;
    }

    /**
     * Sets value of ep2 field
     * @param value boolean
     */
    public void setIsEP2Field(boolean value) {
        isEP2Field = value;
    }

    /**
     * Sets the value of ep1 table
     * @param value boolean
     */
    public void setIsEP1Table(boolean value) {
        isEP1Table = value;
    }

    /**
     * Sets the value of ep2 table
     * @param value boolean
     */
    public void setIsEP2Table(boolean value) {
        isEP2Table = value;
    }
}
