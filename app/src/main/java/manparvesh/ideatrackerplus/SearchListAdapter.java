package manparvesh.ideatrackerplus;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import manparvesh.ideatrackerplus.database.DatabaseHelper;
import manparvesh.ideatrackerplus.recycler.HorizontalAdapter;
import manparvesh.ideatrackerplus.recycler.MyRecyclerView;

/**
 * Adapter for the search list
 */
public class SearchListAdapter extends BaseAdapter {

    //singleton instance
    private static SearchListAdapter sInstance;

    private DatabaseHelper mDbHelper;
    private static String subString;

    private boolean mDarkTheme;

    public static synchronized SearchListAdapter getInstance(Context context, boolean darkTheme) {

        if (sInstance == null) {
            sInstance = new SearchListAdapter(context.getApplicationContext(), darkTheme);
        }
        return sInstance;
    }

    public SearchListAdapter(Context context, boolean darkTheme) {
        mDbHelper = DatabaseHelper.getInstance(context);

        subString = "";
        this.mDarkTheme = darkTheme;
    }

    public static void changeSearch(String newSearch) {
        subString = newSearch;

        if (sInstance != null) {
            sInstance.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mDbHelper.searchIdeas(subString).size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        //Recycle made complicated with big text option
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.recycler_view_item, parent, false);
        }

        MyRecyclerView horizontal_recycler_view = (MyRecyclerView) view.findViewById(R.id.horizontal_recycler_view);
        horizontal_recycler_view.reboot(); //in case it's recycled

        // Get the text and id of the idea
        ArrayList<Pair<Integer, String>> ideas = mDbHelper.searchIdeas(subString);
        Pair<Integer, String> pair = ideas.get(position);

        // Create the right adapter for the recycler view
        HorizontalAdapter horizontalAdapter;
        horizontalAdapter = new HorizontalAdapter(horizontal_recycler_view.getContext(), pair.second, 4, mDarkTheme);

        // Set up the manager and adapter of the recycler view
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(horizontal_recycler_view.getContext(), LinearLayoutManager.HORIZONTAL, false);
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