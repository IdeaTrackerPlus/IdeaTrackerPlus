package appbox.ideastracker.listadapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import appbox.ideastracker.recycleview.HorizontalAdapter;
import appbox.ideastracker.recycleview.MyRecyclerView;
import appbox.ideastracker.R;
import appbox.ideastracker.database.DataEntry;
import appbox.ideastracker.database.DatabaseHelper;

/**
 * Created by Nicklos on 13/07/2016.
 */
public class MyListAdapter extends BaseAdapter {

    private boolean mLater;
    private LayoutInflater inflater;
    private DatabaseHelper mDbHelper;

    public MyListAdapter(Context context, boolean later){
        this.inflater = LayoutInflater.from(context);
        mDbHelper = DatabaseHelper.getInstance(context);
        mLater = later;
    }

    @Override
    public int getCount() {
        return readIdeas().size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        MyRecyclerView horizontal_recycler_view;

        if (view == null) {
            view = inflater.inflate(R.layout.child_layout, parent,false);
            horizontal_recycler_view = (MyRecyclerView) view.findViewById(R.id.horizontal_recycler_view);
            ArrayList<Pair<Integer ,String >> ideas = readIdeas();
            Pair<Integer ,String > pair = ideas.get(position);
            HorizontalAdapter horizontalAdapter;
            if(mLater) horizontalAdapter = new HorizontalAdapter(pair.second,2);
            else horizontalAdapter = new HorizontalAdapter(pair.second,3);
            horizontal_recycler_view.setTag(pair.first);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(inflater.getContext(),LinearLayoutManager.HORIZONTAL,false);
            horizontalLayoutManager.scrollToPositionWithOffset(1, 0);
            horizontal_recycler_view.setLayoutManager(horizontalLayoutManager);
            horizontal_recycler_view.setAdapter(horizontalAdapter);
        }

        //Edit text of adapter and notify changes
        horizontal_recycler_view = (MyRecyclerView) view.findViewById(R.id.horizontal_recycler_view);
        ArrayList<Pair<Integer ,String >> ideas = readIdeas(); //get all ideas from priority
        Pair<Integer ,String > pair = ideas.get(position);
        HorizontalAdapter horizontalAdapter = (HorizontalAdapter) horizontal_recycler_view.getAdapter();
        horizontalAdapter.editText(pair.second);
        horizontalAdapter.notifyDataSetChanged();
        horizontal_recycler_view.setTag(pair.first);

        //return the entire view
        return view;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private ArrayList<Pair<Integer ,String >> readIdeas() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Only the text and priority will be read
        String[] projection = {DataEntry._ID, DataEntry.COLUMN_NAME_TEXT};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DataEntry._ID + " ASC";
        //Either get the "later" or the "done"
        String where = "";
        if(mLater){
            where = "later=?";
        }else{
            where = "done=?";
        }

        Cursor cursor = db.query(
                DataEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                where,                                   // The columns for the WHERE clause
                new String[] {"1"},                      // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        ArrayList<Pair<Integer ,String >> ideas = new ArrayList<>();
        Pair<Integer ,String > pair;

        //Scan the ideas and return everything
        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {
                String text = cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME_TEXT));
                int id = cursor.getInt(cursor.getColumnIndex(DataEntry._ID));
                pair = new Pair<>(id,text);
                ideas.add(pair);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return ideas;
    }
}
