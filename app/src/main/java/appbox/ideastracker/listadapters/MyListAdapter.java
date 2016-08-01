package appbox.ideastracker.listadapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import appbox.ideastracker.R;
import appbox.ideastracker.database.DatabaseHelper;
import appbox.ideastracker.recycler.HorizontalAdapter;
import appbox.ideastracker.recycler.MyRecyclerView;

/**
 * Created by Nicklos on 13/07/2016.
 * Adapter for the listView of the "Later" and "Done" tabs
 */
public class MyListAdapter extends BaseAdapter {

    private boolean mLater; //true for "Later", false for "Done"
    private LayoutInflater inflater;
    private DatabaseHelper mDbHelper;

    public MyListAdapter(Context context, boolean later){

        this.inflater = LayoutInflater.from(context);
        mDbHelper = DatabaseHelper.getInstance(context);
        mLater = later;
    }

    @Override
    public int getCount() {
        return mDbHelper.readIdeas(mLater).size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        //Recycle made complicated with big text option
        if (view == null) {
            view = inflater.inflate(R.layout.child_layout, parent, false);
        }

        MyRecyclerView horizontal_recycler_view = (MyRecyclerView) view.findViewById(R.id.horizontal_recycler_view);
        horizontal_recycler_view.reboot(); //in case it's recycled

        // Get the text and id of the idea
        ArrayList<Pair<Integer ,String >> ideas = mDbHelper.readIdeas(mLater);
        Pair<Integer ,String > pair = ideas.get(position);

        // Create the right adapter for the recycler view
        HorizontalAdapter horizontalAdapter;
        if(mLater) horizontalAdapter = new HorizontalAdapter(pair.second,2);
        else horizontalAdapter = new HorizontalAdapter(pair.second,3);

        // Set up the manager and adapter of the recycler view
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(inflater.getContext(),LinearLayoutManager.HORIZONTAL,false);
        horizontalLayoutManager.scrollToPositionWithOffset(1, 0);
        horizontal_recycler_view.setTag(pair.first);
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManager);
        horizontal_recycler_view.setAdapter(horizontalAdapter);
        horizontal_recycler_view.setUp();

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

}
