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

import appbox.ideastracker.R;
import appbox.ideastracker.database.DataEntry;
import appbox.ideastracker.database.DatabaseHelper;

/**
 * Created by Nicklos on 20/07/2016.
 */
public class RecyclerOnClickListener implements View.OnClickListener{

    private DatabaseHelper mDbHelper;
    private int mIdRecycler;
    private int mTabNumber;
    private View mView;
    private Dialog mEditIdeaDialog;
    private RadioGroup mRadioGroup;
    private EditText mIdeaField;
    private Switch mDoLater;

    private static int mPrimaryColor;

    public RecyclerOnClickListener(int tabNumber,int idRecycler){
        mIdRecycler = idRecycler;
        mTabNumber = tabNumber;
    }

    @Override
    public void onClick(View v) {
        mView = v;
        editIdeaDialog();

    }

    public static void setPrimaryColor(int color){
        mPrimaryColor = color;
    }

    private void editIdeaDialog(){

        mEditIdeaDialog = new LovelyCustomDialog(mView.getContext(),R.style.EditTextTintTheme)
                .setView(R.layout.edit_idea_form)
                .setTopColor(mPrimaryColor)
                .setTitle("Edit idea")
                .setIcon(R.drawable.ic_edit)
                .setListener(R.id.doneEditButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mRadioGroup.getCheckedRadioButtonId() != -1) {
                            View radioButton = mRadioGroup.findViewById(mRadioGroup.getCheckedRadioButtonId());
                            RadioButton btn = (RadioButton) mRadioGroup.getChildAt(mRadioGroup.indexOfChild(radioButton));
                            String selection = (String) btn.getText();

                            String new_text = mIdeaField.getText().toString();
                            boolean new_later = mDoLater.isChecked();
                            int new_priority = Integer.parseInt(selection.toString());

                            mDbHelper.editEntry(mIdRecycler,new_text,new_priority,new_later);
                        }

                        mEditIdeaDialog.dismiss();
                    }
                })
                .show();

        //Get the views
        mDoLater = (Switch) mEditIdeaDialog.findViewById(R.id.editDoLater);
        mRadioGroup = (RadioGroup) mEditIdeaDialog.findViewById(R.id.editRadioGroup);
        mIdeaField = (EditText) mEditIdeaDialog.findViewById(R.id.editIdeaText);

        //Get the values from the idea and set them
        mDbHelper = DatabaseHelper.getInstance(mView.getContext());
        mIdeaField.setText(mDbHelper.getTextById(mIdRecycler));
        if(mTabNumber == 2) mDoLater.toggle();

        RadioButton radio = null;
        int priority = mDbHelper.getPriorityById(mIdRecycler);
        switch (priority){
            case 1: radio = (RadioButton) mEditIdeaDialog.findViewById(R.id.editRadioButton1);
                break;
            case 2: radio = (RadioButton) mEditIdeaDialog.findViewById(R.id.editRadioButton2);
                break;
            case 3: radio = (RadioButton) mEditIdeaDialog.findViewById(R.id.editRadioButton3);
                break;
        }
        radio.setChecked(true);

    }
}
