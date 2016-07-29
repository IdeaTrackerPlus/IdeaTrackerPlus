package appbox.ideastracker.recycleview;

import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyCustomDialog;

import appbox.ideastracker.R;
import appbox.ideastracker.database.DatabaseHelper;

/**
 * Created by Nicklos on 26/07/2016.
 * Listener for long clicks on MyRecyclerView
 */
public class RecyclerOnLongClickListener implements View.OnLongClickListener{

    private DatabaseHelper mDbHelper;

    //RecyclerView attrs
    private int mIdRecycler;
    private int mTabNumber;
    private View mView;

    private Dialog mEditIdeaDialog;

    //Dialog views
    private RadioGroup mRadioGroup;
    private EditText mIdeaField;
    private EditText mNoteField;
    private TextView mError;
    private Switch mDoLater;

    private static int mPrimaryColor;

    public RecyclerOnLongClickListener(int tabNumber,int idRecycler){
        mIdRecycler = idRecycler;
        mTabNumber = tabNumber;
    }

    @Override
    public boolean onLongClick(View v) {
        mView = v;
        editIdeaDialog();
        return true;
    }

    public static void setPrimaryColor(int color){
        mPrimaryColor = color;
    }

    /**
     * Show a dialog allwing to edit all the attributes
     * of the idea and showing the original ones.
     */
    private void editIdeaDialog(){

        mEditIdeaDialog = new LovelyCustomDialog(mView.getContext(), R.style.EditTextTintTheme)
                .setView(R.layout.edit_idea_form)
                .setTopColor(mPrimaryColor)
                .setTitle("Edit idea")
                .setIcon(R.drawable.ic_edit)
                .setListener(R.id.doneEditButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String new_text = mIdeaField.getText().toString();
                        if(!new_text.equals("")) {

                            if (mRadioGroup.getCheckedRadioButtonId() != -1) {
                                View radioButton = mRadioGroup.findViewById(mRadioGroup.getCheckedRadioButtonId());
                                RadioButton btn = (RadioButton) mRadioGroup.getChildAt(mRadioGroup.indexOfChild(radioButton));
                                String selection = (String) btn.getText();

                                String new_note = mNoteField.getText().toString();
                                boolean new_later = mDoLater.isChecked();
                                int new_priority = Integer.parseInt(selection.toString());

                                mDbHelper.editEntry(mIdRecycler, new_text, new_note, new_priority, new_later);
                            }

                            mEditIdeaDialog.dismiss();
                        }else{
                            mError.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .show();

        //Get the views
        mDoLater = (Switch) mEditIdeaDialog.findViewById(R.id.editDoLater);
        mRadioGroup = (RadioGroup) mEditIdeaDialog.findViewById(R.id.editRadioGroup);
        mIdeaField = (EditText) mEditIdeaDialog.findViewById(R.id.editIdeaText);
        mNoteField = (EditText) mEditIdeaDialog.findViewById(R.id.edit_Note);

        //set up the error message
        mError = (TextView) mEditIdeaDialog.findViewById(R.id.edit_error_message);
        mIdeaField.addTextChangedListener(new HideErrorOnTextChanged());

        //Get the values from the idea and set them
        mDbHelper = DatabaseHelper.getInstance(mView.getContext());
        mIdeaField.setText(mDbHelper.getTextById(mIdRecycler));
        mNoteField.setText(mDbHelper.getNoteById(mIdRecycler));
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

    private class HideErrorOnTextChanged implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mError.setVisibility(View.GONE);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
