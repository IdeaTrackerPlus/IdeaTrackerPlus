package appbox.ideastracker;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.thebluealliance.spectrum.SpectrumDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import appbox.ideastracker.database.DataEntry;
import appbox.ideastracker.database.DatabaseHelper;
import appbox.ideastracker.listadapters.MyCustomAdapter;
import appbox.ideastracker.listadapters.MyListAdapter;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper mDbHelper;

    private Drawer result = null;
    private Drawer append = null;
    private AccountHeader header = null;
    private Toolbar mToolbar;
    private FragmentManager mFragmentManager;

    private int mPrimaryColor;
    private ArrayList<String> mTableNames;
    private List<IProfile> mProfiles;


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TABLES
        //TODO: retrieve table names from shared preferences
        //if table name gathering failed, no names then create default one
        mTableNames = new ArrayList<>();
        mTableNames.add("My_Project");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("My_Project");
        setSupportActionBar(mToolbar);

        mFragmentManager = getSupportFragmentManager();

        //Get the database helper
        mDbHelper = DatabaseHelper.getInstance(this);
        //mDbHelper.newTable("My_Project");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        NonSwipeableViewPager mViewPager = (NonSwipeableViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("Ideas"));
        tabLayout.addTab(tabLayout.newTab().setText("Later"));
        tabLayout.addTab(tabLayout.newTab().setText("Done"));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG.setAction("Action", null).show();
                newIdeaDialog();
            }
        });

        //DRAWERS
        setUpDrawers(savedInstanceState);

    }

    private Drawer.OnDrawerItemClickListener profile_listener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            if (drawerItem != null && drawerItem instanceof IProfile) {
                String tableName = ((IProfile) drawerItem).getName().getText(MainActivity.this);
                mToolbar.setTitle(tableName);
                mDbHelper.switchTable(tableName);
            }
            return false;
        }
    };

    private AccountHeader.OnAccountHeaderListener header_listener = new AccountHeader.OnAccountHeaderListener() {
        @Override
        public boolean onProfileChanged(View view, IProfile profile, boolean current) {
            return true;
        }
    };

    private void setUpDrawers(Bundle savedInstanceState) {

        //PROFILES
        mProfiles = new ArrayList<>();
        for (String table : mTableNames) {
            mProfiles.add(new ProfileDrawerItem().withName(table).withOnDrawerItemClickListener(profile_listener));
        }

        //HEADER
        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withOnAccountHeaderListener(header_listener)
                .withHeaderBackground(R.drawable.header)
                .withProfiles(mProfiles)
                .withProfileImagesVisible(false)
                .withSavedInstance(savedInstanceState)
                .build();


        //LEFT DRAWER
        result = new DrawerBuilder(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withSelectedItem(-1)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1).withName("Rename table").withIcon(FontAwesome.Icon.faw_i_cursor).withSelectable(false),
                        new PrimaryDrawerItem().withIdentifier(2).withName("Delete table").withIcon(FontAwesome.Icon.faw_trash).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withIdentifier(3).withName("New Table").withIcon(FontAwesome.Icon.faw_plus).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            int id = (int) drawerItem.getIdentifier();
                            switch (id) {
                                case 1:
                                    renameTableDialog();
                                    break;

                                case 2:
                                    int index = mTableNames.indexOf(DataEntry.TABLE_NAME);
                                    mTableNames.remove(index);
                                    mProfiles.remove(index);
                                    mDbHelper.deleteTable();
                                    switchToExistingTable(index);
                                    break;

                                case 3:
                                    newTableDialog();
                                    break;
                            }
                        }
                        return true;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        mPrimaryColor = getResources().getColor(R.color.md_blue_500);
        final PrimaryDrawerItem aItem1 = new PrimaryDrawerItem().withIdentifier(1).withName("Primary color").withIcon(FontAwesome.Icon.faw_paint_brush).withIconColor(mPrimaryColor).withSelectable(false);

        //RIGHT DRAWER
        append = new DrawerBuilder(this)
                .withActionBarDrawerToggleAnimated(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        new SectionDrawerItem().withName("Customize colors"),
                        aItem1,
                        new PrimaryDrawerItem().withIdentifier(2).withName("Secondary color").withIcon(FontAwesome.Icon.faw_paint_brush).withSelectable(false),
                        new PrimaryDrawerItem().withIdentifier(3).withName("Text color").withIcon(FontAwesome.Icon.faw_paint_brush).withSelectable(false),
                        new SectionDrawerItem().withName("Functions"),
                        new PrimaryDrawerItem().withIdentifier(4).withName("Move all ideas from a tab").withIcon(FontAwesome.Icon.faw_exchange).withSelectable(false),
                        new PrimaryDrawerItem().withIdentifier(5).withName("Expand/collapse all").withIcon(FontAwesome.Icon.faw_arrows_v).withSelectable(false)
                )
                .withDrawerGravity(Gravity.END)
                .withStickyFooter(R.layout.footer)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {
                            int id = (int) drawerItem.getIdentifier();
                            switch (id) {
                                case 1:
                                    new SpectrumDialog.Builder(getApplicationContext())
                                            .setColors(R.array.colors)
                                            .setSelectedColor(mPrimaryColor)
                                            .setDismissOnColorSelected(false)
                                            .setFixedColumnCount(4)
                                            .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                                                @Override
                                                public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                                                    if (positiveResult) {
                                                        //update selected color
                                                        changePrimaryColor(color);
                                                        aItem1.withIconColor(color);
                                                        append.updateItem(aItem1);
                                                        mPrimaryColor = color;
                                                    }
                                                }
                                            }).build().show(mFragmentManager, "dialog_spectrum");

                                    break;

                                case 2:
                                    break;

                                case 3:
                                    break;

                                case 4:
                                    newMoveDialog();
                                    append.closeDrawer();
                                    break;
                            }
                        }
                        return true;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .append(result);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void newIdeaDialog() {

        final Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.new_idea_form);
        myDialog.setCanceledOnTouchOutside(true);
        Button done = (Button) myDialog.findViewById(R.id.doneButton);
        Button cancel = (Button) myDialog.findViewById(R.id.cancelButton);
        final ToggleButton doLater = (ToggleButton) myDialog.findViewById(R.id.doLater);
        final RadioGroup radioGroup = (RadioGroup) myDialog.findViewById(R.id.radioGroup);

        final EditText ideaField = (EditText) myDialog.findViewById(R.id.editText);
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

                    String text = ideaField.getText().toString();
                    boolean later = doLater.isChecked();
                    int priority = Integer.parseInt(selection);

                    mDbHelper.newEntry(text, priority, later); //add the idea to the actual database

                    DatabaseHelper.notifyAllLists();
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

    @SuppressWarnings("ConstantConditions")
    private void newMoveDialog() {

        final Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.move_dialog);
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.show();

        final View root = findViewById(R.id.main_content);
        Button cancelButton = (Button) myDialog.findViewById(R.id.cancel_move_button);
        Button goButton = (Button) myDialog.findViewById(R.id.move_button);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();

                Spinner spinnerFrom = (Spinner) myDialog.findViewById(R.id.spinner_from);
                Spinner spinnerTo = (Spinner) myDialog.findViewById(R.id.spinner_to);
                final String from = spinnerFrom.getSelectedItem().toString();
                final String to = spinnerTo.getSelectedItem().toString();

                String snackText = "Nothing to move from " + from;
                boolean success = false;
                if (from.equals(to)) snackText = "Locations must be different";
                else if (mDbHelper.moveAllFromTo(from, to)) {
                    snackText = "All ideas from " + from + " moved to " + to;
                    success = true;
                }

                Snackbar snackbar = Snackbar.make(root, snackText, Snackbar.LENGTH_LONG);
                if (success) {
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (to.equals("Trash")) {//undo temp deleting
                                mDbHelper.recoverAllFromTemp();
                            } else {
                                mDbHelper.moveAllFromTo(to, from);
                            }
                        }
                    }).setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if ((event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE) && to.equals("Trash")) {
                                //delete for real ideas in temp
                                mDbHelper.deleteAllFromTemp();
                            }
                        }
                    });
                }

                snackbar.show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

    }

    private void newTableDialog() {

        final Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.table_form);
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.show();

        TextView title = (TextView) myDialog.findViewById(R.id.table_title);
        title.setText("New Project");
        final EditText name = (EditText) myDialog.findViewById(R.id.table_editText);

        Button done = (Button) myDialog.findViewById(R.id.table_doneButton);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = name.getText().toString();
                //TODO: make sure name is valid
                if (mTableNames.contains(tableName)) {
                    //TODO: Warning same name exist already
                } else {
                    mDbHelper.newTable(tableName);
                    mTableNames.add(tableName);
                    IProfile newProfile = new ProfileDrawerItem().withName(tableName).withOnDrawerItemClickListener(profile_listener);
                    header.addProfiles(newProfile);
                    myDialog.dismiss();
                    //TODO: open the profile drawer and select the new profile
                    header.setActiveProfile(newProfile);
                    header.toggleSelectionList(getApplicationContext());
                    mToolbar.setTitle(tableName);
                }
            }
        });

        Button cancel = (Button) myDialog.findViewById(R.id.table_cancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
    }

    private void renameTableDialog() {

        final Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.table_form);
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.show();

        TextView title = (TextView) myDialog.findViewById(R.id.table_title);
        title.setText("Rename Project: " + DataEntry.TABLE_NAME);
        final EditText name = (EditText) myDialog.findViewById(R.id.table_editText);

        Button done = (Button) myDialog.findViewById(R.id.table_doneButton);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update table's name is the list and the database
                String tableName = name.getText().toString();
                int index = mTableNames.indexOf(DataEntry.TABLE_NAME);
                mTableNames.add(index, tableName);
                mTableNames.remove(DataEntry.TABLE_NAME);
                mDbHelper.renameTable(tableName);

                //update profile's name
                IProfile profile = mProfiles.get(index);
                profile.withName(tableName);
                header.updateProfile(profile);

                mToolbar.setTitle(tableName);

                myDialog.dismiss();
            }
        });

        Button cancel = (Button) myDialog.findViewById(R.id.table_cancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
    }

    private void switchToExistingTable(int index) {
        index -= 1;
        boolean inBounds = (index >= 0) && (index < mTableNames.size());
        if (inBounds) {
            String tableToSelect = mTableNames.get(index);
            IProfile profileToSelect = mProfiles.get(index);
            header.setActiveProfile(profileToSelect);
            mToolbar.setTitle(tableToSelect);
            mDbHelper.switchTable(tableToSelect);
        }else if(!mTableNames.isEmpty()){
            String tableToSelect = mTableNames.get(0);
            IProfile profileToSelect = mProfiles.get(0);
            header.setActiveProfile(profileToSelect);
            mToolbar.setTitle(tableToSelect);
            mDbHelper.switchTable(tableToSelect);
        }else{
            //TODO: No table to show
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void changePrimaryColor(int color) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);


        fab.setBackgroundTintList(ColorStateList.valueOf(color));
        toolbar.setBackgroundColor(color);
        tabLayout.setBackgroundColor(color);
        appbar.setBackgroundColor(color);

        if (Build.VERSION.SDK_INT >= 21) {
            //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().setStatusBarColor(darken(color));
        }
    }

    public int darken(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.75f;
        color = Color.HSVToColor(hsv);
        return color;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        append.openDrawer();
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        public static ListFragment newInstance(int index) {
            ListFragment f = new ListFragment();

            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt("index", index);
            f.setArguments(args);

            return f;
        }

        public int getIndex() {
            return getArguments().getInt("index", 0);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            switch (this.getIndex()) {
                case 0: //IDEAS
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    AnimatedExpandableListView list = (AnimatedExpandableListView) rootView.findViewById(R.id.expandableList);
                    //sets the adapter that provides data to the list
                    MyCustomAdapter adapter = new MyCustomAdapter(getContext());
                    DatabaseHelper.setAdapterIdea(adapter);
                    list.setAdapter(adapter);
                    list.expandGroup(0);
                    list.expandGroup(1);
                    list.expandGroup(2);
                    setExpandAnimation(list);

                    break;

                case 1: //LATER
                    rootView = inflater.inflate(R.layout.fragment_secondary, container, false);
                    ListView list2 = (ListView) rootView.findViewById(R.id.list);
                    MyListAdapter adapter2 = new MyListAdapter(getContext(), true);
                    DatabaseHelper.setAdapterLater(adapter2);
                    list2.setAdapter(adapter2);
                    break;

                case 2: //DONE
                    rootView = inflater.inflate(R.layout.fragment_secondary, container, false);
                    ListView list3 = (ListView) rootView.findViewById(R.id.list);
                    MyListAdapter adapter3 = new MyListAdapter(getContext(), false);
                    DatabaseHelper.setAdapterDone(adapter3);
                    list3.setAdapter(adapter3);
                    break;

            }


            return rootView;
        }

        void setExpandAnimation(final AnimatedExpandableListView listView) {
            listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                    if (listView.isGroupExpanded(groupPosition)) {
                        listView.collapseGroupWithAnimation(groupPosition);
                    } else {
                        listView.expandGroupWithAnimation(groupPosition);
                    }
                    return true;
                }

            });

        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            return ListFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Ideas";
                case 1:
                    return "Later";
                case 2:
                    return "Done";
            }
            return null;
        }
    }

}
