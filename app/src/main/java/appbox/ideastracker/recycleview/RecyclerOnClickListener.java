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
import android.widget.TextView;
import android.widget.ToggleButton;

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

    public RecyclerOnClickListener(int tabNumber,int idRecycler){
        mIdRecycler = idRecycler;
        mTabNumber = tabNumber;
    }

    @Override
    public void onClick(View v) {
        mView = v;
        editIdeaDialog();

    }

    private void editIdeaDialog() {

        //Initialize the Dialog
        final Dialog myDialog = new Dialog(mView.getContext());
        myDialog.setContentView(R.layout.edit_idea_form);
        myDialog.setCancelable(false);

        //Get the views and buttons
        Button done = (Button) myDialog.findViewById(R.id.doneEditButton);
        Button cancel = (Button) myDialog.findViewById(R.id.cancelEditButton);
        final ToggleButton doLater = (ToggleButton) myDialog.findViewById(R.id.editDoLater);
        final RadioGroup radioGroup = (RadioGroup) myDialog.findViewById(R.id.editRadioGroup);
        final EditText ideaField = (EditText) myDialog.findViewById(R.id.editIdeaText);

        //Get the values from the idea and set them
        mDbHelper = DatabaseHelper.getInstance(mView.getContext());
        ideaField.setText(mDbHelper.getTextById(mIdRecycler));
        if(mTabNumber == 2) doLater.toggle();

        RadioButton radio = null;
        int priority = mDbHelper.getPriorityById(mIdRecycler);
        switch (priority){
            case 1: radio = (RadioButton) myDialog.findViewById(R.id.editRadioButton1);
                break;
            case 2: radio = (RadioButton) myDialog.findViewById(R.id.editRadioButton2);
                break;
            case 3: radio = (RadioButton) myDialog.findViewById(R.id.editRadioButton3);
                break;
        }
        radio.setChecked(true);


        myDialog.show();
        Window window = myDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    View radioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                    RadioButton btn = (RadioButton) radioGroup.getChildAt(radioGroup.indexOfChild(radioButton));
                    String selection = (String) btn.getText();

                    String new_text = ideaField.getText().toString();
                    boolean new_later = doLater.isChecked();
                    int new_priority = Integer.parseInt(selection.toString());

                    mDbHelper.editEntry(mIdRecycler,new_text,new_priority,new_later);
                }
                myDialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

    }
}
