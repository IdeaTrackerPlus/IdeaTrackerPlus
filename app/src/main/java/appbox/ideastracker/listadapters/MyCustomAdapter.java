package appbox.ideastracker.listadapters;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import appbox.ideastracker.recycleview.HorizontalAdapter;
import appbox.ideastracker.recycleview.MyRecyclerView;
import appbox.ideastracker.R;
import appbox.ideastracker.database.DataEntry;
import appbox.ideastracker.database.DatabaseHelper;

/**
 * Created by Nicklos on 30/06/2016.
 * Adapter for the expandable list of the "Ideas" tab
 */
public class MyCustomAdapter extends BaseExpandableListAdapter {


    private LayoutInflater inflater;
    private DatabaseHelper mDbHelper;


    public MyCustomAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        mDbHelper = DatabaseHelper.getInstance(context);
    }


    @Override
    //counts the number of group/parent items so the list knows how many times calls getGroupView() method
    public int getGroupCount() {
        return 3;
    }

    @Override
    //counts the number of children items so the list knows how many times calls getChildView() method
    public int getChildrenCount(int i){
        return readIdeas(i).size();
    }

    @Override
    //gets the title of each parent/group
    public Object getGroup(int i) {
        return i;
    }

    @Override
    //gets the name of each item
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    //in this method you must set the text to see the parent/group on the list
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = inflater.inflate(R.layout.parent_layout, viewGroup,false);
        }

        TextView textView = (TextView) view.findViewById(R.id.textViewParent);
        textView.setText("Priority : " + Integer.toString(groupPosition+1));


        //return the entire view
        return view;
    }

    @Override
    //in this method you must set the text to see the children on the list
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {

        ViewHolder holder = new ViewHolder();
        holder.id = childPosition;
        holder.priority = groupPosition;
        MyRecyclerView horizontal_recycler_view;

        if (view == null) {
            view = inflater.inflate(R.layout.child_layout, viewGroup,false);
            horizontal_recycler_view = (MyRecyclerView) view.findViewById(R.id.horizontal_recycler_view);
            ArrayList<Pair<Integer ,String >> ideas = readIdeas(groupPosition); //get all ideas from priority
            Pair<Integer ,String > pair = ideas.get(childPosition);
            HorizontalAdapter horizontalAdapter = new HorizontalAdapter(pair.second);
            horizontal_recycler_view.setTag(pair.first);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(inflater.getContext(),LinearLayoutManager.HORIZONTAL,false);
            horizontalLayoutManager.scrollToPositionWithOffset(1, 0);
            horizontal_recycler_view.setLayoutManager(horizontalLayoutManager);
            horizontal_recycler_view.setAdapter(horizontalAdapter);

        }

        //Edit text of adapter and notify changes
        horizontal_recycler_view = (MyRecyclerView) view.findViewById(R.id.horizontal_recycler_view);
        ArrayList<Pair<Integer ,String >> ideas = readIdeas(groupPosition); //get all ideas from priority
        Pair<Integer ,String > pair = ideas.get(childPosition);
        HorizontalAdapter horizontalAdapter = (HorizontalAdapter) horizontal_recycler_view.getAdapter();
        horizontalAdapter.editText(pair.second);
        horizontalAdapter.notifyDataSetChanged();
        horizontal_recycler_view.setTag(pair.first);

        //return the entire view
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        /* used to make the notifyDataSetChanged() method work */
        super.registerDataSetObserver(observer);
    }

    /**
     * Read all ideas of the "Ideas" tab in the database
     */
    private ArrayList<Pair<Integer ,String >> readIdeas(int priority) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Only the text and priority will be read
        String[] projection = {DataEntry._ID, DataEntry.COLUMN_NAME_TEXT, DataEntry.COLUMN_NAME_PRIORITY};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DataEntry._ID + " ASC";

        Cursor cursor = db.query(
                DataEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                "later=? and done=?",                    // The columns for the WHERE clause
                new String[] {"0", "0"},                  // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        ArrayList<Pair<Integer ,String >> ideas = new ArrayList<>();
        Pair<Integer ,String > pair;

        //Scan the ideas and return only the one with the expected priority
        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {
                String text = cursor.getString(cursor.getColumnIndex(DataEntry.COLUMN_NAME_TEXT));
                int id = cursor.getInt(cursor.getColumnIndex(DataEntry._ID));
                int prio = cursor.getInt(cursor.getColumnIndex(DataEntry.COLUMN_NAME_PRIORITY));
                if(prio == priority + 1){
                    pair = new Pair<>(id,text);
                    ideas.add(pair);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        return ideas;
    }


    protected class ViewHolder {
        protected int id;
        protected int priority;
    }
}
