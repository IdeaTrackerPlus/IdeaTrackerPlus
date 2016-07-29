package appbox.ideastracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;

import appbox.ideastracker.MainActivity;

/**
 * This class takes care of the interaction with the database
 * where the ideas are stored.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    // Store the list adapters to notify changes in the database
    private static BaseExpandableListAdapter mExpandleAdapter;
    private static BaseAdapter mAdapterLater, mAdapterDone;

    // Needs to display the number of ideas at all time
    private static MainActivity mainActivity;

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
     * @param context
     * @return the helper object
     */
    public static synchronized DatabaseHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public static void setMainActivity(MainActivity act){
        mainActivity = act;

    }

    // SET ADAPTERS

    public static void setAdapterIdea(BaseExpandableListAdapter adapter) {
        mExpandleAdapter = adapter;
    }

    public static void setAdapterLater(BaseAdapter adapter) {
        mAdapterLater = adapter;
    }

    public static void setAdapterDone(BaseAdapter adapter) {
        mAdapterDone = adapter;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Nothing is created at first
     * @param db
     */
    public void onCreate(SQLiteDatabase db) {
        //Do nothing
    }

    /**
     * Called when newer version of the database
     * so users don't their data
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

        if (mExpandleAdapter != null) mExpandleAdapter.notifyDataSetChanged();
        if (mAdapterLater != null) mAdapterLater.notifyDataSetChanged();
        if (mAdapterDone != null) mAdapterDone.notifyDataSetChanged();
    }

    //TABLE OPERATIONS

    /**
     * Creates a new table (project) in the database
     * @param tableName
     */
    public void newTable(String tableName) {

        DataEntry.setTableName(tableName);
        String SQL_NEW_TABLE =
                "CREATE TABLE " + DataEntry.TABLE_NAME + " (" +
                        DataEntry._ID + " INTEGER PRIMARY KEY," +
                        DataEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
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
     * @param tableName
     */
    public void switchTable(String tableName) {
        DataEntry.setTableName(tableName);
        notifyAllLists();
    }

    /**
     * Rename the current table
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
     * @param text
     * @param note
     * @param priority range from 1 (high) to 3 (low)
     * @param later if the idea should be in the "LATER" tab
     */
    public void newEntry(String text, String note, int priority, boolean later) {

        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_NAME_ENTRY_ID, 0);
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
     * @param id
     * @return a Cursor object containing the idea's information
     */
    public Cursor getEntryById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {DataEntry.COLUMN_NAME_TEXT, DataEntry.COLUMN_NAME_PRIORITY, DataEntry.COLUMN_NAME_NOTE};
        return db.query(
                DataEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                "_id=?",                    // The columns for the WHERE clause
                new String[]{Integer.toString(id)},                  // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
    }

    public String getTextById(int id) {
        Cursor cursor = getEntryById(id);
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                return cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME_TEXT));
            }
        }
        cursor.close();
        return "Nothing";
    }

    public String getNoteById(int id) {
        Cursor cursor = getEntryById(id);
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                return cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME_NOTE));
            }
        }
        cursor.close();
        return "Nothing";
    }

    public int getPriorityById(int id) {
        Cursor cursor = getEntryById(id);
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                return cursor.getInt(cursor.getColumnIndex(DataEntry.COLUMN_NAME_PRIORITY));
            }
        }
        cursor.close();
        return 0;
    }

    /**
     * Modify an entry's values
     * @param id
     * @param new_text
     * @param new_note
     * @param new_priority
     * @param later
     */
    public void editEntry(int id, String new_text,String new_note, int new_priority, boolean later) {

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

        Snackbar snackbar = Snackbar.make(view, "Idea deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
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
        Log.d("NICKLOS", Integer.toString(temp.size()));
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
     * Move an idea to another tab
     * @param tabNumber
     * @param id
     */
    public void moveToTab(int tabNumber, int id) {
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

        db.update(DataEntry.TABLE_NAME, values, "_id=" + id, null);
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
        switch (from) {
            case "Ideas": //get all the ideas from NOW tab
                ideas = readIdeas(-1);
                break;

            case "Later": //get all the ideas from LATER tab
                ideas = readIdeas(true);
                break;

            case "Done": //get all the ideas from LATER tab
                ideas = readIdeas(false);
                break;
        }
        if (ideas.size() == 0) return false; //nothing to move

        switch (to) {
            case "Ideas":
                moveAllToTab(1, ideas);
                break;

            case "Later":
                moveAllToTab(2, ideas);
                break;

            case "Done":
                moveAllToTab(3, ideas);
                break;

            case "Trash":
                moveAllToTemp(ideas);
                break;
        }
        return true;
    }

    //READING OPERATIONS

    /**
     * Retrieve the ideas of the NOW tab with the desired priority
     * @param priority 0,1,2 for priority 1,2,3 respectively (-1 for all of them)
     * @return a list of the ideas paired with their id in the database
     */
    public ArrayList<Pair<Integer, String>> readIdeas(int priority) {

        if (!DataEntry.TABLE_NAME.equals("[]")) {

            SQLiteDatabase db = this.getReadableDatabase();

            // Only the text and priority will be read
            String[] projection = {DataEntry._ID, DataEntry.COLUMN_NAME_TEXT, DataEntry.COLUMN_NAME_PRIORITY};

            // How you want the results sorted in the resulting Cursor
            String sortOrder = DataEntry._ID + " ASC";

            Cursor cursor = db.query(
                    DataEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    "later=? and done=? and temp=?",                    // The columns for the WHERE clause
                    new String[]{"0", "0", "0"},                  // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            ArrayList<Pair<Integer, String>> ideas = new ArrayList<>();
            Pair<Integer, String> pair;

            //Scan the ideas and return only the one with the expected priority
            if (cursor.moveToFirst()) {

                while (cursor.isAfterLast() == false) {
                    String text = cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME_TEXT));
                    int id = cursor.getInt(cursor.getColumnIndex(DataEntry._ID));
                    int prio = cursor.getInt(cursor.getColumnIndex(DataEntry.COLUMN_NAME_PRIORITY));
                    if (prio == priority + 1) {
                        pair = new Pair<>(id, text);
                        ideas.add(pair);
                    } else if (priority == -1) { // if priority -1, add anyway
                        pair = new Pair<>(id, text);
                        ideas.add(pair);
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return ideas;
        }

        return new ArrayList<>();
    }

    /**
     * Retrieve the ideas of the LATER or DONE tab with the desired priority
     * @param later true for LATER tab
     * @return a list of the ideas paired with their id in the database
     */
    public ArrayList<Pair<Integer, String>> readIdeas(boolean later) {

        if (!DataEntry.TABLE_NAME.equals("[]")) {

            SQLiteDatabase db = this.getReadableDatabase();

            // Only the text and priority will be read
            String[] projection = {DataEntry._ID, DataEntry.COLUMN_NAME_TEXT};

            // How you want the results sorted in the resulting Cursor
            String sortOrder = DataEntry._ID + " ASC";
            //Either get the "later" or the "done"
            String where = "";
            if (later) {
                where = "later=? and temp=?";
            } else {
                where = "done=? and temp=?";
            }

            Cursor cursor = db.query(
                    DataEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    where,                                   // The columns for the WHERE clause
                    new String[]{"1", "0"},                      // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            ArrayList<Pair<Integer, String>> ideas = new ArrayList<>();
            Pair<Integer, String> pair;

            //Scan the ideas and return everything
            if (cursor.moveToFirst()) {

                while (cursor.isAfterLast() == false) {
                    String text = cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME_TEXT));
                    int id = cursor.getInt(cursor.getColumnIndex(DataEntry._ID));
                    pair = new Pair<>(id, text);
                    ideas.add(pair);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return ideas;
        }

        return new ArrayList<>();
    }

    /**
     * Count the active ideas (not done) in the current project (table)
     *
     * @return
     */
    public int getIdeasCount() {
        Cursor mCount = getReadableDatabase().rawQuery("select count(*) from " + DataEntry.TABLE_NAME +
                " where done=0 and temp=0", null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();
        return count;
    }

    /**
     * Get the temporary ideas (temp = 1)
     * @return a list of their ids
     */
    public ArrayList<Integer> readTempIdeas() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {DataEntry._ID};
        Cursor cursor = db.query(
                DataEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                "temp=?",                                   // The columns for the WHERE clause
                new String[]{"1"},                      // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        ArrayList<Integer> temps = new ArrayList<>();
        //Scan the ideas and return everything
        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {
                int id = cursor.getInt(cursor.getColumnIndex(DataEntry._ID));
                temps.add(id);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return temps;
    }


}

