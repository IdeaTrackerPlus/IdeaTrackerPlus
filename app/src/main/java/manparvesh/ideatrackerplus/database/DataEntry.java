package manparvesh.ideatrackerplus.database;

import android.provider.BaseColumns;

/**
 * Inner class that defines the table contents
 */
public abstract class DataEntry implements BaseColumns {

    // Contains the name of selected table
    public static String TABLE_NAME = "[]";

    // Column names
    public static final String COLUMN_NAME_ENTRY_ID = "entryid";
    public static final String COLUMN_NAME_TEXT = "text";
    public static final String COLUMN_NAME_NOTE = "note";
    public static final String COLUMN_NAME_PRIORITY = "priority";
    public static final String COLUMN_NAME_LATER = "later";
    public static final String COLUMN_NAME_DONE = "done";
    public static final String COLUMN_NAME_TEMP = "temp";
    public static final String COLUMN_NAME_NULLABLE = "null";

    /**
     * Change the selected table
     * @param newTableName
     */
    public static void setTableName(String newTableName) {
        TABLE_NAME = "[" + newTableName + "]";
    }


}
