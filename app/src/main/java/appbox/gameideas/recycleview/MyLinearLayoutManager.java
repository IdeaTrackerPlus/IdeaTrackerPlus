package appbox.gameideas.recycleview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by Nicklos on 13/07/2016.
 */
public class MyLinearLayoutManager extends LinearLayoutManager {

    private Context mContext;

    public MyLinearLayoutManager(Context context) {
        super(context,LinearLayoutManager.HORIZONTAL, false);
        mContext = context;
    }

}
