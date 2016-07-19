package appbox.ideastracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.os.Handler;
import android.util.Pair;
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
    public static final int DATABASE_VERSION = 1;
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
                    DataEntry.COLUMN_NAME_LATER + BOOL_TYPE +
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


    public static synchronized void notifyAllLists() {
        if (mExpandleAdapter != null) mExpandleAdapter.notifyDataSetChanged();
        if (mAdapterLater != null) mAdapterLater.notifyDataSetChanged();
        if (mAdapterDone != null) mAdapterDone.notifyDataSetChanged();
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // To change in  release version
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
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
                "later=? and done=?",                    // The columns for the WHERE clause
                new String[]{"0", "0"},                  // The values for the WHERE clause
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
            where = "later=?";
        } else {
            where = "done=?";
        }

        Cursor cursor = db.query(
                DataEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                where,                                   // The columns for the WHERE clause
                new String[]{"1"},                      // The values for the WHERE clause
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

    public void deleteEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DataEntry.TABLE_NAME, "_id=" + id, null);
    }

    public void deleteIdeas(ArrayList<Pair<Integer, String>> ideas) {
        for (Pair<Integer, String> idea : ideas) {
            deleteEntry(idea.first);
        }
        notifyAllLists();
    }

    public void moveToTab(int tabNumber, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        switch (tabNumber){
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

        db.update(DataEntry.TABLE_NAME,values,"_id="+id,null);
    }

    public void moveAllToTab(int tabNumber, ArrayList<Pair<Integer, String>> ideas){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        switch (tabNumber){
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

        for(Pair<Integer, String> idea : ideas){
            db.update(DataEntry.TABLE_NAME,values,"_id="+idea.first,null);
        }
        notifyAllLists();
    }


}

