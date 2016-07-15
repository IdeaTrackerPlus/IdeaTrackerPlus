package appbox.ideastracker.database;

import android.provider.BaseColumns;

/**
 * Created by Nicklos on 12/07/2016.
 */
/* Inner class that defines the table contents */
public abstract class DataEntry implements BaseColumns {

    public static final String TABLE_NAME = "myGame";
    public static final String COLUMN_NAME_ENTRY_ID = "entryid";
    public static final String COLUMN_NAME_TEXT = "text";
    public static final String COLUMN_NAME_PRIORITY = "priority";
    public static final String COLUMN_NAME_LATER = "later";
    public static final String COLUMN_NAME_DONE = "done";
    public static final String COLUMN_NAME_NULLABLE = "null";


}
