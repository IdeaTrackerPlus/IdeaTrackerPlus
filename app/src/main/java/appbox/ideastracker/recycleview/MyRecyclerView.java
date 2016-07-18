package appbox.ideastracker.recycleview;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import appbox.ideastracker.database.DataEntry;
import appbox.ideastracker.database.DatabaseHelper;

/**
 * Created by Nicklos on 13/07/2016.
 */
public class MyRecyclerView extends RecyclerView {

    private boolean isActivated;
    private HorizontalAdapter mAdapter;
    private LinearLayoutManager mManager;


    public MyRecyclerView(Context context) {
        super(context);
        isActivated = false;
    }

    public MyRecyclerView(Context context,AttributeSet attrs) {
        super(context,attrs);
        isActivated = false;
    }

    public MyRecyclerView(Context context,AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        isActivated = false;
    }

    public void setUp(){
        mManager = (LinearLayoutManager) this.getLayoutManager();
        mAdapter = (HorizontalAdapter) this.getAdapter();
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityX *= 0.5;
        return super.fling(velocityX, velocityY);
    }

    @Override
    public void onScrollStateChanged(int state) {
        int tab = mAdapter.getTabNumber();

        switch (tab){
            case 1:
                stateChangedIdea(state);
                break;

            case 2:
                stateChangeOther(state);
                break;

            case 3:
                stateChangeOther(state);
                break;
        }

    }

    //Move an idea to LATER or DONE
    private void editEntry(boolean later, int id){
        DatabaseHelper helper = DatabaseHelper.getInstance(this.getContext());
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(later) values.put(DataEntry.COLUMN_NAME_LATER, true);
        else values.put(DataEntry.COLUMN_NAME_DONE, true);

        db.update(DataEntry.TABLE_NAME,values,"_id="+id,null);

        //notify on first tab to delete element
        Handler handler = new Handler();
        handler.postDelayed(myRunnable,300);
    }

    private void moveToNow(int id){
        DatabaseHelper helper = DatabaseHelper.getInstance(this.getContext());
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_NAME_DONE, false);
        values.put(DataEntry.COLUMN_NAME_LATER, false);

        db.update(DataEntry.TABLE_NAME,values,"_id="+id,null);

        //notify on first tab to delete element
        Handler handler = new Handler();
        handler.postDelayed(myRunnable,300);
    }

    private void deleteEntry(int id){
        DatabaseHelper helper = DatabaseHelper.getInstance(this.getContext());
        SQLiteDatabase db = helper.getWritableDatabase();

        db.delete(DataEntry.TABLE_NAME,"_id="+id,null);

        //notify
        Handler handler = new Handler();
        handler.postDelayed(myRunnable,300);
    }

    private Runnable myRunnable = new Runnable() {
        public void run() {
            DatabaseHelper.notifyAllLists();
        }
    };

    private void stateChangedIdea(int state){
        int width = mManager.getChildAt(0).getWidth();
        double limLeft =  0.4d*width;
        double limRight = 0.6d*width;

        if(state != RecyclerView.SCROLL_STATE_DRAGGING && !isActivated){

            View child;
            int first = mManager.findFirstVisibleItemPosition();
            int last = mManager.findLastVisibleItemPosition();
            int left, right;

            if((child = mManager.getChildAt(0)) != null && first == 0){//We are going towards LATER
                right = child.getRight();
                if(right > limLeft){
                    isActivated = true;
                    smoothScrollToPosition(0);
                }
                else smoothScrollToPosition(1);

            }else if((child = mManager.getChildAt(1)) != null && last == 2){//We are going towards DONE
                left = child.getLeft();
                if(left < limRight){
                    isActivated = true;
                    smoothScrollToPosition(2);
                }
                else smoothScrollToPosition(1);
            }
        }else if(isActivated && state == RecyclerView.SCROLL_STATE_IDLE){
            int first = mManager.findFirstVisibleItemPosition();
            if((mManager.getChildAt(0)) != null && first == 0){
                int tagId = (int) this.getTag();
                editEntry(true,tagId);
            }else{
                int tagId = (int) this.getTag();
                editEntry(false,tagId);
            }
        }
    }

    private void stateChangeOther(int state){
        int width = mManager.getChildAt(0).getWidth();
        double limLeft =  0.4d*width;
        double limRight = 0.6d*width;

        if(state != RecyclerView.SCROLL_STATE_DRAGGING && !isActivated){

            View child;
            int first = mManager.findFirstVisibleItemPosition();
            int last = mManager.findLastVisibleItemPosition();
            int left, right;

            if((child = mManager.getChildAt(0)) != null && first == 0){//We are going towards DELETE
                right = child.getRight();
                if(right > limLeft){
                    isActivated = true;
                    smoothScrollToPosition(0);
                }
                else smoothScrollToPosition(1);

            }else if((child = mManager.getChildAt(1)) != null && last == 2){//We are going towards NOW
                left = child.getLeft();
                if(left < limRight){
                    isActivated = true;
                    smoothScrollToPosition(2);
                }
                else smoothScrollToPosition(1);
            }
        }else if(isActivated && state == RecyclerView.SCROLL_STATE_IDLE){ //Wait for animation to finish
            int first = mManager.findFirstVisibleItemPosition();
            if((mManager.getChildAt(0)) != null && first == 0){ //DELETE
                int tagId = (int) this.getTag();
                deleteEntry(tagId);
            }else{ //NOW
                int tagId = (int) this.getTag();
                moveToNow(tagId);
            }
        }
    }

}

