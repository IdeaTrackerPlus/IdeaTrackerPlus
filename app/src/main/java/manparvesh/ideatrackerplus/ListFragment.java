package manparvesh.ideatrackerplus;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.woxthebox.draglistview.DragListView;

import manparvesh.ideatrackerplus.database.DataEntry;
import manparvesh.ideatrackerplus.database.DatabaseHelper;

/**
 * Fragment containing the listView to be displayed in each tab
 */
public class ListFragment extends Fragment {

    @Nullable
    IdeaActivityHost host;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static ListFragment newInstance(String tabName, boolean darkTheme) {
        ListFragment f = new ListFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("tabName", tabName);
        args.putBoolean("darkTheme", darkTheme);
        f.setArguments(args);

        return f;
    }

    public String getTabName() {
        return getArguments().getString("tabName");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() instanceof IdeaActivityHost)
            host = (IdeaActivityHost) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        boolean darkTheme = getArguments().getBoolean("darkTheme", false);

        // NO PROJECT
        if (DataEntry.TABLE_NAME.equals("[]")) {
            rootView = inflater.inflate(R.layout.no_project_layout, container, false);
            LinearLayout lin = (LinearLayout) rootView.findViewById(R.id.noProject);
            lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (host != null)
                        host.openProjectDialog();
                }
            });
            return rootView;
        }

        if (isSearchModeEnabled()) {
            rootView = inflater.inflate(R.layout.search_view, container, false);
            ListView list = (ListView) rootView.findViewById(R.id.search_list);

            SearchListAdapter adapter = SearchListAdapter.getInstance(getContext(), darkTheme);
            list.setAdapter(adapter);
            return rootView;
        }

        //Inflate the list view
        rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        DragListView mDragListView = (DragListView) rootView.findViewById(R.id.list);
        mDragListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Determine tab number
        int tabNumber = 0;

        if (getTabName().equals(getString(R.string.first_tab))) { //IDEAS
            tabNumber = 1;
        } else if (getTabName().equals(getString(R.string.second_tab))) {
            tabNumber = 2;
        } else if (getTabName().equals(getString(R.string.third_tab))) {
            tabNumber = 3;
        }

        //Set reorder listener
        final int finalTabNumber = tabNumber;
        mDragListView.setDragListListener(new DragListView.DragListListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemDragStarted(int position) {
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition != toPosition) {
                    DatabaseHelper.getInstance(getContext()).resetEntriesOrderAt(finalTabNumber);
                }
            }

            @Override
            public void onItemDragging(int itemPosition, float x, float y) {
            }
        });

        //Set adapter
        ItemAdapter itemAdapter = new ItemAdapter(getContext(), tabNumber, R.layout.recycler_view_item, R.id.horizontal_recycler_view, darkTheme);
        mDragListView.setAdapter(itemAdapter, false);
        mDragListView.setCanDragHorizontally(false);

        DatabaseHelper.setAdapterAtTab(tabNumber, itemAdapter);
        DatabaseHelper.notifyAllLists();

        return rootView;
    }

    public boolean isSearchModeEnabled() {
        return host != null && host.isSearchEnabled();
    }
}