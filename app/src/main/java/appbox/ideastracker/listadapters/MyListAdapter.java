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
        return mDbHelper.readIdeas(mLater).size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if(view == null){
            view = inflater.inflate(R.layout.child_layout, parent,false);
        }

        MyRecyclerView horizontal_recycler_view = (MyRecyclerView) view.findViewById(R.id.horizontal_recycler_view);
        horizontal_recycler_view.reboot(); //in case it's recycled

        ArrayList<Pair<Integer ,String >> ideas = mDbHelper.readIdeas(mLater);
        Pair<Integer ,String > pair = ideas.get(position);
        HorizontalAdapter horizontalAdapter;
        if(mLater) horizontalAdapter = new HorizontalAdapter(pair.second,2);
        else horizontalAdapter = new HorizontalAdapter(pair.second,3);
        horizontal_recycler_view.setTag(pair.first);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(inflater.getContext(),LinearLayoutManager.HORIZONTAL,false);
        horizontalLayoutManager.scrollToPositionWithOffset(1, 0);
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
