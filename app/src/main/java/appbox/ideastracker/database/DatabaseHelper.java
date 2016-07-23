package appbox.ideastracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;

/**
 * Created by Nicklos on 12/07/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;
    private static BaseExpandableListAdapter mExpandleAdapter;
    private static BaseAdapter mAdapterLater, mAdapterDone;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "MyIdeas.db";

    //SQL COMMANDS
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";
    private static final String BOOL_TYPE = " BOOLEAN";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DataEntry.TABLE_NAME + " (" +
                    DataEntry._ID + " INTEGER PRIMARY KEY," +
                    DataEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    DataEntry.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
                    DataEntry.COLUMN_NAME_PRIORITY + INT_TYPE + COMMA_SEP +
                    DataEntry.COLUMN_NAME_DONE + BOOL_TYPE + COMMA_SEP +
                    DataEntry.COLUMN_NAME_LATER + BOOL_TYPE + COMMA_SEP +
                    DataEntry.COLUMN_NAME_TEMP + BOOL_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME;

    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: change in release version
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void newTable(String tableName) {
        DataEntry.setTableName(tableName);
        String SQL_NEW_TABLE =
                "CREATE TABLE " + tableName + " (" +
                        DataEntry._ID + " INTEGER PRIMARY KEY," +
                        DataEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                        DataEntry.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
                        DataEntry.COLUMN_NAME_PRIORITY + INT_TYPE + COMMA_SEP +
                        DataEntry.COLUMN_NAME_DONE + BOOL_TYPE + COMMA_SEP +
                        DataEntry.COLUMN_NAME_LATER + BOOL_TYPE + COMMA_SEP +
                        DataEntry.COLUMN_NAME_TEMP + BOOL_TYPE +
                        " )";
        getWritableDatabase().execSQL(SQL_NEW_TABLE);
        notifyAllLists();
    }

    public void switchTable(String tableName) {
        DataEntry.setTableName(tableName);
        notifyAllLists();
    }

    public void renameTable(String newName) {
        getWritableDatabase().execSQL("ALTER TABLE " + DataEntry.TABLE_NAME + " RENAME TO " + newName);
        DataEntry.setTableName(newName);
        notifyAllLists();
    }

    public void deleteTable() {
        String SQL_DELETE =
                "DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME;
        getWritableDatabase().execSQL(SQL_DELETE);
    }


    public static void setAdapterIdea(BaseExpandableListAdapter adapter) {
        mExpandleAdapter = adapter;
    }

    public static void setAdapterLater(BaseAdapter adapter) {
        mAdapterLater = adapter;
    }

    public static void setAdapterDone(BaseAdapter adapter) {
        mAdapterDone = adapter;
    }

    public static void notifyAllLists() {
        if (mExpandleAdapter != null) mExpandleAdapter.notifyDataSetChanged();
        if (mAdapterLater != null) mAdapterLater.notifyDataSetChanged();
        if (mAdapterDone != null) mAdapterDone.notifyDataSetChanged();
    }

    public void newEntry(String text, int priority, boolean later) {

        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_NAME_ENTRY_ID, 0);
        values.put(DataEntry.COLUMN_NAME_TEXT, text);
        values.put(DataEntry.COLUMN_NAME_PRIORITY, priority);
        values.put(DataEntry.COLUMN_NAME_LATER, later);
        values.put(DataEntry.COLUMN_NAME_DONE, false);
        values.put(DataEntry.COLUMN_NAME_TEMP, false);

        getWritableDatabase().insert(
                DataEntry.TABLE_NAME,
                DataEntry.COLUMN_NAME_NULLABLE,
                values);
    }

    public Cursor getEntryById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {DataEntry.COLUMN_NAME_TEXT, DataEntry.COLUMN_NAME_PRIORITY};
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
        return "nothing";
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

    public void editEntry(int id, String new_text, int new_priority, boolean later) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_NAME_TEXT, new_text);
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

    /**
     * Retrieve the ideas of the NOW tab with the desired priority
     *
     * @param priority 0,1,2 for priority 1,2,3 respectively (-1 for all of them)
     * @return a list of the ideas paired with their id in the database
     */
    public ArrayList<Pair<Integer, String>> readIdeas(int priority) {
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

    public ArrayList<Pair<Integer, String>> readIdeas(boolean later) {
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

    public void deleteEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DataEntry.TABLE_NAME, "_id=" + id, null);
    }

    public void deleteEntryWithSnack(View view, final int id) {

        moveToTemp(id);

        Snackbar snackbar = Snackbar.make(view, "Idea deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recoverFromTemp(id);
                        notifyAllLists();
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


}

