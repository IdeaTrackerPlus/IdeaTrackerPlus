package appbox.ideastracker.recycleview;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

import appbox.ideastracker.MainActivity;
import appbox.ideastracker.R;
import appbox.ideastracker.database.DataEntry;
import appbox.ideastracker.database.DatabaseHelper;

/**
 * Created by Nicklos on 13/07/2016.
 */
public class MyRecyclerView extends RecyclerView {

    private boolean isActivated;
    private HorizontalAdapter mAdapter;
    private LinearLayoutManager mManager;
    private DatabaseHelper mDbHelper;

    private static MainActivity mainActivity;


    public MyRecyclerView(Context context) {
        super(context);
        isActivated = false;
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        isActivated = false;
    }

    public MyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        isActivated = false;
    }

    public static void setMainActivity(MainActivity act){
        mainActivity = act;
    }

    public void setUp() {
        mManager = (LinearLayoutManager) this.getLayoutManager();
        mAdapter = (HorizontalAdapter) this.getAdapter();
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityX *= 0.8;
        return super.fling(velocityX, velocityY);
    }

    @Override
    public void onScrollStateChanged(int state) {
        int tab = mAdapter.getTabNumber();

        switch (tab) {
            case 1: //Tab NOW
                stateChangedIdea(state);
                break;

            case 2: //Tab LATER
                stateChangeOther(state);
                break;

            case 3: //Tab DONE
                stateChangeOther(state);
                break;
        }

    }

    private Runnable myRunnable = new Runnable() {
        public void run() {
            DatabaseHelper.notifyAllLists();
        }
    };

    private void notifyChange() {
        Handler handler = new Handler();
        handler.postDelayed(myRunnable, 300);
    }

    private void stateChangedIdea(int state) {

        mDbHelper = DatabaseHelper.getInstance(getContext());
        int width = mManager.getChildAt(0).getWidth();
        double limLeft = 0.4d * width;
        double limRight = 0.6d * width;

        if (state != RecyclerView.SCROLL_STATE_DRAGGING && !isActivated) {

            View child;
            int first = mManager.findFirstVisibleItemPosition();
            int last = mManager.findLastVisibleItemPosition();
            int left, right;

            if ((child = mManager.getChildAt(0)) != null && first == 0) {//We are going towards LATER
                right = child.getRight();
                if (right > limLeft) {
                    isActivated = true;
                    smoothScrollToPosition(0);
                } else smoothScrollToPosition(1);

            } else if ((child = mManager.getChildAt(1)) != null && last == 2) {//We are going towards DONE
                left = child.getLeft();
                if (left < limRight) {
                    isActivated = true;
                    smoothScrollToPosition(2);
                } else smoothScrollToPosition(1);
            }
        } else if (isActivated && state == RecyclerView.SCROLL_STATE_IDLE) {
            int first = mManager.findFirstVisibleItemPosition();
            if ((mManager.getChildAt(0)) != null && first == 0) { //move to LATER
                int tagId = (int) this.getTag();
                mDbHelper.moveToTab(2, tagId);
                notifyChange();
            } else { //move to DONE
                int tagId = (int) this.getTag();
                cheerSnackmessage();
                mDbHelper.moveToTab(3, tagId);
                notifyChange();
                mainActivity.displayIdeasCount();
            }
        }
    }

    private void stateChangeOther(int state) {

        mDbHelper = DatabaseHelper.getInstance(getContext());
        int width = mManager.getChildAt(0).getWidth();
        double limLeft = 0.4d * width;
        double limRight = 0.6d * width;

        if (state != RecyclerView.SCROLL_STATE_DRAGGING && !isActivated) {

            View child;
            int first = mManager.findFirstVisibleItemPosition();
            int last = mManager.findLastVisibleItemPosition();
            int left, right;

            if ((child = mManager.getChildAt(0)) != null && first == 0) {//We are going towards DELETE
                right = child.getRight();
                if (right > limLeft) {
                    isActivated = true;
                    smoothScrollToPosition(0);
                } else smoothScrollToPosition(1);

            } else if ((child = mManager.getChildAt(1)) != null && last == 2) {//We are going towards NOW
                left = child.getLeft();
                if (left < limRight) {
                    isActivated = true;
                    smoothScrollToPosition(2);
                } else smoothScrollToPosition(1);
            }
        } else if (isActivated && state == RecyclerView.SCROLL_STATE_IDLE) { //Wait for animation to finish
            int first = mManager.findFirstVisibleItemPosition();
            if ((mManager.getChildAt(0)) != null && first == 0) { //DELETE
                int tagId = (int) this.getTag();
                mDbHelper.deleteEntryWithSnack(this,tagId);
                notifyChange();
                mainActivity.displayIdeasCount();
            } else { //NOW
                int tagId = (int) this.getTag();
                mDbHelper.moveToTab(1, tagId);
                notifyChange();
                mainActivity.displayIdeasCount();
            }
        }
    }

    private void cheerSnackmessage(){
        String[] array = getContext().getResources().getStringArray(R.array.done_cheers);
        String randomStr = array[new Random().nextInt(array.length)];
        Snackbar.make(mainActivity.findViewById(R.id.main_content),randomStr,Snackbar.LENGTH_LONG).show();
    }

}

