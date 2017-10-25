package manparvesh.ideatrackerplus;

/*
 * Copyright 2014 Magnus Woxblom
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.woxthebox.draglistview.DragItemAdapter;

import manparvesh.ideatrackerplus.recycler.HorizontalAdapter;
import manparvesh.ideatrackerplus.recycler.MyRecyclerView;

public class ItemAdapter extends DragItemAdapter<Pair<Integer, String>, ItemAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;

    private Context mContext;
    private int mTabNumber;

    private boolean mDarkTheme;

    public ItemAdapter(Context context, int tabNumber, int layoutId, int grabHandleId, boolean darkTheme) {
        super(true);
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        setHasStableIds(true);

        mContext = context;
        mTabNumber = tabNumber;
        mDarkTheme = darkTheme;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);

        //create Recycler here
        MyRecyclerView recyclerView = (MyRecyclerView) layoutView.findViewById(R.id.horizontal_recycler_view);
        recyclerView.reboot(); //in case it's recycled

        // Create the right adapter for the recycler view
        HorizontalAdapter horizontalAdapter;
        horizontalAdapter = new HorizontalAdapter(recyclerView.getContext(), "", mTabNumber, mDarkTheme);

        // Set up the manager and adapter of the recycler view
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        horizontalLayoutManager.scrollToPositionWithOffset(1, 0);
        recyclerView.setTag(0);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(horizontalAdapter);
        recyclerView.setUp();

        return new ViewHolder(layoutView, recyclerView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        String text = mItemList.get(position).second;
        int id = mItemList.get(position).first;

        //change text and id of mRecyclerView
        MyRecyclerView recyclerView = holder.mRecyclerView;
        recyclerView.setTag(id);
        recyclerView.changeIdeaText(text);

    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    public class ViewHolder extends DragItemAdapter<Pair<Integer, String>, ItemAdapter.ViewHolder>.ViewHolder {
        public MyRecyclerView mRecyclerView;

        public ViewHolder(final View layoutView, MyRecyclerView recyclerView) {
            super(layoutView, mGrabHandleId);
            mRecyclerView = recyclerView;
        }

        @Override
        public void onItemClicked(View view) {
        }

        @Override
        public boolean onItemLongClicked(View view) {
            return true;
        }
    }
}