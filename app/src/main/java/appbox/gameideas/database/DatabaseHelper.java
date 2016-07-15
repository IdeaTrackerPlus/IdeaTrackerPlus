package appbox.gameideas.database;

import android.content.Context;
import android.database.sqlite.*;
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

    public static synchronized void notifyListChanged(int tab){
        switch (tab){
            case 1: mExpandleAdapter.notifyDataSetChanged();
                break;

            case 2: mAdapterLater.notifyDataSetChanged();
                break;

            case 3 : mAdapterDone.notifyDataSetChanged();
                break;
        }

    }

    public static synchronized  void notifyAllLists(){
        if(mExpandleAdapter != null) mExpandleAdapter.notifyDataSetChanged();
        if(mAdapterLater != null) mAdapterLater.notifyDataSetChanged();
        if(mAdapterDone != null) mAdapterDone.notifyDataSetChanged();
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

    public static void setAdapterIdea(BaseExpandableListAdapter adapter){
        mExpandleAdapter = adapter;
    }
    public static void setAdapterLater(BaseAdapter adapter){
        mAdapterLater = adapter;
    }
    public static void setAdapterDone(BaseAdapter adapter){
        mAdapterDone = adapter;
    }


}

