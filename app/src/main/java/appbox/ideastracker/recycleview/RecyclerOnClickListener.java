package appbox.ideastracker.recycleview;

import android.view.View;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import appbox.ideastracker.MainActivity;
import appbox.ideastracker.R;
import appbox.ideastracker.database.DatabaseHelper;

/**
 * Created by Nicklos on 20/07/2016.
 */
public class RecyclerOnClickListener implements View.OnClickListener {

    private DatabaseHelper mDbHelper;
    private View mTextView;
    private int mIdRecycler;
    private MyRecyclerView mRecyclerView;

    private static int mPrimaryColor;

    public RecyclerOnClickListener(MyRecyclerView recyclerView) {
        mIdRecycler = (Integer) recyclerView.getTag();
        mRecyclerView = recyclerView;

    }

    @Override
    public void onClick(View v) {
        mTextView = v;
        showIdeaDialog();

    }

    public static void setPrimaryColor(int color) {
        mPrimaryColor = color;
    }

    private void showIdeaDialog() {

        mDbHelper = DatabaseHelper.getInstance(mTextView.getContext());
        String text = mDbHelper.getTextById(mIdRecycler);
        String note = mDbHelper.getNoteById(mIdRecycler);

        new LovelyStandardDialog(mTextView.getContext())
                .setTopColor(mPrimaryColor)
                .setIcon(R.drawable.ic_bulb)
                .setTitle(text)
                .setMessage(note)
                .setPositiveButtonColorRes(R.color.md_pink_a200)
                .setPositiveButton(R.string.ok,null)
                .setNeutralButton("DELETE", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRecyclerView.sendCellToDelete();

                        MainActivity mainActivity = MainActivity.getActivity(mTextView);
                        mainActivity.displayIdeasCount();
                    }
                })
                .setNeutralButtonColorRes(R.color.md_pink_a200)
                .show();

    }

}
