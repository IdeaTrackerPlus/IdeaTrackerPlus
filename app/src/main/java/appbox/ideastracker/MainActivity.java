package appbox.ideastracker;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.ColorInt;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;
import com.shehabic.droppy.animations.DroppyFadeInAnimation;
import com.thebluealliance.spectrum.SpectrumDialog;
import com.woxthebox.draglistview.DragListView;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.List;

import appbox.ideastracker.customviews.NonSwipeableViewPager;
import appbox.ideastracker.customviews.ToolbarColorizeHelper;
import appbox.ideastracker.database.DataEntry;
import appbox.ideastracker.database.DatabaseHelper;
import appbox.ideastracker.database.Project;
import appbox.ideastracker.database.TinyDB;
import appbox.ideastracker.ideamenu.FabShadowBuilder;
import appbox.ideastracker.ideamenu.IdeaMenuItemListener;
import appbox.ideastracker.recycler.HorizontalAdapter;
import appbox.ideastracker.recycler.RecyclerOnClickListener;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.prefs.PreferencesManager;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class MainActivity extends AppCompatActivity {

    // Singleton
    private static MainActivity sInstance;

    // Database
    private DatabaseHelper mDbHelper;

    // Drawers items
    private Drawer leftDrawer = null;
    private Drawer rightDrawer = null;
    private AccountHeader header = null;
    private SwitchDrawerItem doneSwitch;
    private SwitchDrawerItem bigTextSwitch;
    private PrimaryDrawerItem mColorItem1;
    private PrimaryDrawerItem mColorItem2;
    private PrimaryDrawerItem mColorItem3;
    private ProfileSettingDrawerItem mAddProject;
    private MaterialFavoriteButton mFavoriteButton;

    // UI elements
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private FragmentManager mFragmentManager;
    private NonSwipeableViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tabLayout;
    private MaterialSearchBar mSearchBar;
    public TextView mSearchLabel;
    private static boolean searchMode;
    private DroppyMenuPopup.Builder mDroppyBuilder;

    // Dialogs
    private Dialog mNewIdeaDialog;
    private Dialog mProjectDialog;

    // Dialogs views
    private RadioGroup mRadioGroup;
    private TextView mIdeaError;
    private EditText mIdeaField;
    private EditText mNoteField;

    private TextView mProjectError;
    private EditText mProjectField;

    // Preferences
    private TinyDB mTinyDB;
    private static final String PREF_KEY = "MyPrefKey";
    private int mPrimaryColor;
    private int mSecondaryColor;
    private int mTextColor;
    private ArrayList<Object> mProjects;
    private List<IProfile> mProfiles;
    private int mSelectedProfileIndex;
    private boolean mNoProject = false;

    // Color preferences
    private int defaultPrimaryColor;
    private int defaultSecondaryColor;
    private int defaultTextColor;


    // STATIC METHODS //

    public static synchronized MainActivity getInstance() {
        return sInstance;
    }

    // OVERRODE METHODS //

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sInstance = this;

        // Databases
        mTinyDB = new TinyDB(this);
        mDbHelper = DatabaseHelper.getInstance(this);

        introOnFirstStart();

        //Default colors
        defaultPrimaryColor = ContextCompat.getColor(this, R.color.md_blue_grey_800);
        defaultSecondaryColor = ContextCompat.getColor(this, R.color.md_teal_a400);
        defaultTextColor = ContextCompat.getColor(this, R.color.md_white);

        // Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Drop down menu - droppy
        mDroppyBuilder = new DroppyMenuPopup.Builder(MainActivity.this, mToolbar);
        mDroppyBuilder.triggerOnAnchorClick(false);
        mToolbar.setOnClickListener(toolBarProjectListener);

        // Searchbar
        mSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        mSearchBar.setHint(getString(R.string.search));
        mSearchBar.setOnSearchActionListener(searchListener);
        EditText searchEdit = (EditText) mSearchBar.findViewById(com.mancj.materialsearchbar.R.id.mt_editText);
        searchEdit.addTextChangedListener(editSearchWatcher);

        // Fragments manager to populate the tabs
        mFragmentManager = getSupportFragmentManager();
        mSectionsPagerAdapter = new SectionsPagerAdapter(mFragmentManager);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the tab layout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorHeight(8);

        // Wire the floating button
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newIdeaDialog();
            }
        });
        mFab.setOnLongClickListener(fabLongClick);

        //TABLES
        loadProjects();

        // Set up the drawers and their items
        setUpDrawers(savedInstanceState);

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
        outState = leftDrawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (leftDrawer != null && leftDrawer.isDrawerOpen()) {
            leftDrawer.closeDrawer();

        } else if (searchMode) {
            disableSearchMode();

        } else {
            super.onBackPressed();
        }
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

        switch (id) {
            case R.id.action_search:
                searchMode = true;

                //hide tabs
                AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
                appbar.removeView(tabLayout);
                //hide floating button
                mFab.setVisibility(View.INVISIBLE);

                //display search bar
                mSearchBar.setVisibility(View.VISIBLE);
                mSearchBar.enableSearch();

                //refresh the fragment display
                mViewPager.setAdapter(null);
                mViewPager.setAdapter(mSectionsPagerAdapter);
                return true;

            case R.id.action_settings:
                if (!mNoProject) rightDrawer.openDrawer();
                return true;
        }

        return false;
    }


    // ON CREATE SET UP METHODS //

    // Creates and fill the right and left drawers
    private void setUpDrawers(Bundle savedInstanceState) {

        mAddProject = new ProfileSettingDrawerItem().withName("New project").withIcon(FontAwesome.Icon.faw_plus).withIdentifier(30).withSelectable(false).withOnDrawerItemClickListener(profile_listener);

        //HEADER
        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withProfiles(mProfiles)
                .addProfiles(mAddProject)
                .withProfileImagesVisible(false)
                .withSavedInstance(savedInstanceState)
                .build();

        //SWITCHES
        setUpSwitches();

        //LEFT DRAWER
        leftDrawer = new DrawerBuilder(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withSelectedItem(-1)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1).withName(R.string.rename_pro).withIcon(FontAwesome.Icon.faw_i_cursor).withSelectable(false),
                        new PrimaryDrawerItem().withIdentifier(2).withName(R.string.delete_pro).withIcon(FontAwesome.Icon.faw_trash).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withIdentifier(4).withName(R.string.all_pro).withIcon(GoogleMaterial.Icon.gmd_inbox).withSelectable(false),
                        new PrimaryDrawerItem().withIdentifier(3).withName(R.string.new_pro).withIcon(FontAwesome.Icon.faw_plus).withSelectable(false),
                        new DividerDrawerItem(),
                        new ExpandableDrawerItem().withName(R.string.settings).withIcon(FontAwesome.Icon.faw_gear).withSelectable(false).withSubItems(
                                doneSwitch, bigTextSwitch),
                        new ExpandableDrawerItem().withName(R.string.help_feedback).withIcon(FontAwesome.Icon.faw_question_circle).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(R.string.see_app_intro).withLevel(2).withIcon(GoogleMaterial.Icon.gmd_camera_rear).withIdentifier(8).withSelectable(false),
                                new SecondaryDrawerItem().withName(R.string.activate_tuto).withLevel(2).withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(9).withSelectable(false),
                                new SecondaryDrawerItem().withName(R.string.rate_app).withLevel(2).withIcon(GoogleMaterial.Icon.gmd_star).withIdentifier(11).withSelectable(false),
                                new SecondaryDrawerItem().withName(R.string.feedback).withLevel(2).withIcon(GoogleMaterial.Icon.gmd_bug).withIdentifier(10).withSelectable(false),
                                new SecondaryDrawerItem().withName(R.string.source_code).withLevel(2).withIcon(GoogleMaterial.Icon.gmd_github).withIdentifier(12).withSelectable(false))


                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            int id = (int) drawerItem.getIdentifier();
                            switch (id) {
                                case 1: //Rename project
                                    if (!mNoProject) {
                                        renameProjectDialog();
                                    } else {
                                        noProjectSnack();
                                    }
                                    break;

                                case 2: //Delete project
                                    if (!mNoProject) {
                                        deleteProjectDialog();
                                    } else {
                                        noProjectSnack();
                                    }
                                    break;

                                case 3: //New project
                                    newProjectDialog();
                                    break;

                                case 4: //My projects
                                    if (!mNoProject) {
                                        header.toggleSelectionList(getApplicationContext());
                                    } else {
                                        noProjectSnack();
                                    }
                                    break;

                                case 8: //See intro again
                                    forceIntro();
                                    break;

                                case 9: //Tutorial mode
                                    leftDrawer.closeDrawer();
                                    Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), R.string.tuto_mode, Snackbar.LENGTH_SHORT)
                                            .setCallback(new Snackbar.Callback() {
                                                @Override
                                                public void onDismissed(Snackbar snackbar, int event) {
                                                    mTinyDB.putBoolean(getString(R.string.handle_idea_pref), true);
                                                    mTinyDB.putBoolean(getString(R.string.first_project_pref), true);
                                                    mTinyDB.putBoolean(getString(R.string.first_idea_pref), true);
                                                    mTinyDB.putBoolean(getString(R.string.right_drawer_pref), true);
                                                }
                                            });
                                    snackbar.show();
                                    break;

                                case 10:
                                    // Open browser to github issues section
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nserguier/IdeasTracker/issues"));
                                    startActivity(browserIntent);
                                    break;

                                case 11:
                                    // Rate
                                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                    // To count with Play market backstack, After pressing back button,
                                    // to taken back to our application, we need to add following flags to intent.
                                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                    try {
                                        startActivity(goToMarket);
                                    } catch (ActivityNotFoundException e) {
                                        startActivity(new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                                    }
                                    break;

                                case 12:
                                    // Open browser to github source code
                                    Intent browserSource = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nserguier/IdeasTracker"));
                                    startActivity(browserSource);
                                    break;

                            }
                        }
                        return true;
                    }
                })
                .withOnDrawerListener(new MyDrawerListener())
                .withSavedInstance(savedInstanceState)
                .build();

        //FAVORITE BUTTON
        mFavoriteButton = (MaterialFavoriteButton) header.getView().findViewById(R.id.favorite_button);
        mFavoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        if (favorite) {
                            mTinyDB.putInt(getString(R.string.favorite_project), getProjectId());
                        } else if (mTinyDB.getInt(getString(R.string.favorite_project)) == mSelectedProfileIndex) {
                            mTinyDB.putInt(getString(R.string.favorite_project), -1); //no favorite
                        }
                    }
                });

        //COLORS BUTTONS
        mColorItem1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.primary_col).withIcon(FontAwesome.Icon.faw_paint_brush).withIconColor(mPrimaryColor).withSelectable(false);
        mColorItem2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.secondary_col).withIcon(FontAwesome.Icon.faw_paint_brush).withIconColor(mSecondaryColor).withSelectable(false);
        mColorItem3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.text_col).withIcon(FontAwesome.Icon.faw_paint_brush).withIconColor(mTextColor).withSelectable(false);

        //RIGHT DRAWER
        rightDrawer = new DrawerBuilder(this)
                .withActionBarDrawerToggleAnimated(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        new SectionDrawerItem().withName(R.string.color_prefs),
                        mColorItem1,
                        mColorItem2,
                        mColorItem3,
                        new PrimaryDrawerItem().withIdentifier(6).withName(R.string.reset_color_prefs).withIcon(FontAwesome.Icon.faw_tint).withSelectable(false),
                        new SectionDrawerItem().withName(R.string.functions),
                        new PrimaryDrawerItem().withIdentifier(4).withName(R.string.clear_done).withIcon(FontAwesome.Icon.faw_check_circle).withSelectable(false),
                        new PrimaryDrawerItem().withIdentifier(5).withName(R.string.sort_priority).withIcon(FontAwesome.Icon.faw_sort_amount_desc).withSelectable(false)
                )
                .withDrawerGravity(Gravity.END)
                .withStickyFooter(R.layout.footer)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null && !mNoProject) {
                            int id = (int) drawerItem.getIdentifier();
                            switch (id) {
                                case 1:
                                    new SpectrumDialog.Builder(getApplicationContext())
                                            .setTitle(R.string.select_prim_col)
                                            .setColors(R.array.colors)
                                            .setSelectedColor(mPrimaryColor)
                                            .setDismissOnColorSelected(false)
                                            .setFixedColumnCount(4)
                                            .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                                                @Override
                                                public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                                                    if (positiveResult) {
                                                        //update selected color
                                                        mPrimaryColor = color;
                                                        changePrimaryColor();
                                                        saveProjectColors();

                                                        //change project icon
                                                        Drawable disk = ContextCompat.getDrawable(getApplicationContext(), R.drawable.disk);
                                                        disk.setColorFilter(mPrimaryColor, PorterDuff.Mode.SRC_ATOP);
                                                        IProfile p = header.getActiveProfile();
                                                        p.withIcon(disk);
                                                        header.updateProfile(p);
                                                    }
                                                }
                                            }).build().show(mFragmentManager, "dialog_spectrum");

                                    break;

                                case 2:
                                    new SpectrumDialog.Builder(getApplicationContext())
                                            .setTitle(R.string.select_sec_col)
                                            .setColors(R.array.accent_colors)
                                            .setSelectedColor(mSecondaryColor)
                                            .setDismissOnColorSelected(false)
                                            .setFixedColumnCount(4)
                                            .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                                                @Override
                                                public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                                                    if (positiveResult) {
                                                        //update selected color
                                                        mSecondaryColor = color;
                                                        changeSecondaryColor();
                                                        saveProjectColors();
                                                    }
                                                }
                                            }).build().show(mFragmentManager, "dialog_spectrum");
                                    break;

                                case 3:
                                    new SpectrumDialog.Builder(getApplicationContext())
                                            .setTitle(R.string.select_text_col)
                                            .setColors(R.array.textColors)
                                            .setSelectedColor(mTextColor)
                                            .setDismissOnColorSelected(false)
                                            .setFixedColumnCount(4)
                                            .setOutlineWidth(2)
                                            .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                                                @Override
                                                public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                                                    if (positiveResult) {
                                                        //update selected color
                                                        mTextColor = color;
                                                        changeTextColor();
                                                        saveProjectColors();
                                                    }
                                                }
                                            }).build().show(mFragmentManager, "dialog_spectrum");
                                    break;

                                case 4:
                                    mDbHelper.clearDoneWithSnack(mViewPager);
                                    rightDrawer.closeDrawer();
                                    break;

                                case 5:
                                    mDbHelper.sortByAscPriority();
                                    rightDrawer.closeDrawer();
                                    break;

                                case 6:
                                    resetColorsDialog();
                                    break;
                            }
                        } else {
                            noProjectSnack();
                        }
                        return true;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .append(leftDrawer);

        //Select favorite project if there is any
        if (!mNoProject) {
            mSelectedProfileIndex = getIndexOfFavorite();
            IProfile activeProfile = mProfiles.get(mSelectedProfileIndex);
            String activeProfileName = activeProfile.getName().getText();
            header.setActiveProfile(activeProfile);

            ActionBar bar;
            if ((bar = getSupportActionBar()) != null) {
                bar.setTitle(activeProfileName);
            }

            DataEntry.setTableName(activeProfileName);
            displayIdeasCount();

            switchToProjectColors();
            refreshStar();
        } else { // No project

            header.setProfiles(mProfiles);
            header.setSelectionSecondLine(getString(R.string.no_project));
            //reset color
            mPrimaryColor = defaultPrimaryColor;
            mSecondaryColor = defaultSecondaryColor;
            mTextColor = defaultTextColor;
            updateColors();
            refreshStar();
        }
    }

    // Creates the swicthes displayed in the drawer
    private void setUpSwitches() {

        doneSwitch = new SwitchDrawerItem().withName(R.string.show_done_msg).withLevel(2).withIdentifier(6).withOnCheckedChangeListener(onCheckedChangeListener).withSelectable(false);
        if (mTinyDB.getBoolean(getString(R.string.show_done_pref))) doneSwitch.withChecked(true);
        else toggleDoneTab();

        bigTextSwitch = new SwitchDrawerItem().withName(R.string.big_text_msg).withLevel(2).withIdentifier(20).withOnCheckedChangeListener(onCheckedChangeListener).withSelectable(false);
        if (mTinyDB.getBoolean(getString(R.string.big_text_pref), false)) {
            bigTextSwitch.withChecked(true);
            HorizontalAdapter.setBigText(true);
        }

    }


    // DIALOG METHODS //

    // Shows an idea creation dialog
    public void newIdeaDialog() {

        mNewIdeaDialog = new LovelyCustomDialog(this, R.style.EditTextTintTheme)
                .setView(R.layout.new_idea_form)
                .setTopColor(mPrimaryColor)
                .setIcon(R.drawable.ic_bulb)
                .setListener(R.id.doneButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendIdeaFromDialog();
                    }
                })
                .show();

        //get the view items
        mRadioGroup = (RadioGroup) mNewIdeaDialog.findViewById(R.id.radioGroup);
        mIdeaError = (TextView) mNewIdeaDialog.findViewById(R.id.new_error_message);
        mIdeaField = (EditText) mNewIdeaDialog.findViewById(R.id.editText);
        mNoteField = (EditText) mNewIdeaDialog.findViewById(R.id.editNote);

        //set up listener for "ENTER" and text changed
        mIdeaField.addTextChangedListener(new HideErrorOnTextChanged());
        mIdeaField.setOnEditorActionListener(ideaFieldListener);
        mNoteField.setOnEditorActionListener(noteFieldListener);

        //request focus on the edit text
        if (mIdeaField.requestFocus()) {
            mNewIdeaDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

    }

    private void sendIdeaFromDialog() {
        Switch doLater = (Switch) mNewIdeaDialog.findViewById(R.id.doLater);

        String text = mIdeaField.getText().toString();
        if (!text.equals("")) {

            boolean later = doLater.isChecked();

            if (mRadioGroup.getCheckedRadioButtonId() != -1) {
                View radioButton = mRadioGroup.findViewById(mRadioGroup.getCheckedRadioButtonId());
                RadioButton btn = (RadioButton) mRadioGroup.getChildAt(mRadioGroup.indexOfChild(radioButton));
                String selection = (String) btn.getText();

                String note = mNoteField.getText().toString();
                int priority = Integer.parseInt(selection);

                mDbHelper.newEntry(text, note, priority, later); //add the idea to the actual database
                displayIdeasCount();

                DatabaseHelper.notifyAllLists();

            }

            mNewIdeaDialog.dismiss();

            if (mTinyDB.getBoolean(getString(R.string.handle_idea_pref))) {
                //move tab where idea was created
                int index = 0;
                if (later) index = 1;

                tabLayout.setScrollPosition(index, 0f, true);
                mViewPager.setCurrentItem(index);

                //start the handle idea guide
                handleIdeaGuide();
            }

        } else {
            mIdeaError.setVisibility(View.VISIBLE);
        }
    }

    // Shows and idea creation dialog
    // pre-select the given priority
    public void newIdeaDialog(int priority) {

        mNewIdeaDialog = new LovelyCustomDialog(this, R.style.EditTextTintTheme)
                .setView(R.layout.new_idea_form)
                .setTopColor(mPrimaryColor)
                .setIcon(R.drawable.ic_bulb)
                .setListener(R.id.doneButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendIdeaFromDialog();
                    }
                })
                .show();

        //set up the error message
        mIdeaError = (TextView) mNewIdeaDialog.findViewById(R.id.new_error_message);
        mIdeaField = (EditText) mNewIdeaDialog.findViewById(R.id.editText);
        mNoteField = (EditText) mNewIdeaDialog.findViewById(R.id.editNote);
        mIdeaField.addTextChangedListener(new HideErrorOnTextChanged());

        //set up "ENTER" listeners
        mIdeaField.setOnEditorActionListener(ideaFieldListener);
        mNoteField.setOnEditorActionListener(noteFieldListener);

        //check the right priority radio button
        mRadioGroup = (RadioGroup) mNewIdeaDialog.findViewById(R.id.radioGroup);
        RadioButton radio = (RadioButton) mNewIdeaDialog.findViewById(R.id.radioButton1);
        switch (priority) {
            case 1:
                radio = (RadioButton) mNewIdeaDialog.findViewById(R.id.radioButton1);
                break;
            case 2:
                radio = (RadioButton) mNewIdeaDialog.findViewById(R.id.radioButton2);
                break;
            case 3:
                radio = (RadioButton) mNewIdeaDialog.findViewById(R.id.radioButton3);
                break;
        }
        radio.setChecked(true);


    }

    private void newProjectDialog() {

        mProjectDialog = new LovelyCustomDialog(this, R.style.EditTextTintTheme)
                .setView(R.layout.project_form)
                .setTopColor(mPrimaryColor)
                .setIcon(R.drawable.ic_notepad)
                .setListener(R.id.projectDoneButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createProjectFromDialog();
                    }
                })
                .show();

        //get the views
        mProjectError = (TextView) mProjectDialog.findViewById(R.id.project_error_message);
        mProjectField = (EditText) mProjectDialog.findViewById(R.id.editProjectName);

        //hide error when text change
        mProjectField.addTextChangedListener(new HideErrorOnTextChanged());

        //request focus on the edit text
        if (mProjectField.requestFocus()) {
            mProjectDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

    }

    private void createProjectFromDialog() {

        String projectName = mProjectField.getText().toString();

        if (isProjectNameAvailable(projectName) && !projectName.equals("")) {

            mDbHelper.newTable(projectName);

            //create the profile with its colored icon
            Drawable disk = ContextCompat.getDrawable(getApplicationContext(), R.drawable.disk);
            disk.setColorFilter(defaultPrimaryColor, PorterDuff.Mode.SRC_ATOP);
            IProfile newProfile = new ProfileDrawerItem().withName(projectName).withIcon(disk).withOnDrawerItemClickListener(profile_listener);
            mProfiles.add(newProfile);

            saveProject(new Project(projectName, defaultPrimaryColor, defaultSecondaryColor, defaultTextColor));

            //open the profile drawer and select the new profile


            header.removeProfile(mAddProject);
            header.addProfile(mAddProject, mProfiles.size());
            header.setActiveProfile(newProfile);
            mSelectedProfileIndex = mProfiles.size() - 2;
            switchToProjectColors();

            mToolbar.setTitle(projectName);
            displayIdeasCount();

            if (mNoProject) {
                mFab.setVisibility(View.VISIBLE);
                mNoProject = false;

                mViewPager.setAdapter(null);
                mViewPager.setAdapter(mSectionsPagerAdapter);
            }

            //If first project ever
            if (mTinyDB.getBoolean(getString(R.string.first_project_pref))) {
                leftDrawer.openDrawer();
                header.toggleSelectionList(getApplicationContext());
                firstProjectGuide();
            }

            refreshStar();

            mProjectDialog.dismiss();


        } else { //Error - project name is taken or empty, show the error
            mProjectError.setVisibility(View.VISIBLE);
        }

    }

    // Show a dialog to rename the current project
    private void renameProjectDialog() {

        mProjectDialog = new LovelyCustomDialog(this, R.style.EditTextTintTheme)
                .setView(R.layout.project_form)
                .setTopColor(mPrimaryColor)
                .setIcon(R.drawable.ic_edit)
                .setListener(R.id.projectDoneButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String projectName = mProjectField.getText().toString();
                        if (!projectName.equals("") && isProjectNameAvailable(projectName)) {

                            //update table's name is the list and the database
                            renameProject(projectName);
                            mDbHelper.renameTable(projectName);

                            //update profile's name
                            IProfile profile = mProfiles.get(mSelectedProfileIndex);
                            profile.withName(projectName);
                            header.updateProfile(profile);
                            mProfiles.remove(mSelectedProfileIndex);
                            mProfiles.add(mSelectedProfileIndex, profile);

                            mToolbar.setTitle(projectName);

                            mProjectDialog.dismiss();

                        } else { //Error - project name taken or empty, show message
                            mProjectError.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .show();

        //get the views
        mProjectError = (TextView) mProjectDialog.findViewById(R.id.project_error_message);
        mProjectField = (EditText) mProjectDialog.findViewById(R.id.editProjectName);
        Button projectButton = (Button) mProjectDialog.findViewById(R.id.projectDoneButton);
        TextView projectTitle = (TextView) mProjectDialog.findViewById(R.id.projectTitle);

        //change title and button label
        projectButton.setText(R.string.rename);
        projectTitle.setText(R.string.rename_pro);

        //hide error when text change
        mProjectField.addTextChangedListener(new HideErrorOnTextChanged());

        //request focus on the edit text
        if (mProjectField.requestFocus()) {
            mProjectDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // Shows a dialog to delete the current project
    private void deleteProjectDialog() {

        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.md_red_400)
                .setButtonsColorRes(R.color.md_deep_orange_500)
                .setIcon(R.drawable.ic_warning)
                .setTitle(getString(R.string.delete_pro_title) + ((Project) mProjects.get(mSelectedProfileIndex)).getName() + "'")
                .setMessage(R.string.delete_pro_message)
                .setPositiveButton(R.string.delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProfiles.remove(mSelectedProfileIndex);
                        deleteProject();
                        mDbHelper.deleteTable();
                        if (mProjects.isEmpty()) {

                            DataEntry.setTableName("");
                            mToolbar.setTitle(R.string.app_name);
                            mFab.setVisibility(View.INVISIBLE);
                            header.setProfiles(mProfiles);
                            header.setSelectionSecondLine(getString(R.string.no_project));
                            mNoProject = true;

                            //refresh the fragment display
                            mViewPager.setAdapter(null);
                            mViewPager.setAdapter(mSectionsPagerAdapter);

                            //reset color
                            mPrimaryColor = defaultPrimaryColor;
                            mSecondaryColor = defaultSecondaryColor;
                            mTextColor = defaultTextColor;
                            updateColors();

                        }
                        switchToExistingProject(mSelectedProfileIndex);

                        //favorite star
                        refreshStar();
                        //search mode
                        disableSearchMode();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    // Shows a dialog to reset color preferences to default
    private void resetColorsDialog() {
        new LovelyStandardDialog(this)
                .setTopColor(mPrimaryColor)
                .setButtonsColorRes(R.color.md_pink_a200)
                .setIcon(R.drawable.ic_drop)
                .setTitle(R.string.reset_color_prefs)
                .setMessage(R.string.reset_color_pref_message)
                .setPositiveButton(android.R.string.yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPrimaryColor = defaultPrimaryColor;
                        mSecondaryColor = defaultSecondaryColor;
                        mTextColor = defaultTextColor;
                        saveProjectColors();
                        updateColors();

                        //change project icon
                        Drawable disk = ContextCompat.getDrawable(getApplicationContext(), R.drawable.disk);
                        disk.setColorFilter(mPrimaryColor, PorterDuff.Mode.SRC_ATOP);
                        IProfile p = header.getActiveProfile();
                        p.withIcon(disk);
                        header.updateProfile(p);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }


    // TUTORIAL AND INTRO METHODS //

    // Launch the app introduction only for the first start
    private void introOnFirstStart() {
        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                //  Create a new boolean and preference and set it to true
                boolean firstStart = mTinyDB.getBoolean("firstStart");

                //  If the activity has never started before...
                if (firstStart) {

                    forceIntro();

                    mTinyDB.putBoolean("firstStart", false);
                }
            }
        });

        t.start();
    }

    // Launch the app introduction
    private void forceIntro() {
        Intent i = new Intent(MainActivity.this, MyIntro.class);
        startActivity(i);
    }

    // Shows the tutorial for the first project creation
    private void firstProjectGuide() {

        new PreferencesManager(this).reset("first_project");

        new MaterialIntroView.Builder(this)
                .enableIcon(true)
                .enableDotAnimation(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.NORMAL)
                .setDelayMillis(100)
                .enableFadeAnimation(true)
                .performClick(true)
                .setInfoText(getString(R.string.first_project_content))
                .setInfoTextSize(13)
                .setTarget(header.getView())
                .setUsageId("first_project") //THIS SHOULD BE UNIQUE ID
                .show();

        mTinyDB.putBoolean(getString(R.string.first_project_pref), false);
    }

    // Shows the tutorial for the first idea creation
    private void firstIdeaGuide() {

        new PreferencesManager(this).reset("first");

        new MaterialIntroView.Builder(this)
                .enableIcon(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.NORMAL)
                .setTargetPadding(30)
                .setDelayMillis(100)
                .enableFadeAnimation(true)
                .performClick(true)
                .setInfoText(getString(R.string.first_idea_title))
                .setInfoTextSize(13)
                .setTarget(mFab)
                .setUsageId("first") //THIS SHOULD BE UNIQUE ID
                .show();

        rightDrawer.closeDrawer();
        leftDrawer.closeDrawer();
        mTinyDB.putBoolean(getString(R.string.first_idea_pref), false);
    }

    // Shows the tutorial on how interacting with ideas
    private void handleIdeaGuide() {

        View firstIdea = findViewById(R.id.firstIdea);
        new PreferencesManager(this).reset("handle");
        new MaterialIntroView.Builder(this)
                .enableDotAnimation(true)
                .enableIcon(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.NORMAL)
                .enableFadeAnimation(true)
                .setInfoText(getString(R.string.handle_idea_content))
                .setInfoTextSize(13)
                .setTarget(firstIdea)
                .setUsageId("handle") //THIS SHOULD BE UNIQUE ID
                .show();

        mTinyDB.putBoolean(getString(R.string.handle_idea_pref), false);
    }


    private void rightDrawerGuide() {

        new PreferencesManager(this).reset("right_drawer");
        new MaterialIntroView.Builder(MainActivity.this)
                .enableIcon(true)
                .enableFadeAnimation(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.NORMAL)
                .setInfoText(getString(R.string.right_drawer_guide))
                .setInfoTextSize(13)
                .performClick(true)
                .setTarget(mToolbar.getChildAt(2))
                .setListener(new MaterialIntroListener() {
                    @Override
                    public void onUserClicked(String materialIntroViewId) {
                        rightDrawer.openDrawer();
                    }
                })
                .setUsageId("right_drawer") //THIS SHOULD BE UNIQUE ID
                .show();

        mTinyDB.putBoolean(getString(R.string.right_drawer_pref), false);
    }


    // UI COLOR METHODS //

    @SuppressWarnings("ConstantConditions")
    private void changePrimaryColor() {

        //disable search mode for tabLayout
        disableSearchMode();

        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);

        mToolbar.setBackgroundColor(mPrimaryColor);
        tabLayout.setBackgroundColor(mPrimaryColor);
        appbar.setBackgroundColor(mPrimaryColor);

        if (Build.VERSION.SDK_INT >= 21) {
            //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().setStatusBarColor(darken(mPrimaryColor));
        }

        mColorItem1.withIconColor(mPrimaryColor);
        rightDrawer.updateItem(mColorItem1);

        RecyclerOnClickListener.setPrimaryColor(mPrimaryColor);
    }

    private void changeSecondaryColor() {

        //disable search mode for tabLayout
        disableSearchMode();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.setSelectedTabIndicatorColor(mSecondaryColor);
        mFab.setBackgroundTintList(ColorStateList.valueOf(mSecondaryColor));

        mColorItem2.withIconColor(mSecondaryColor);
        rightDrawer.updateItem(mColorItem2);
    }

    private void changeTextColor() {

        //disable search mode for tabLayout
        disableSearchMode();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.setTabTextColors(slightDarken(mTextColor), mTextColor);
        mToolbar.setTitleTextColor(mTextColor);

        ToolbarColorizeHelper.colorizeToolbar(mToolbar, mTextColor, this);

        mColorItem3.withIconColor(mTextColor);
        rightDrawer.updateItem(mColorItem3);

    }

    // Change all UI colors to match the color attributes
    private void updateColors() {
        changePrimaryColor();
        changeSecondaryColor();
        changeTextColor();
    }

    // Change all UI colors to match the selected project preferences
    private void switchToProjectColors() {

        //Disable search mode
        disableSearchMode();

        Project selectedProject = (Project) mProjects.get(mSelectedProfileIndex);
        mPrimaryColor = selectedProject.getPrimaryColor();
        mSecondaryColor = selectedProject.getSecondaryColor();
        mTextColor = selectedProject.getTextColor();

        updateColors();

        mColorItem1.withIconColor(mPrimaryColor);
        mColorItem2.withIconColor(mSecondaryColor);
        mColorItem3.withIconColor(mTextColor);

        rightDrawer.updateItem(mColorItem1);
        rightDrawer.updateItem(mColorItem2);
        rightDrawer.updateItem(mColorItem3);

    }

    // Makes a color darker
    private int darken(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.85f;
        color = Color.HSVToColor(hsv);
        return color;
    }

    // Makes a color slightly darker
    private int slightDarken(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.90f;
        color = Color.HSVToColor(hsv);
        return color;
    }


    // UI METHODS //

    // Shows/hide the DONE tab
    private void toggleDoneTab() {

        int count = tabLayout.getTabCount();

        for (int i = 0; i < count; i++) {
            if (tabLayout.getTabAt(i).getText().equals(getString(R.string.done))) {
                tabLayout.removeTabAt(i);
                mSectionsPagerAdapter.setTabCount(2);
                mViewPager.setAdapter(null);
                mViewPager.setAdapter(mSectionsPagerAdapter);
                mTinyDB.putBoolean(getString(R.string.show_done_pref), false);
                return;
            }
        }
        tabLayout.addTab(tabLayout.newTab().setText("Done"));
        mSectionsPagerAdapter.setTabCount(3);
        mViewPager.setAdapter(null);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTinyDB.putBoolean(getString(R.string.show_done_pref), true);


    }

    // Disable the search mode, go back to standard mode
    private void disableSearchMode() {
        if (searchMode) {
            searchMode = false;

            //display tabs again
            AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
            appbar.removeView(tabLayout); //make sure we're not adding the tabLayout while it's already there
            appbar.addView(tabLayout);
            //display floating button
            mFab.setVisibility(View.VISIBLE);

            //hide searchbar
            mSearchBar.setVisibility(View.GONE);

            //refresh the fragment display
            mViewPager.setAdapter(null);
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
    }

    // Vibrates breefly for feedback
    public void feedbackVibration() {
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(18);
    }


    // PROJECT METHODS //

    // Saves a project in the TinyDB
    private void saveProject(Project p) {

        if (mProjects == null) {
            mProjects = new ArrayList<>();
        }
        mProjects.add(p);

        // save the project list to preference
        mTinyDB.putListObject(PREF_KEY, mProjects);

    }

    // Saves color preferences for the current project
    private void saveProjectColors() {
        Project p = (Project) mProjects.get(mSelectedProfileIndex);
        p.setPrimaryColor(mPrimaryColor);
        p.setSecondaryColor(mSecondaryColor);
        p.setTextColor(mTextColor);
        mTinyDB.putListObject(PREF_KEY, mProjects);
    }

    private void renameProject(String newName) {
        Project p = (Project) mProjects.get(mSelectedProfileIndex);
        p.setName(newName);
        mTinyDB.putListObject(PREF_KEY, mProjects);
    }

    private boolean isProjectNameAvailable(String name) {

        for (Object o : mProjects) {
            Project p = (Project) o;
            if (p.getName().equalsIgnoreCase(name)) return false;
        }
        return true;
    }

    private void deleteProject() {
        mProjects.remove(mSelectedProfileIndex);
        mTinyDB.putListObject(PREF_KEY, mProjects);
    }

    // Fills the project and profile lists with the projects saved in the TinyDB
    private void loadProjects() {

        mProjects = mTinyDB.getListObject(PREF_KEY, Project.class);

        if (mProjects.size() == 0) {
            DataEntry.setTableName("");
            mToolbar.setTitle(R.string.app_name);
            mFab.setVisibility(View.INVISIBLE);
            mNoProject = true;
        } else { //Start counter where we stopped
            int lastId = ((Project) mProjects.get(mProjects.size() - 1)).getId();
            Project.setCounter(lastId + 1);
        }

        mProfiles = new ArrayList<>();
        for (Object p : mProjects) {
            Project project = (Project) p;
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.disk);
            drawable.setColorFilter(project.getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);
            mProfiles.add(new ProfileDrawerItem().withName(project.getName())
                    .withIcon(drawable)
                    .withOnDrawerItemClickListener(profile_listener));
        }
    }

    public void displayIdeasCount() {
        int count = mDbHelper.getIdeasCount(0);

        if (count == 0) {
            header.setSelectionSecondLine(getString(R.string.no_ideas));
        } else if (count == 1) {
            header.setSelectionSecondLine(count + " " + getString(R.string.idea));
        } else {
            header.setSelectionSecondLine(count + " " + getString(R.string.ideas));
        }

    }

    // Shows a snackbar message to tell the user there's no project
    private void noProjectSnack() {
        leftDrawer.closeDrawer();
        rightDrawer.closeDrawer();
        Snackbar.make(findViewById(R.id.main_content), R.string.no_project_snack_message, Snackbar.LENGTH_LONG).show();
    }

    // Get selected project toString
    private int getProjectId() {
        return ((Project) mProjects.get(mSelectedProfileIndex)).getId();
    }

    // Fill or empty star depending is the current project is favorite
    private void refreshStar() {

        if (!mNoProject) {
            mFavoriteButton.setVisibility(View.VISIBLE);
            if (mTinyDB.getInt(getString(R.string.favorite_project)) == getProjectId()) {
                mFavoriteButton.setFavorite(true, false);
            } else {
                mFavoriteButton.setFavorite(false, false);
            }
        } else {
            mFavoriteButton.setVisibility(View.INVISIBLE);
        }
    }

    // Get index of favorite project if any
    private int getIndexOfFavorite() {

        int favoriteId = mTinyDB.getInt(getString(R.string.favorite_project)); //id of fav or -1 if no fav
        Project p;
        int index = 0;
        for (Object o : mProjects) {
            p = (Project) o;
            if (p.getId() == favoriteId) {
                return index;
            }
            index++;
        }

        return 0;
    }

    // Get index of project with a given name
    private int getIndexOfProject(String projectName) {
        Project p;
        int index = 0;
        for (Object o : mProjects) {
            p = (Project) o;
            if (p.getName().equals(projectName)) {
                return index;
            }
            index++;
        }

        return 0;
    }

    // Get the list of the other projects, excluding the current one
    private Project[] getOtherProjects() {

        int size = mProjects.size() - 1;
        Project[] otherProjects = new Project[size];
        Object currentProject = mProjects.remove(mSelectedProfileIndex);

        int index = 0;
        for (Object o : mProjects) {
            otherProjects[index] = (Project) o;
            index++;
        }

        mProjects.add(mSelectedProfileIndex, currentProject);
        return otherProjects;
    }

    // Switch to another project, project name has to be a valid project
    private void switchToProject(String projectName) {

        mSelectedProfileIndex = getIndexOfProject(projectName);
        IProfile profileToSelect = mProfiles.get(mSelectedProfileIndex);
        header.setActiveProfile(profileToSelect);

        mToolbar.setTitle(projectName);
        mDbHelper.switchTable(projectName);
        DatabaseHelper.notifyAllLists();
        displayIdeasCount();
        switchToProjectColors();

        //favorite star
        refreshStar();

        //search mode
        disableSearchMode();
    }

    /**
     * After project deletion, selects another project
     *
     * @param index the index of the deleted project
     */
    private void switchToExistingProject(int index) {
        index -= 1;
        boolean inBounds = (index >= 0) && (index < mProfiles.size());

        if (!mNoProject) {

            if (inBounds) mSelectedProfileIndex = index;
            else mSelectedProfileIndex = 0;

            IProfile profileToSelect = mProfiles.get(mSelectedProfileIndex);
            String tableToSelect = profileToSelect.getName().getText();
            header.setActiveProfile(profileToSelect);
            mToolbar.setTitle(tableToSelect);
            mDbHelper.switchTable(tableToSelect);
            displayIdeasCount();

            switchToProjectColors();
        }

    }


    // FRAGMENT CLASSES //

    /**
     * Fragment containing the listView to be displayed in each tab
     */
    public static class ListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private static MainActivity mainActivity;

        public static ListFragment newInstance(String tabName) {
            ListFragment f = new ListFragment();

            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putString("tabName", tabName);
            f.setArguments(args);

            return f;
        }

        public String getTabName() {
            return getArguments().getString("tabName");
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            mainActivity = MainActivity.getInstance();

            View rootView;
            if (DataEntry.TABLE_NAME.equals("[]")) {
                rootView = inflater.inflate(R.layout.no_project_layout, container, false);
                LinearLayout lin = (LinearLayout) rootView.findViewById(R.id.noProject);
                lin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainActivity.newProjectDialog();
                    }
                });
                return rootView;
            }

            if (MainActivity.searchMode) {
                rootView = inflater.inflate(R.layout.search_view, container, false);
                ListView list = (ListView) rootView.findViewById(R.id.search_list);
                mainActivity.mSearchLabel = (TextView) rootView.findViewById(R.id.search_text);

                SearchListAdapter adapter = SearchListAdapter.getInstance(getContext());
                list.setAdapter(adapter);
                mainActivity.mSearchLabel.setText("Search for ...");
                return rootView;
            }

            //Inflate the list view
            rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
            DragListView mDragListView = (DragListView) rootView.findViewById(R.id.list);
            mDragListView.setLayoutManager(new LinearLayoutManager(getActivity()));

            //Determine tab number
            int tabNumber = 0;
            if (getTabName().equals(getString(R.string.first_tab))) { //IDEAS
                tabNumber = 1;
            } else if (getTabName().equals(getString(R.string.second_tab))) {
                tabNumber = 2;
            } else if (getTabName().equals(getString(R.string.third_tab))) {
                tabNumber = 3;
            }

            //Set reorder listener
            final int finalTabNumber = tabNumber;
            mDragListView.setDragListListener(new DragListView.DragListListener() {

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onItemDragStarted(int position) {
                }

                @Override
                public void onItemDragEnded(int fromPosition, int toPosition) {
                    if (fromPosition != toPosition) {
                        DatabaseHelper.getInstance(getContext()).resetEntriesOrderAt(finalTabNumber);
                    }
                }

                @Override
                public void onItemDragging(int itemPosition, float x, float y) {
                }
            });

            //Set adapter
            ItemAdapter itemAdapter = new ItemAdapter(getContext(), tabNumber, R.layout.recycler_view_item, R.id.horizontal_recycler_view);
            mDragListView.setAdapter(itemAdapter, false);
            mDragListView.setCanDragHorizontally(false);

            DatabaseHelper.setAdapterAtTab(tabNumber, itemAdapter);
            DatabaseHelper.notifyAllLists();

            return rootView;
        }

    }

    /**
     * Fragment adapter creating the right fragment for the right tab
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int tabCount = 3;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setTabCount(int count) {
            tabCount = count;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            return ListFragment.newInstance(tabLayout.getTabAt(position).getText().toString());
        }

        @Override
        public int getCount() {
            return tabCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.first_tab);
                case 1:
                    return getString(R.string.second_tab);
                case 2:
                    return getString(R.string.third_tab);
            }
            return null;
        }
    }


    // LISTENERS //

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
                return true;
            }
            return false;
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
                sendIdeaFromDialog();
                return true;
            }
            return false;
        }
    };

    private class HideErrorOnTextChanged implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mIdeaError != null) mIdeaError.setVisibility(View.GONE);
            if (mProjectError != null) mProjectError.setVisibility(View.GONE);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


    // Click listener for drawer profiles
    private Drawer.OnDrawerItemClickListener profile_listener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

            if (drawerItem.getIdentifier() == 30) {//Add project
                leftDrawer.openDrawer();
                newProjectDialog();
                return true;
            }

            if (drawerItem != null && drawerItem instanceof IProfile) {

                String projectName = ((IProfile) drawerItem).getName().getText(MainActivity.this);
                switchToProject(projectName);
            }
            return false;
        }
    };

    // Listener to trigger tutorial when drawer is closed
    private class MyDrawerListener implements Drawer.OnDrawerListener {

        private boolean mHandleFilter;

        @Override
        public void onDrawerOpened(View drawerView) {
            mHandleFilter = rightDrawer.isDrawerOpen();

        }

        @Override
        public void onDrawerClosed(View drawerView) {

            if (mTinyDB.getBoolean(getString(R.string.right_drawer_pref)) && !mNoProject && !mHandleFilter) {//Left drawer closed
                rightDrawerGuide();
            } else if (mTinyDB.getBoolean(getString(R.string.first_idea_pref)) && !mNoProject && mHandleFilter) {
                firstIdeaGuide();
            }
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
        }
    }

    // Listener for the settings switches in the left drawer
    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {

            int id = (int) drawerItem.getIdentifier();
            switch (id) {

                case 6:
                    toggleDoneTab();
                    break;

                case 20:
                    if (isChecked) {
                        HorizontalAdapter.setBigText(true);
                        mTinyDB.putBoolean(getString(R.string.big_text_pref), true);
                        DatabaseHelper.notifyAllLists();

                    } else {
                        HorizontalAdapter.setBigText(false);
                        mTinyDB.putBoolean(getString(R.string.big_text_pref), false);
                        DatabaseHelper.notifyAllLists();

                    }
            }


        }
    };

    // Listeners and watcher for the search tab
    private Handler mHandler = new Handler(); //Handle modification made outside of the UI thread
    private MaterialSearchBar.OnSearchActionListener searchListener = new MaterialSearchBar.OnSearchActionListener() {
        @Override
        public void onSearchStateChanged(boolean enabled) {
            if (!enabled) {
                disableSearchMode();
            }
        }

        @Override
        public void onSearchConfirmed(final CharSequence text) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSearchLabel.setText("Search for " + text.toString());
                    mSearchLabel.invalidate();
                }
            });
            SearchListAdapter.changeSearch(text.toString());
        }

        @Override
        public void onButtonClicked(int buttonCode) {
        }

    };

    private TextWatcher editSearchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(final CharSequence s, int start, int before, int count) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSearchLabel.setText("Search for " + s);
                    mSearchLabel.invalidate();
                }
            });
            SearchListAdapter.changeSearch(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // Listener for the toolbar project name

    private View.OnClickListener toolBarProjectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final Project[] otherProjects = getOtherProjects();
            mDroppyBuilder = new DroppyMenuPopup.Builder(MainActivity.this, mToolbar);
            mDroppyBuilder.triggerOnAnchorClick(false);
            if (otherProjects.length > 0) {
                for (Project p : otherProjects) {
                    mDroppyBuilder.addMenuItem(new DroppyMenuItem(p.getName()));
                }


                mDroppyBuilder.setOnClick(new DroppyClickCallbackInterface() {
                    @Override
                    public void call(View v, int id) {
                        switchToProject(otherProjects[id].getName());
                    }
                });

                mDroppyBuilder.setPopupAnimation(new DroppyFadeInAnimation())
                        .setXOffset(150);

                DroppyMenuPopup droppyMenu = mDroppyBuilder.build();
                droppyMenu.show();
            }
        }
    };

    //DRAG AND DROP OF FAB

    private View.OnLongClickListener fabLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            feedbackVibration();
            Animation anim = new ScaleAnimation(
                    1f, 1.2f, // Start and end values for the X axis scaling
                    1f, 1.2f, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
            anim.setDuration(350);
            anim.setInterpolator(new BounceInterpolator());
            //anim.setFillAfter(true); // Needed to keep the result of the animation
            anim.setAnimationListener(fabAnimListener);
            v.startAnimation(anim);
            return true;
        }
    };

    private Animation.AnimationListener fabAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {

            //Screen dim
            ImageView screenDim = (ImageView) findViewById(R.id.screenDim);
            screenDim.setAlpha(0.5f);

            //Set up listeners on items
            IdeaMenuItemListener.setScreenDim(screenDim);
            findViewById(R.id.item_p1).setOnDragListener(new IdeaMenuItemListener(1));
            findViewById(R.id.item_p2).setOnDragListener(new IdeaMenuItemListener(2));
            findViewById(R.id.item_p3).setOnDragListener(new IdeaMenuItemListener(3));

            //Move items on a circle
            setUpIdeaMenuItems();


            //Shadow to drop
            FabShadowBuilder shadowBuilder = new FabShadowBuilder(mFab);
            mFab.startDrag(ClipData.newPlainText("", ""), shadowBuilder, mFab, 0);
            mFab.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    private void setUpIdeaMenuItems() {

        final int RADIUS = mFab.getWidth() * 2;
        final int RADII = (int) (Math.sqrt(2) * RADIUS / 2);
        final int DURATION = 2000;
        final int DELAY = 500;

        //ANIMATE ITEM 1
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.item_p1);
        AnimationSet set1 = new AnimationSet(true);
        set1.setInterpolator(new AccelerateDecelerateInterpolator());
        set1.setDuration(DURATION);
        set1.setFillAfter(true);

        TranslateAnimation tr1 = new TranslateAnimation(0f, 0f, 0f, -RADIUS);
        ScaleAnimation sc1 = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        set1.addAnimation(tr1);
        set1.addAnimation(sc1);
        set1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.item_p1);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fab1.getLayoutParams();
                params.setMargins(0, 0, 0, RADIUS);
                fab1.setLayoutParams(params);
                fab1.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        //ANIMATE ITEM 2
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.item_p2);
        AnimationSet set2 = new AnimationSet(true);
        set2.setInterpolator(new AccelerateDecelerateInterpolator());
        set2.setDuration(DURATION);
        set2.setStartOffset(DELAY);
        set2.setFillAfter(true);

        TranslateAnimation tr2 = new TranslateAnimation(0f, 0f, -RADII, -RADII);
        ScaleAnimation sc2 = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        set2.addAnimation(tr2);
        set2.addAnimation(sc2);
        set2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.item_p2);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fab2.getLayoutParams();
                params.setMargins(0, 0, RADII, RADII);
                fab2.setLayoutParams(params);
                fab2.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        //ANIMATE ITEM 3
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.item_p3);
        AnimationSet set3 = new AnimationSet(true);
        set3.setInterpolator(new AccelerateDecelerateInterpolator());
        set3.setDuration(DURATION);
        set3.setStartOffset(2 * DELAY);
        set3.setFillAfter(true);

        TranslateAnimation tr3 = new TranslateAnimation(0f, 0f, -RADII, 0f);
        ScaleAnimation sc3 = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        set3.addAnimation(tr3);
        set3.addAnimation(sc3);
        set3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.item_p3);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fab3.getLayoutParams();
                params.setMargins(0, 0, RADII, 0);
                fab3.setLayoutParams(params);
                fab3.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        //LAUNCH ALL ANIMATIONS
        fab1.startAnimation(set1);
        fab2.startAnimation(set2);
        fab3.startAnimation(set3);

    }

}
