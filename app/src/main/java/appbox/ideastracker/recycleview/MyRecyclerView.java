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

    private boolean isActivated = false;

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context,AttributeSet attrs) {
        super(context,attrs);
    }

    public MyRecyclerView(Context context,AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
    }


    @Override
    public boolean fling(int velocityX, int velocityY) {

        //velocityY *= 0.7;
         velocityX *= 0.5;

        return super.fling(velocityX, velocityY);
    }

    @Override
    public void onScrollStateChanged(int state) {
        LinearLayoutManager manager = (LinearLayoutManager) this.getLayoutManager();
        int width = manager.getChildAt(0).getWidth();
        double limLeft =  0.4d*width;
        double limRight = 0.6d*width;


        if(state != RecyclerView.SCROLL_STATE_DRAGGING && !isActivated){

            View child;
            int first = manager.findFirstVisibleItemPosition();
            int last = manager.findLastVisibleItemPosition();
            int left, right;

            if((child = manager.getChildAt(0)) != null && first == 0){//We are going towards LATER
                right = child.getRight();
                if(right > limLeft){
                    isActivated = true;
                    smoothScrollToPosition(0);
                }
                else smoothScrollToPosition(1);

            }else if((child = manager.getChildAt(1)) != null && last == 2){//We are going towards DONE
                left = child.getLeft();
                if(left < limRight){
                    isActivated = true;
                    smoothScrollToPosition(2);
                }
                else smoothScrollToPosition(1);
            }
        }else if(isActivated && state == RecyclerView.SCROLL_STATE_IDLE){
            int first = manager.findFirstVisibleItemPosition();
            if((manager.getChildAt(0)) != null && first == 0){
                int tagId = (int) this.getTag();
                editEntry(true,tagId);
            }else{
                int tagId = (int) this.getTag();
                editEntry(false,tagId);
            }
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
        handler.postDelayed(myRunnable,500);
    }

    private Runnable myRunnable = new Runnable() {
        public void run() {
            DatabaseHelper.notifyAllLists();
        }
    };

}

