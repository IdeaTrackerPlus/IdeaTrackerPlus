package appbox.ideastracker;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.thebluealliance.spectrum.SpectrumDialog;

import java.util.ArrayList;

import appbox.ideastracker.database.DataEntry;
import appbox.ideastracker.database.DatabaseHelper;
import appbox.ideastracker.listadapters.MyCustomAdapter;
import appbox.ideastracker.listadapters.MyListAdapter;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase mDatabase;
    private DatabaseHelper mDbHelper;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager mFragmentManager;

    private int mPrimaryColor = R.color.md_blue_500;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private NonSwipeableViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Get the database helper
        mDbHelper = DatabaseHelper.getInstance(this);
        // Gets the data repository in write mode
        mDatabase = mDbHelper.getWritableDatabase();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.container);
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

        //DRAWERS LEFT AND RIGHT

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //RIGHT DRAWER BUTTONS
        mFragmentManager = getSupportFragmentManager();
        Button mainColor_button = (Button) findViewById(R.id.mainColor_button);
        mainColor_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                    mPrimaryColor = color;
                                }
                            }
                        }).build().show(mFragmentManager, "dialog_spectrum");
            }
        });

        FloatingActionButton fab_go = (FloatingActionButton) findViewById(R.id.move_go);
        fab_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newMoveDialog(view);
            }
        });

        /** Change navigation bar color
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }*/

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void newIdeaDialog() {
        final Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.new_idea_form);
        myDialog.setCancelable(false);
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
                    int priority = Integer.parseInt(selection.toString());

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

    private void newMoveDialog(View view) {

        Spinner spinnerFrom = (Spinner) findViewById(R.id.spinner_from);
        Spinner spinnerTo = (Spinner) findViewById(R.id.spinner_to);
        final String from = spinnerFrom.getSelectedItem().toString();
        final String to = spinnerTo.getSelectedItem().toString();

        String snackText = "Nothing to move from " + from;
        boolean success = false;
        if (from == to) snackText = "Locations must be different";
        else if (mDbHelper.moveAllFromTo(from, to)) {
            snackText = "All ideas from " + from + " moved to " + to;
            success = true;
        }

        Snackbar snackbar = Snackbar.make(view, snackText, Snackbar.LENGTH_LONG);
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

    private void changePrimaryColor(int color){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        LinearLayout rightDrawer = (LinearLayout) findViewById(R.id.right_drawer);
        LinearLayout leftDrawer = (LinearLayout) findViewById(R.id.left_drawer);

        fab.setBackgroundTintList(ColorStateList.valueOf(color));
        toolbar.setBackgroundColor(color);
        tabLayout.setBackgroundColor(color);
        rightDrawer.setBackgroundColor(color);
        leftDrawer.setBackgroundColor(color);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
            return true;
        }

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
        private static BaseExpandableListAdapter mExpandleAdapter;
        private static ArrayList<BaseAdapter> mAdapters;

        static {
            mAdapters = new ArrayList<>();
        }

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
                    mExpandleAdapter = adapter;
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
                    mAdapters.add(adapter2);
                    DatabaseHelper.setAdapterLater(adapter2);
                    list2.setAdapter(adapter2);
                    break;

                case 2: //DONE
                    rootView = inflater.inflate(R.layout.fragment_secondary, container, false);
                    ListView list3 = (ListView) rootView.findViewById(R.id.list);
                    MyListAdapter adapter3 = new MyListAdapter(getContext(), false);
                    mAdapters.add(adapter3);
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
                    // We call collapseGroupWithAnimation(int) and
                    // expandGroupWithAnimation(int) to animate group
                    // expansion/collapse.
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
