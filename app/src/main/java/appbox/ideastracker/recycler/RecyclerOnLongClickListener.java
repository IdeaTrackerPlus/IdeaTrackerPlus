package appbox.ideastracker.recycler;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
        editIdeaDialog(v.getContext());
        return true;
    }

    public static void setPrimaryColor(int color){
        mPrimaryColor = color;
    }

    /**
     * Show a dialog allwing to edit all the attributes
     * of the idea and showing the original ones.
     */
    public void editIdeaDialog(Context context){

        mEditIdeaDialog = new LovelyCustomDialog(context, R.style.EditTextTintTheme)
                .setView(R.layout.new_idea_form)
                .setTopColor(mPrimaryColor)
                .setIcon(R.drawable.ic_edit)
                .setListener(R.id.doneButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendEditIdea();
                    }
                })
                .show();

        //Get the views
        TextView title = (TextView) mEditIdeaDialog.findViewById(R.id.idea_dialog_title);
        Button doneButton = (Button) mEditIdeaDialog.findViewById(R.id.doneButton);
        mDoLater = (Switch) mEditIdeaDialog.findViewById(R.id.doLater);
        mRadioGroup = (RadioGroup) mEditIdeaDialog.findViewById(R.id.radioGroup);
        mIdeaField = (EditText) mEditIdeaDialog.findViewById(R.id.editText);
        mNoteField = (EditText) mEditIdeaDialog.findViewById(R.id.editNote);

        //change some strings from new to edit
        title.setText(R.string.edit_idea);
        doneButton.setText(R.string.edit);


        //set up the error message
        mError = (TextView) mEditIdeaDialog.findViewById(R.id.new_error_message);
        mIdeaField.addTextChangedListener(new HideErrorOnTextChanged());

        //set up the "ENTER" listeners
        mIdeaField.setOnEditorActionListener(ideaFieldListener);
        mNoteField.setOnEditorActionListener(noteFieldListener);

        //Get the values from the idea and set them
        mDbHelper = DatabaseHelper.getInstance(context);
        mIdeaField.setText(mDbHelper.getTextById(mIdRecycler));
        mNoteField.setText(mDbHelper.getNoteById(mIdRecycler));
        if(mTabNumber == 2) mDoLater.toggle();

        RadioButton radio = null;
        int priority = mDbHelper.getPriorityById(mIdRecycler);
        switch (priority){
            case 1:
                radio = (RadioButton) mEditIdeaDialog.findViewById(R.id.radioButton1);
                break;
            case 2:
                radio = (RadioButton) mEditIdeaDialog.findViewById(R.id.radioButton2);
                break;
            case 3:
                radio = (RadioButton) mEditIdeaDialog.findViewById(R.id.radioButton3);
                break;
        }
        radio.setChecked(true);

    }

    private void sendEditIdea() {

        String new_text = mIdeaField.getText().toString();
        if (!new_text.equals("")) {

            if (mRadioGroup.getCheckedRadioButtonId() != -1) {
                View radioButton = mRadioGroup.findViewById(mRadioGroup.getCheckedRadioButtonId());
                RadioButton btn = (RadioButton) mRadioGroup.getChildAt(mRadioGroup.indexOfChild(radioButton));
                String selection = (String) btn.getText();

                String new_note = mNoteField.getText().toString();
                boolean new_later = mDoLater.isChecked();
                int new_priority = Integer.parseInt(selection);

                mDbHelper.editEntry(mIdRecycler, new_text, new_note, new_priority, new_later);
            }

            mEditIdeaDialog.dismiss();
        } else {
            mError.setVisibility(View.VISIBLE);
        }
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

    // EDIT TEXT LISTENERS //

    private TextView.OnEditorActionListener ideaFieldListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
                mNoteField.requestFocus();
            }
            return true;
        }
    };

    private TextView.OnEditorActionListener noteFieldListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
                sendEditIdea();
            }
            return true;
        }
    };

}
