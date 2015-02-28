package edgeconvert.output;

import edgeconvert.Table;
import java.util.List;
import net.xeoh.plugins.base.Plugin;

/**
 * Interface used to create DDL files from a schema.
 */
public interface DDLBuilder extends Plugin {

    /**
     * Get the name of the DBMS product that this class creates DDL for.
     *
     * @return The DBMS that DDL will be created for
     */
    public String getProductName();

    /**
     * Get the file extension for the filetype that this class creates.
     *
     * @return The file extension for the generated DDL
     */
    public String getFileExtension();

    /**
     * Provide the tables to be created by the generated DDL.
     *
     * @param tables The tables to be created by the DDL
     */
    public void setTables(List<Table> tables);

    /**
     * Set the name of the database created by the generated DDL
     *
     * @param dbName The database to be created by the DDL
     */
    public void setDatabaseName(String dbName);

    /**
     * Create a DDL string from the provided tables and database name.
     *
     * @return The DDL generated from the data provided.
     */
    public String buildDDL();

    /**
     * Determine whether the DDLBuilder requires a database name.
     *
     * @return Whether or not a database name is required
     */
    public boolean requiresDatabaseName();
}
