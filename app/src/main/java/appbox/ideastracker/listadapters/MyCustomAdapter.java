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
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import appbox.ideastracker.AnimatedExpandableListView;
import appbox.ideastracker.recycleview.HorizontalAdapter;
import appbox.ideastracker.recycleview.MyRecyclerView;
import appbox.ideastracker.R;
import appbox.ideastracker.database.DataEntry;
import appbox.ideastracker.database.DatabaseHelper;

/**
 * Created by Nicklos on 30/06/2016.
 * Adapter for the expandable list of the "Ideas" tab
 */
public class MyCustomAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {


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
    public int getRealChildrenCount(int i) {
        return mDbHelper.readIdeas(i).size();
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
            view = inflater.inflate(R.layout.parent_layout, viewGroup, false);
        }

        TextView textView = (TextView) view.findViewById(R.id.textViewParent);
        textView.setText("Priority " + Integer.toString(groupPosition + 1));
        LinearLayout parent = (LinearLayout) view.findViewById(R.id.parentPriority);
        switch (groupPosition) {
            case 0:
                parent.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.priority1));
                break;
            case 1:
                parent.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.priority2));
                break;
            case 2:
                parent.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.priority3));
                break;
        }


        //return the entire view
        return view;
    }

    @Override
    //in this method you must set the text to see the children on the list
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {

        //Recycle made complicated with big text option
        view = inflater.inflate(R.layout.child_layout, viewGroup, false);

        MyRecyclerView horizontal_recycler_view = (MyRecyclerView) view.findViewById(R.id.horizontal_recycler_view);
        horizontal_recycler_view.reboot(); //in case it's recycled
        ArrayList<Pair<Integer, String>> ideas = mDbHelper.readIdeas(groupPosition); //get all ideas from priority
        Pair<Integer, String> pair = ideas.get(childPosition);
        HorizontalAdapter horizontalAdapter = new HorizontalAdapter(pair.second, 1);
        horizontal_recycler_view.setTag(pair.first);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontalLayoutManager.scrollToPositionWithOffset(1, 0);
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManager);
        horizontal_recycler_view.setAdapter(horizontalAdapter);
        horizontal_recycler_view.setUp();

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
}
