package appbox.ideastracker.recycler;

import android.view.View;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import appbox.ideastracker.MainActivity;
import appbox.ideastracker.R;
import appbox.ideastracker.database.DatabaseHelper;

/**
 * Created by Nicklos on 20/07/2016.
 * Listener for clicks on MyRecyclerView
 */
public class RecyclerOnClickListener implements View.OnClickListener {

    private View mTextView;
    private int mIdRecycler;
    private MyRecyclerView mRecyclerView;

    // Long click listener to trigger the edit idea action
    private RecyclerOnLongClickListener mOtherListener;

    //Color for the dialogs
    private static int mPrimaryColor;

    public RecyclerOnClickListener(MyRecyclerView recyclerView) {
        mIdRecycler = (Integer) recyclerView.getTag();
        mRecyclerView = recyclerView;
    }

    public void setOtherListener(RecyclerOnLongClickListener otherListener){
        mOtherListener = otherListener;
    }

    @Override
    public void onClick(View v) {
        mTextView = v;
        showIdeaDialog();
    }

    public static void setPrimaryColor(int color) {
        mPrimaryColor = color;
    }

    /**
     * Show a dialog with the idea's text and note
     * allows to delete or edit the idea
     */
    private void showIdeaDialog() {

        DatabaseHelper dBhelper = DatabaseHelper.getInstance(mTextView.getContext());
        String text = dBhelper.getTextById(mIdRecycler);
        String note = dBhelper.getNoteById(mIdRecycler);

        new LovelyStandardDialog(mTextView.getContext())
                .setTopColor(mPrimaryColor)
                .setIcon(R.drawable.ic_bulb)
                .setTitle(text)
                .setMessage(note)
                .setPositiveButtonColorRes(R.color.md_pink_a200)
                .setPositiveButton(R.string.ok, null)
                .setNeutralButton("DELETE", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRecyclerView.sendCellToDelete();

                        MainActivity mainActivity = MainActivity.getActivity(mTextView);
                        mainActivity.displayIdeasCount();
                    }
                })
                .setNeutralButtonColorRes(R.color.md_pink_a200)
                .setNegativeButton("EDIT", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherListener.editIdeaDialog(mTextView.getContext());
                    }
                })
                .setNegativeButtonColorRes(R.color.md_pink_a200)
                .show();

    }

}
