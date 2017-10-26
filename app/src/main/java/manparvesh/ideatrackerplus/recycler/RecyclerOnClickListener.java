package manparvesh.ideatrackerplus.recycler;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyCustomDialog;

import java.util.Locale;

import manparvesh.ideatrackerplus.MainActivity;
import manparvesh.ideatrackerplus.R;
import manparvesh.ideatrackerplus.database.DatabaseHelper;
import manparvesh.ideatrackerplus.database.TinyDB;

/**
 * Created by Nicklos on 20/07/2016.
 * Listener for clicks on MyRecyclerView
 */
public class RecyclerOnClickListener implements View.OnClickListener, View.OnFocusChangeListener {

    private Context context;
    private DatabaseHelper mDbHelper;
    private MyRecyclerView mRecyclerView;
    private boolean mDarkTheme;

    //RecyclerView attrs
    private int mIdRecycler;
    private int mTabNumber;
    private int mPriority;

    private Dialog mDetailedIdeaDialog;
    private Dialog mEditIdeaDialog;

    //Dialog views
    private RadioGroup mRadioGroup;
    private TextView mIdeaField;
    private TextView mNoteField;
    private TextView mError;
    private Switch mDoLater;

    //Color for the dialogs
    private static int mPrimaryColor;
    private static int mSecondaryColor;

    public RecyclerOnClickListener(MyRecyclerView recyclerView, int tabNumber) {
        this.context = recyclerView.getContext();
        mRecyclerView = recyclerView;
        mTabNumber = tabNumber;

        mDbHelper = DatabaseHelper.getInstance(context);
        TinyDB mTinyDB = new TinyDB(context);
        mDarkTheme = mTinyDB.getBoolean(context.getString(R.string.dark_theme_pref), false);
    }

    @Override
    public void onClick(View v) {
        mIdRecycler = (int) mRecyclerView.getTag();
        mPriority = mDbHelper.getPriorityById(mIdRecycler);
        showIdeaDialog();
    }

    public static void setPrimaryColor(int color) {
        mPrimaryColor = color;
    }

    public static void setSecondaryColor(int color) {
        mSecondaryColor = color;
    }

    /**
     * Show a dialog with the idea's text and note
     * allows to delete or edit the idea
     */
    private void showIdeaDialog() {
        mDetailedIdeaDialog = new LovelyCustomDialog(context, mDarkTheme ? R.style.EditTextTintThemeDark : R.style.EditTextTintTheme)
                .setView(R.layout.detailed_idea_form)
                .setTopColor(mPrimaryColor)
                .setIcon(R.drawable.ic_bulb)
                .setListener(R.id.editButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editIdeaDialog();
                        mDetailedIdeaDialog.dismiss();
                    }
                })
                .setListener(R.id.deleteButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mTabNumber == 4) {//Search tab
                            mRecyclerView.muteCellToDelete();

                        } else { //Other tabs
                            mRecyclerView.sendCellToTab(-1);
                        }
                        mDetailedIdeaDialog.dismiss();
                    }
                })
                .setListener(R.id.okButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDetailedIdeaDialog.dismiss();
                    }
                })
                .configureView(new LovelyCustomDialog.ViewConfigurator() {
                    @Override
                    public void configureView(View v) {
                        mIdeaField = (TextView) v.findViewById(R.id.editText);
                        mNoteField = (TextView) v.findViewById(R.id.editNote);
                        mIdeaField.append(mDbHelper.getTextById(mIdRecycler));
                        mNoteField.setText(mDbHelper.getNoteById(mIdRecycler));

                        AppCompatRadioButton radio = (AppCompatRadioButton) v.findViewById(R.id.priorityRadioButton);
                        radio.setText(String.format(Locale.getDefault(), "%d", mPriority));
                        radio.setHighlightColor(getPriorityColor());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            radio.setButtonTintList(ColorStateList.valueOf(getPriorityColor()));
                        }
                    }
                })
                .show();
    }

    @ColorInt
    private int getPriorityColor() {
        return ContextCompat.getColor(MainActivity.getInstance(), getPriorityColorResource());
    }

    @ColorRes
    private int getPriorityColorResource() {
        switch (mPriority) {
            case 1:
                return R.color.priority1;

            case 2:
                return R.color.priority2;

            case 3:
                return R.color.priority3;

        }

        return R.color.white;
    }

    /**
     * Show a dialog allowing to edit all the attributes
     * of the idea and showing the original ones.
     */
    public void editIdeaDialog() {
        mEditIdeaDialog = new LovelyCustomDialog(context, mDarkTheme ? R.style.EditTextTintThemeDark : R.style.EditTextTintTheme)
                .setView(R.layout.new_idea_form)
                .setTopColor(mPrimaryColor)
                .setIcon(R.drawable.ic_edit)
                .setListener(R.id.doneButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendEditIdea();
                    }
                })
                .configureView(new LovelyCustomDialog.ViewConfigurator() {
                    @Override
                    public void configureView(View v) {

                        //Get the views
                        TextView title = (TextView) v.findViewById(R.id.idea_dialog_title);
                        Button doneButton = (Button) v.findViewById(R.id.doneButton);
                        mDoLater = (Switch) v.findViewById(R.id.doLater);
                        mRadioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
                        mIdeaField = (EditText) v.findViewById(R.id.editText);
                        mNoteField = (EditText) v.findViewById(R.id.editNote);

                        //change some strings from new to edit
                        title.setText(R.string.edit_idea);
                        doneButton.setText(R.string.edit);


                        //set up the error message
                        mError = (TextView) v.findViewById(R.id.new_error_message);
                        mIdeaField.addTextChangedListener(new HideErrorOnTextChanged());

                        //set up the "ENTER" listeners
                        mIdeaField.setOnEditorActionListener(ideaFieldListener);
                        mNoteField.setOnEditorActionListener(noteFieldListener);

                        mIdeaField.setOnFocusChangeListener(RecyclerOnClickListener.this);
                        mNoteField.setOnFocusChangeListener(RecyclerOnClickListener.this);

                        //Get the values from the idea and set them
                        String ideaText = mDbHelper.getTextById(mIdRecycler);
                        mIdeaField.append(ideaText);
                        mNoteField.setText(mDbHelper.getNoteById(mIdRecycler));
                        int fromTab = mDbHelper.getTabById(mIdRecycler); //Give the tab from where the idea belong
                        if (fromTab == 2) mDoLater.toggle();

                        RadioButton radio = null;
                        switch (mPriority) {
                            case 1:
                                radio = (RadioButton) v.findViewById(R.id.radioButton1);
                                break;
                            case 2:
                                radio = (RadioButton) v.findViewById(R.id.radioButton2);
                                break;
                            case 3:
                                radio = (RadioButton) v.findViewById(R.id.radioButton3);
                                break;
                        }
                        radio.setChecked(true);
                    }
                })
                .show();

        if (mNoteField.requestFocus() && mIdeaField.requestFocus()) {
            mEditIdeaDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (view instanceof EditText) {
            EditText editText = (EditText) view;
            if (hasFocus) {
                editText.getBackground().setColorFilter(mSecondaryColor, PorterDuff.Mode.SRC_IN);
            } else {
                editText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            }
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
            if (actionId == EditorInfo.IME_ACTION_GO
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_NEXT
                    || actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_NULL) {
                mNoteField.requestFocus();
            }
            return true;
        }
    };

    private TextView.OnEditorActionListener noteFieldListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_GO
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_NEXT
                    || actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_NULL) {
                sendEditIdea();
            }
            return true;
        }
    };
}
