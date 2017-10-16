package manparvesh.ideatrackerplus.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import manparvesh.ideatrackerplus.ItemAdapter;
import manparvesh.ideatrackerplus.MainActivity;
import manparvesh.ideatrackerplus.R;
import manparvesh.ideatrackerplus.SearchListAdapter;

/**
 * This class takes care of the interaction with the database
 * where the ideas are stored.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    // Store the list adapters to notify changes in the database
    private static ItemAdapter[] adapters = new ItemAdapter[4];

    // Needs to display the number of ideas at all time
    private static MainActivity mainActivity;

    // Keeps the ideas which have just been moved with "move all" to undo the action
    private static ArrayList<Pair<Integer, String>> movedIdeas;
    // Keep the idea last moved with "moveToTab" to undo the action
    private static Pair<Integer, Integer> lastMoved;
    private static int lastMovedOrderIndex;

    // If the database schema change, must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "MyIdeas.db";

    //SQL COMMANDS
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";
    private static final String BOOL_TYPE = " BOOLEAN";
    private static final String COMMA_SEP = ",";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME;

    /**
     * This methods is used to get an instance of the class and ensure
     * uniqueness (singleton)
     *
     * @param context
     * @return the helper object
     */
    public static synchronized DatabaseHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    // SET ADAPTERS

    public static void setAdapterAtTab(int tabNumber, ItemAdapter adapter) {
        if (1 <= tabNumber && tabNumber <= 3) {
            adapters[tabNumber] = adapter;
        }
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mainActivity = MainActivity.getInstance();
    }

    /**
     * Nothing is created at first
     *
     * @param db
     */
    public void onCreate(SQLiteDatabase db) {
        //Do nothing
    }

    /**
     * Called when newer version of the database
     * so users don't their data
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Notify the list adapters of the data changes
     */
    public static void notifyAllLists() {

        DatabaseHelper helper = DatabaseHelper.getInstance(mainActivity);

        //Notify all 3 tabs
        for (int tab = 1; tab <= 3; tab++) {
            if (adapters[tab] != null) adapters[tab].setItemList(helper.readIdeas(tab));
        }

        //Notify search tab
        SearchListAdapter.getInstance(mainActivity, false).notifyDataSetChanged();

    }


    //TABLE OPERATIONS

    /**
     * Creates a new table (project) in the database
     *
     * @param tableName
     */
    public void newTable(String tableName) {

        DataEntry.setTableName(tableName);
        String SQL_NEW_TABLE =
                "CREATE TABLE " + DataEntry.TABLE_NAME + " (" +
                        DataEntry._ID + " INTEGER PRIMARY KEY," +
                        DataEntry.COLUMN_NAME_ENTRY_ID + INT_TYPE + COMMA_SEP +
                        DataEntry.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
                        DataEntry.COLUMN_NAME_NOTE + TEXT_TYPE + COMMA_SEP +
                        DataEntry.COLUMN_NAME_PRIORITY + INT_TYPE + COMMA_SEP +
                        DataEntry.COLUMN_NAME_DONE + BOOL_TYPE + COMMA_SEP +
                        DataEntry.COLUMN_NAME_LATER + BOOL_TYPE + COMMA_SEP +
                        DataEntry.COLUMN_NAME_TEMP + BOOL_TYPE +
                        " )";
        getWritableDatabase().execSQL(SQL_NEW_TABLE);
        notifyAllLists();
    }

    /**
     * Select another table (project)
     *
     * @param tableName
     */
    public void switchTable(String tableName) {
        DataEntry.setTableName(tableName);
        notifyAllLists();
    }

    /**
     * Rename the current table
     *
     * @param newName
     */
    public void renameTable(String newName) {
        getWritableDatabase().execSQL("ALTER TABLE " + DataEntry.TABLE_NAME + " RENAME TO " + "[" + newName + "]");
        DataEntry.setTableName(newName);
        notifyAllLists();
    }

    /**
     * Delete the current table
     */
    public void deleteTable() {
        String SQL_DELETE =
                "DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME;
        getWritableDatabase().execSQL(SQL_DELETE);
    }


    //ENTRY OPERATIONS

    /**
     * Create an entry (idea) in the current table (project)
     *
     * @param text
     * @param note
     * @param priority range from 1 (high) to 3 (low)
     * @param later    if the idea should be in the "LATER" tab
     */
    public void newEntry(String text, String note, int priority, boolean later) {

        ContentValues values = new ContentValues();
        if (later) {
            values.put(DataEntry.COLUMN_NAME_ENTRY_ID, getLastOrderIndex(2) + 1); // The order index is set so the item is the last in the list
        } else {
            values.put(DataEntry.COLUMN_NAME_ENTRY_ID, getLastOrderIndex(1) + 1);
        }
        values.put(DataEntry.COLUMN_NAME_TEXT, text);
        values.put(DataEntry.COLUMN_NAME_NOTE, note);
        values.put(DataEntry.COLUMN_NAME_PRIORITY, priority);
        values.put(DataEntry.COLUMN_NAME_LATER, later);
        values.put(DataEntry.COLUMN_NAME_DONE, false);
        values.put(DataEntry.COLUMN_NAME_TEMP, false);

        getWritableDatabase().insert(
                DataEntry.TABLE_NAME,
                DataEntry.COLUMN_NAME_NULLABLE,
                values);
    }


    /**
     * Find an entry cursor with its id
     *
     * @param id
     * @return a Cursor object containing the idea's information
     */
    public Cursor getEntryById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                DataEntry.TABLE_NAME,  // The table to query
                null,                               // The columns to return
                "_id=?",                    // The columns for the WHERE clause
                new String[]{Integer.toString(id)},                  // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
    }

    public String getTextById(int id) {
        Cursor cursor = null;
        try {
            cursor = getEntryById(id);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    return cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME_TEXT));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "Nothing";
    }

    public String getNoteById(int id) {
        Cursor cursor = null;
        try {
            cursor = getEntryById(id);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    return cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME_NOTE));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "Nothing";
    }

    public int getPriorityById(int id) {
        Cursor cursor = null;
        try {
            cursor = getEntryById(id);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    return cursor.getInt(cursor.getColumnIndex(DataEntry.COLUMN_NAME_PRIORITY));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public int getPriorityColorById(int id) {
        int priority = getPriorityById(id);

        switch (priority) {
            case 1:
                return R.color.priority1;

            case 2:
                return R.color.priority2;

            case 3:
                return R.color.priority3;
        }

        return R.color.white;
    }

    public int getOrderIndexById(int id) {
        Cursor cursor = null;
        try {
            cursor = getEntryById(id);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    return cursor.getInt(cursor.getColumnIndex(DataEntry.COLUMN_NAME_ENTRY_ID));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public int getTabById(int id) {

        Cursor cursor = null;
        try {
            cursor = getEntryById(id);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    boolean later = cursor.getInt(cursor.getColumnIndex(DataEntry.COLUMN_NAME_LATER)) > 0;
                    boolean done = cursor.getInt(cursor.getColumnIndex(DataEntry.COLUMN_NAME_DONE)) > 0;

                    if (done) {
                        return 3;
                    } else if (later) {
                        return 2;
                    }
                    return 1;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 1;
    }

    /**
     * Modify an entry's values
     *
     * @param id
     * @param new_text
     * @param new_note
     * @param new_priority
     * @param later
     */
    public void editEntry(int id, String new_text, String new_note, int new_priority, boolean later) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_NAME_TEXT, new_text);
        values.put(DataEntry.COLUMN_NAME_NOTE, new_note);
        values.put(DataEntry.COLUMN_NAME_PRIORITY, new_priority);
        if (later) {
            values.put(DataEntry.COLUMN_NAME_LATER, true);
            values.put(DataEntry.COLUMN_NAME_DONE, false);
        } else {
            values.put(DataEntry.COLUMN_NAME_LATER, false);
        }
        db.update(DataEntry.TABLE_NAME, values, "_id=" + id, null);
        notifyAllLists();
    }


    //MOVING ENTRIES

    public void deleteEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DataEntry.TABLE_NAME, "_id=" + id, null);
    }

    /**
     * Mark entry for deletion (temp = 1) and show a snackbar
     * giving the option to undo the deletion, if the users does not
     * the idea is deleted.
     *
     * @param view
     * @param id
     */
    public void deleteEntryWithSnack(View view, final int id) {

        moveToTemp(id);

        Snackbar snackbar = Snackbar.make(view, R.string.idea_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recoverFromTemp(id);
                        notifyAllLists();
                        mainActivity.displayIdeasCount();
                    }
                }).setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            //delete for real ideas in temp
                            deleteEntry(id);
                        }
                    }
                });
        snackbar.show();
    }

    public void moveToTemp(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_NAME_TEMP, true);
        db.update(DataEntry.TABLE_NAME, values, "_id=" + id, null);
    }

    public void recoverFromTemp(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_NAME_TEMP, false);
        db.update(DataEntry.TABLE_NAME, values, "_id=" + id, null);
    }

    public void moveAllToTemp(ArrayList<Pair<Integer, String>> ideas) {
        for (Pair<Integer, String> idea : ideas) {
            moveToTemp(idea.first);
        }
        notifyAllLists();
    }

    public void recoverAllFromTemp() {
        ArrayList<Integer> temp = readTempIdeas();
        for (int id : temp) {
            recoverFromTemp(id);
        }
        notifyAllLists();
    }

    public void deleteAllFromTemp() {
        ArrayList<Integer> temp = readTempIdeas();
        for (int id : temp) {
            deleteEntry(id);
        }
        notifyAllLists();
    }

    /**
     * Move an idea to another tab displaying a snackbar allowing to undo the action
     *
     * @param view a reference view to display to snackbar
     * @param from tab numer the idea comes from
     * @param to   tab number where to move the idea
     * @param id   unique id of the idea in database
     */
    public void moveToTabWithSnack(View view, int from, int to, int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Resources res = mainActivity.getResources();
        String destination = "";

        switch (to) {
            case 1: //NOW
                destination = res.getString(R.string.first_tab);
                values.put(DataEntry.COLUMN_NAME_ENTRY_ID, getLastOrderIndex(1) + 1);
                values.put(DataEntry.COLUMN_NAME_DONE, false);
                values.put(DataEntry.COLUMN_NAME_LATER, false);
                break;

            case 2: //LATER
                destination = res.getString(R.string.second_tab);
                values.put(DataEntry.COLUMN_NAME_ENTRY_ID, getLastOrderIndex(2) + 1);
                values.put(DataEntry.COLUMN_NAME_DONE, false);
                values.put(DataEntry.COLUMN_NAME_LATER, true);
                break;

            case 3: //DONE
                destination = res.getString(R.string.third_tab);
                values.put(DataEntry.COLUMN_NAME_ENTRY_ID, getLastOrderIndex(3) + 1);
                values.put(DataEntry.COLUMN_NAME_LATER, false);
                values.put(DataEntry.COLUMN_NAME_DONE, true);
                break;
        }

        //Keeps the idea info if action has to be undone
        lastMoved = new Pair<>(from, id);
        lastMovedOrderIndex = getOrderIndexById(id);

        db.update(DataEntry.TABLE_NAME, values, "_id=" + id, null);

        //Show snackbar allowing undo action
        Snackbar snackbar = Snackbar.make(view, res.getString(R.string.idea_moved_snack) + destination, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        undoLastMove();
                        mainActivity.displayIdeasCount();
                    }
                });
        snackbar.show();
    }

    /**
     * Move an idea to another tab
     *
     * @param to
     * @param id
     * @param orderIndex
     */
    public void moveToTab(int to, int id, int orderIndex) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        switch (to) {
            case 1: //NOW
                values.put(DataEntry.COLUMN_NAME_DONE, false);
                values.put(DataEntry.COLUMN_NAME_LATER, false);
                break;

            case 2: //LATER
                values.put(DataEntry.COLUMN_NAME_DONE, false);
                values.put(DataEntry.COLUMN_NAME_LATER, true);
                break;

            case 3: //DONE
                values.put(DataEntry.COLUMN_NAME_LATER, false);
                values.put(DataEntry.COLUMN_NAME_DONE, true);
                break;
        }
        values.put(DataEntry.COLUMN_NAME_ENTRY_ID, orderIndex);

        db.update(DataEntry.TABLE_NAME, values, "_id=" + id, null);
    }

    // Move back the last idea moved with moveToTab method
    private void undoLastMove() {
        moveToTab(lastMoved.first, lastMoved.second, lastMovedOrderIndex);
        notifyAllLists();
    }

    public void moveAllToTab(int tabNumber, ArrayList<Pair<Integer, String>> ideas) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        switch (tabNumber) {
            case 1: //NOW
                values.put(DataEntry.COLUMN_NAME_DONE, false);
                values.put(DataEntry.COLUMN_NAME_LATER, false);
                break;

            case 2: //LATER
                values.put(DataEntry.COLUMN_NAME_DONE, false);
                values.put(DataEntry.COLUMN_NAME_LATER, true);
                break;

            case 3: //DONE
                values.put(DataEntry.COLUMN_NAME_LATER, false);
                values.put(DataEntry.COLUMN_NAME_DONE, true);
                break;
        }

        for (Pair<Integer, String> idea : ideas) {
            db.update(DataEntry.TABLE_NAME, values, "_id=" + idea.first, null);
        }
        notifyAllLists();
    }

    /**
     * Move all ideas from a tab to another
     *
     * @param from
     * @param to
     */
    public boolean moveAllFromTo(String from, String to) {

        ArrayList<Pair<Integer, String>> ideas = new ArrayList<>();
        Context c = mainActivity.getApplicationContext();

        if (from.equals(c.getString(R.string.first_tab))) { //get all the ideas from NOW tab
            ideas = readIdeas(1);
        } else if (from.equals(c.getString(R.string.second_tab))) {//get all the ideas from LATER tab
            ideas = readIdeas(2);
        } else if (from.equals(c.getString(R.string.third_tab))) {//get all the ideas from DONE tab
            ideas = readIdeas(3);
        }


        if (ideas.size() == 0) return false; //nothing to move
        movedIdeas = ideas; //store the ideas for UNDO action


        if (to.equals(c.getString(R.string.first_tab))) {
            moveAllToTab(1, ideas);
        } else if (to.equals(c.getString(R.string.second_tab))) {
            moveAllToTab(2, ideas);
        } else if (to.equals(c.getString(R.string.third_tab))) {
            moveAllToTab(3, ideas);
        } else if (to.equals(c.getString(R.string.trash))) {
            moveAllToTemp(ideas);
        }
        return true;
    }


    public void clearDoneWithSnack(View v) {

        //put all done ideas in temps
        moveAllFromTo("Done", "Trash");

        Snackbar snackbar = Snackbar.make(v, R.string.done_cleared, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recoverAllFromTemp();
                    }
                }).setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE) {
                            deleteAllFromTemp();
                        }
                    }
                });
        snackbar.show();
    }


    //READING OPERATIONS

    /**
     * Retrieve the ideas of the LATER or DONE tab with the desired priority
     *
     * @param tabNumber 1 for NOW/IDEAS, 2 for LATER, 3 for DONE
     * @return a list of the ideas paired with their id in the database
     */
    public ArrayList<Pair<Integer, String>> readIdeas(int tabNumber) {

        if (!DataEntry.TABLE_NAME.equals("[]")) {

            SQLiteDatabase db = this.getReadableDatabase();

            // Only the text and id will be read
            String[] projection = {DataEntry._ID, DataEntry.COLUMN_NAME_TEXT};

            // How you want the results sorted in the resulting Cursor
            String sortOrder = DataEntry.COLUMN_NAME_ENTRY_ID + " ASC";

            //Define the where condition
            String where = "";
            String[] values = {};
            switch (tabNumber) {
                case 1:
                    where = "later=? and done=? and temp=?";
                    values = new String[]{"0", "0", "0"};
                    break;

                case 2:
                    where = "later=? and temp=?";
                    values = new String[]{"1", "0"};
                    break;

                case 3:
                    where = "done=? and temp=?";
                    values = new String[]{"1", "0"};
                    break;
            }

            Cursor cursor = null;
            ArrayList<Pair<Integer, String>> ideas = new ArrayList<>();
            try {
                cursor = db.query(
                        DataEntry.TABLE_NAME,  // The table to query
                        projection,                               // The columns to return
                        where,                                   // The columns for the WHERE clause
                        values,                      // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        sortOrder                                 // The sort order
                );


                Pair<Integer, String> pair;

                //Scan the ideas and return everything
                if (cursor.moveToFirst()) {

                    while (!cursor.isAfterLast()) {
                        String text = cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME_TEXT));
                        int id = cursor.getInt(cursor.getColumnIndex(DataEntry._ID));
                        pair = new Pair<>(id, text);
                        ideas.add(pair);
                        cursor.moveToNext();
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return ideas;
        }

        return new ArrayList<>();
    }

    /**
     * Count the ideas in the different tabs for the current project (table)
     *
     * @param tabNumber 0 for NOW+LATER, 1 for Now/IDEAS, 2 for LATER, 3 for DONE
     * @return
     */
    public int getIdeasCount(int tabNumber) {

        int count = 0;
        Cursor cursor = null;
        try {
            switch (tabNumber) {
                case 0: //NOW+LATER
                    cursor = getReadableDatabase().rawQuery("select count(*) from " + DataEntry.TABLE_NAME + " where done=0 and temp=0", null);
                    break;

                case 1: //NOW/IDEAS
                    cursor = getReadableDatabase().rawQuery("select count(*) from " + DataEntry.TABLE_NAME + " where done=0 and temp=0 and later=0", null);
                    break;

                case 2: //LATER
                    cursor = getReadableDatabase().rawQuery("select count(*) from " + DataEntry.TABLE_NAME + " where done=0 and temp=0 and later=1", null);
                    break;

                case 3: //DONE
                    cursor = getReadableDatabase().rawQuery("select count(*) from " + DataEntry.TABLE_NAME + " where done=1 and temp=0", null);
                    break;
            }

            cursor.moveToFirst();
            count = cursor.getInt(0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;
    }

    /**
     * Get the last (and bigger) order index of the given tab item list
     *
     * @param tabNumber
     * @return
     */
    public int getLastOrderIndex(int tabNumber) {

        int lastOrderIndex = -1;

        if (!DataEntry.TABLE_NAME.equals("[]")) {

            SQLiteDatabase db = this.getReadableDatabase();

            // Only the text and priority will be read
            String[] projection = {DataEntry.COLUMN_NAME_ENTRY_ID};

            // How you want the results sorted in the resulting Cursor
            String sortOrder = DataEntry.COLUMN_NAME_ENTRY_ID + " ASC";

            //Define the where condition
            String where = "";
            String[] values = {};
            switch (tabNumber) {
                case 1:
                    where = "later=? and done=? and temp=?";
                    values = new String[]{"0", "0", "0"};
                    break;

                case 2:
                    where = "later=? and temp=?";
                    values = new String[]{"1", "0"};
                    break;

                case 3:
                    where = "done=? and temp=?";
                    values = new String[]{"1", "0"};
                    break;
            }

            Cursor cursor = null;
            try {
                cursor = db.query(
                        DataEntry.TABLE_NAME,  // The table to query
                        projection,                               // The columns to return
                        where,                                   // The columns for the WHERE clause
                        values,                      // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        sortOrder                                 // The sort order
                );


                if (cursor.moveToLast()) {
                    lastOrderIndex = cursor.getInt(cursor.getColumnIndex(DataEntry.COLUMN_NAME_ENTRY_ID));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return lastOrderIndex;
    }

    /**
     * Get the temporary ideas (temp = 1)
     *
     * @return a list of their ids
     */
    public ArrayList<Integer> readTempIdeas() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {DataEntry._ID};

        ArrayList<Integer> temps = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DataEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    "temp=?",                                   // The columns for the WHERE clause
                    new String[]{"1"},                      // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );


            //Scan the ideas and return everything
            if (cursor.moveToFirst()) {

                while (!cursor.isAfterLast()) {
                    int id = cursor.getInt(cursor.getColumnIndex(DataEntry._ID));
                    temps.add(id);
                    cursor.moveToNext();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return temps;
    }

    /**
     * Search for ideas using a substring,
     * match can occur in the title or the note of the idea,
     * search is case insensitive
     *
     * @param sub
     * @return
     */
    public ArrayList<Pair<Integer, String>> searchIdeas(String sub) {
        if (!DataEntry.TABLE_NAME.equals("[]")) {

            SQLiteDatabase db = this.getReadableDatabase();

            // Only the text and id will be read
            String[] projection = {DataEntry._ID, DataEntry.COLUMN_NAME_TEXT, DataEntry.COLUMN_NAME_NOTE};

            // How you want the results sorted in the resulting Cursor
            String sortOrder = DataEntry.COLUMN_NAME_TEXT + " ASC";

            //Define the where condition, all not temps ideas
            String where = "temp=?";
            String[] values = new String[]{"0"};

            Cursor cursor = null;
            ArrayList<Pair<Integer, String>> ideas = new ArrayList<>();
            try {
                cursor = db.query(
                        DataEntry.TABLE_NAME,  // The table to query
                        projection,                               // The columns to return
                        where,                                   // The columns for the WHERE clause
                        values,                      // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        sortOrder                                 // The sort order
                );


                Pair<Integer, String> pair;

                //Scan the ideas and return everything
                if (cursor.moveToFirst()) {

                    while (!cursor.isAfterLast()) {
                        String text = cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME_TEXT));
                        String note = cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME_NOTE));
                        int id = cursor.getInt(cursor.getColumnIndex(DataEntry._ID));

                        if (text.toLowerCase().contains(sub.toLowerCase()) || note.toLowerCase().contains(sub.toLowerCase())) {
                            pair = new Pair<>(id, text);
                            ideas.add(pair);
                        }
                        cursor.moveToNext();
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return ideas;
        }

        return new ArrayList<>();
    }


    //ORDER OPERATIONS

    /**
     * Reset the order indexes of the ideas to match the order displayed
     * Usefull after a manual reorder (long click)
     *
     * @param tabNumber
     */
    public void resetEntriesOrderAt(int tabNumber) {

        //Get the list with right order
        List<Pair<Integer, String>> itemList = adapters[tabNumber].getItemList();

        SQLiteDatabase db = this.getWritableDatabase();
        int indexOrder = 0;

        for (Pair<Integer, String> item : itemList) {
            ContentValues values = new ContentValues();
            values.put(DataEntry.COLUMN_NAME_ENTRY_ID, indexOrder);
            db.update(DataEntry.TABLE_NAME, values, "_id=" + item.first, null);
            indexOrder++;
        }

    }

    public void sortByAscPriorityAt(int tabNumber) {

        if (adapters[tabNumber] != null) {
            //Get the list with right order
            List<Pair<Integer, String>> itemList = adapters[tabNumber].getItemList();

            ArrayList<Integer> p1 = new ArrayList<>(), p2 = new ArrayList<>(), p3 = new ArrayList<>();

            //Separate the ideas by priority in 3 lists
            int priority = 1;
            int id = 0;
            for (Pair<Integer, String> item : itemList) {
                id = item.first;
                priority = getPriorityById(id);
                switch (priority) {
                    case 1:
                        p1.add(id);
                        break;

                    case 2:
                        p2.add(id);
                        break;

                    default:
                        p3.add(id);
                        break;
                }
            }

            //Go through the lists by ascendent priority and assign the order indexes
            SQLiteDatabase db = this.getWritableDatabase();
            int indexOrder = 0;
            ContentValues values;
            for (Integer i1 : p1) {
                values = new ContentValues();
                values.put(DataEntry.COLUMN_NAME_ENTRY_ID, indexOrder);
                db.update(DataEntry.TABLE_NAME, values, "_id=" + i1, null);
                indexOrder++;
            }
            for (Integer i2 : p2) {
                values = new ContentValues();
                values.put(DataEntry.COLUMN_NAME_ENTRY_ID, indexOrder);
                db.update(DataEntry.TABLE_NAME, values, "_id=" + i2, null);
                indexOrder++;
            }
            for (Integer i3 : p3) {
                values = new ContentValues();
                values.put(DataEntry.COLUMN_NAME_ENTRY_ID, indexOrder);
                db.update(DataEntry.TABLE_NAME, values, "_id=" + i3, null);
                indexOrder++;
            }
        }

    }

    public void sortByAscPriority() {
        for (int tab = 1; tab <= 3; tab++) {
            sortByAscPriorityAt(tab);
        }
        //Send the newly ordered lists for display
        notifyAllLists();
    }


}

