package appbox.ideastracker.recycleview;

import android.app.Dialog;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.yarolegovich.lovelydialog.LovelyCustomDialog;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import appbox.ideastracker.R;
import appbox.ideastracker.database.DataEntry;
import appbox.ideastracker.database.DatabaseHelper;

/**
 * Created by Nicklos on 20/07/2016.
 */
public class RecyclerOnClickListener implements View.OnClickListener {

    private DatabaseHelper mDbHelper;
    private View mView;
    private int mIdRecycler;

    private static int mPrimaryColor;

    public RecyclerOnClickListener(int idRecycler) {
        mIdRecycler = idRecycler;
    }

    @Override
    public void onClick(View v) {
        mView = v;
        showIdeaDialog();

    }

    public static void setPrimaryColor(int color) {
        mPrimaryColor = color;
    }

    private void showIdeaDialog() {

        mDbHelper = DatabaseHelper.getInstance(mView.getContext());
        String text = mDbHelper.getTextById(mIdRecycler);
        String note = mDbHelper.getNoteById(mIdRecycler);

        new LovelyInfoDialog(mView.getContext())
                .setTopColor(mPrimaryColor)
                .setIcon(R.drawable.ic_bulb)
                .setTitle(text)
                .setMessage(note)
                .setConfirmButtonColor(mView.getContext().getResources().getColor(R.color.md_pink_a200))
                .show();

    }
}
