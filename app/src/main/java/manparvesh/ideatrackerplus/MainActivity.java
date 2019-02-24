package manparvesh.ideatrackerplus;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
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
import com.yarolegovich.lovelydialog.LovelyCustomDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.prefs.PreferencesManager;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import manparvesh.ideatrackerplus.customviews.MyMaterialIntroView;
import manparvesh.ideatrackerplus.customviews.NonSwipeableViewPager;
import manparvesh.ideatrackerplus.customviews.ToolbarColorizeHelper;
import manparvesh.ideatrackerplus.database.DataEntry;
import manparvesh.ideatrackerplus.database.DatabaseHelper;
import manparvesh.ideatrackerplus.database.Project;
import manparvesh.ideatrackerplus.database.TinyDB;
import manparvesh.ideatrackerplus.ideamenu.FabShadowBuilder;
import manparvesh.ideatrackerplus.ideamenu.IdeaMenuItemClickListener;
import manparvesh.ideatrackerplus.ideamenu.IdeaMenuItemDragListener;
import manparvesh.ideatrackerplus.recycler.HorizontalAdapter;
import manparvesh.ideatrackerplus.recycler.RecyclerOnClickListener;

public class MainActivity extends AppCompatActivity implements
        TextView.OnEditorActionListener,
        TextWatcher,
        Drawer.OnDrawerItemClickListener,
        Drawer.OnDrawerListener,
        OnCheckedChangeListener,
        MaterialSearchBar.OnSearchActionListener,
        MaterialFavoriteButton.OnFavoriteChangeListener,
        View.OnClickListener,
        View.OnLongClickListener,
        View.OnFocusChangeListener,
        IdeaActivityHost {

    // IDs of the right drawer
    private static final int ID_PRIMARY_COLOR = 1;
    private static final int ID_SECONDARY_COLOR = 2;
    private static final int ID_TEXT_COLOR = 3;
    private static final int ID_CLEAR_DONE = 4;
    private static final int ID_SORT_BY_PRIORITY = 5;
    private static final int ID_RESET_COLOR_PREFS = 6;
    private static final int ID_DARK_THEME = 7;
    private static final int ID_FIRESTORE = 8;


    // IDs of the left drawer
    private static final int ID_RENAME_PROJECT = 1;
    private static final int ID_DELETE_PROJECT = 2;
    private static final int ID_NEW_PROJECT_AND_SWITCH = 3;
    private static final int ID_ALL_PROJECTS = 4;
    private static final int ID_TOGGLE_DONE = 6;
    private static final int ID_SEE_APP_INTRO_AGAIN = 8;
    private static final int ID_ACTIVATE_TUTORIAL_AGAIN = 9;
    private static final int ID_SEND_FEEDBACK = 10;
    private static final int ID_RATE_IDEAS_TRACKER = 11;
    private static final int ID_SOURCE_CODE = 12;
    private static final int ID_TOGGLE_BIG_TEXT = 20;
    private static final int ID_NEW_PROJECT_WITHOUT_SWITCH = 30;

    // Database
    private DatabaseHelper mDbHelper;
    private FirebaseFirestore db;
    // Drawers items
    private Drawer leftDrawer = null;
    private Drawer rightDrawer = null;
    private AccountHeader header = null;
    private SwitchDrawerItem doneSwitch;
    private SwitchDrawerItem bigTextSwitch;
    private SwitchDrawerItem darkSwitch;
    private SwitchDrawerItem fireStoreSwitch;

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
    private MaterialSearchBar mSearchBar = null;
    private boolean searchMode;
    private DroppyMenuPopup.Builder mDroppyBuilder = null;
    private RelativeLayout mIdeasMenu = null;
    private MyMaterialIntroView mIdeasMenuGuide;

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
    private SharedPreferences preferencesEditor;

    private static final String PREF_KEY = "MyPrefKey";
    private int mPrimaryColor;
    private int mSecondaryColor;
    private int mTextColor;
    private ArrayList<Object> mProjects;
    private List<IProfile> mProfiles;
    private int mSelectedProfileIndex;
    private boolean mNoProject = false;
    private boolean mDarkTheme;
    private boolean mFireStore;


    // Color preferences
    private int defaultPrimaryColor;
    private int defaultSecondaryColor;
    private int defaultTextColor;

    // Voice
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;


    // SINGLETON //

    private static MainActivity sInstance;
    private DrawerBuilder rightDrawerBuilder;

    public static synchronized MainActivity getInstance() {
        return sInstance;
    }


    // OVERRODE METHODS //


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferencesEditor = PreferenceManager.getDefaultSharedPreferences(this);
        mTinyDB = new TinyDB(this);
        mDarkTheme = mTinyDB.getBoolean(getString(R.string.dark_theme_pref), false);
        mFireStore = mTinyDB.getBoolean(getString(R.string.firestore_pref), false);
        FirebaseApp.initializeApp(this);
        //db = FirebaseFirestore.getInstance();
        if (mDarkTheme) {
            setTheme(R.style.AppThemeDark_NoActionBar);
        } else {
            setTheme(R.style.AppTheme_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sInstance = this;

        //Initialize SearchListAdapter with proper dark theme value
        SearchListAdapter.getInstance(this, mDarkTheme);
        if (mFireStore) {
            Log.d("tag123", "onCreate: firestore active");
        }
        mDbHelper = DatabaseHelper.getInstance(this);

        // App intro
        introOnFirstStart();

        setUpUI();

        // Fragments manager to populate the tabs
        mFragmentManager = getSupportFragmentManager();
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, mFragmentManager, mDarkTheme);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the tab layout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorHeight(8);

        //TABLES
        loadProjects();

        // Select favorite project
        selectFavoriteProject();

        // Set up drawers in background tasks
        setUpDrawers();
        setRightDrawerSate();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        if (leftDrawer != null) {
            outState = leftDrawer.saveInstanceState(outState);
        }
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
                activateSearch();
                return true;

            case R.id.action_settings:
                if (!mNoProject) rightDrawer.openDrawer();
                return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Speech reckognition
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            // Capitalize first letter
            if (!matches.isEmpty()) {
                StringBuilder capitalized = new StringBuilder(matches.get(0).toLowerCase());
                capitalized.setCharAt(0, Character.toUpperCase(capitalized.charAt(0)));

                // create idea dialog with the spoken text
                newIdeaDialog(capitalized.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // ON CREATE SET UP METHODS //

    private void setUpUI() {

        //Default colors
        defaultPrimaryColor = ContextCompat.getColor(this, R.color.md_blue_grey_800);
        defaultSecondaryColor = ContextCompat.getColor(this, R.color.md_teal_a400);
        defaultTextColor = ContextCompat.getColor(this, R.color.md_white);

        // Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setOnClickListener(this);

        // Wire the floating button
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        mFab.setOnLongClickListener(this);
    }

    // Select favorite project if any, select the first project if no favorite. Show no project if no project.
    private void selectFavoriteProject() {

        if (!mNoProject) {
            mSelectedProfileIndex = getIndexOfFavorite();
            IProfile activeProfile = mProfiles.get(mSelectedProfileIndex);
            String activeProfileName = activeProfile.getName().getText();

            ActionBar bar;
            if ((bar = getSupportActionBar()) != null) {
                bar.setTitle(activeProfileName);
            }

            DataEntry.setTableName(activeProfileName);

            switchToProjectColors();
        }
    }

    // Set up the left and right drawers
    private void setUpDrawers() {

        mAddProject = new ProfileSettingDrawerItem().withName("New project").withIcon(FontAwesome.Icon.faw_plus).withIdentifier(ID_NEW_PROJECT_WITHOUT_SWITCH).withSelectable(false).withOnDrawerItemClickListener(this);

        //HEADER
        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withProfiles(mProfiles)
                .addProfiles(mAddProject)
                .withProfileImagesVisible(false)
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
                        new PrimaryDrawerItem().withIdentifier(ID_RENAME_PROJECT).withName(R.string.rename_pro).withIcon(FontAwesome.Icon.faw_i_cursor).withSelectable(false),
                        new PrimaryDrawerItem().withIdentifier(ID_DELETE_PROJECT).withName(R.string.delete_pro).withIcon(FontAwesome.Icon.faw_trash).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withIdentifier(ID_ALL_PROJECTS).withName(R.string.all_pro).withIcon(GoogleMaterial.Icon.gmd_inbox).withSelectable(false),
                        new PrimaryDrawerItem().withIdentifier(ID_NEW_PROJECT_AND_SWITCH).withName(R.string.new_pro).withIcon(FontAwesome.Icon.faw_plus).withSelectable(false),
                        new DividerDrawerItem(),
                        new ExpandableDrawerItem().withName(R.string.settings).withIcon(FontAwesome.Icon.faw_gear).withSelectable(false).withSubItems(
                                doneSwitch, bigTextSwitch, darkSwitch, fireStoreSwitch),
                        new ExpandableDrawerItem().withName(R.string.help_feedback).withIcon(FontAwesome.Icon.faw_question_circle).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(R.string.see_app_intro).withLevel(2).withIcon(GoogleMaterial.Icon.gmd_camera_rear).withIdentifier(ID_SEE_APP_INTRO_AGAIN).withSelectable(false),
                                new SecondaryDrawerItem().withName(R.string.activate_tuto).withLevel(2).withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(ID_ACTIVATE_TUTORIAL_AGAIN).withSelectable(false),
                                new SecondaryDrawerItem().withName(R.string.rate_app).withLevel(2).withIcon(GoogleMaterial.Icon.gmd_star).withIdentifier(ID_RATE_IDEAS_TRACKER).withSelectable(false),
                                new SecondaryDrawerItem().withName(R.string.feedback).withLevel(2).withIcon(GoogleMaterial.Icon.gmd_bug).withIdentifier(ID_SEND_FEEDBACK).withSelectable(false),
                                new SecondaryDrawerItem().withName(R.string.source_code).withLevel(2).withIcon(GoogleMaterial.Icon.gmd_github).withIdentifier(ID_SOURCE_CODE).withSelectable(false))


                )
                .withOnDrawerItemClickListener(this)
                .withOnDrawerListener(this)
                .withCloseOnClick(false)
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
        mColorItem1 = new PrimaryDrawerItem()
                .withIdentifier(ID_PRIMARY_COLOR)
                .withName(R.string.primary_col)
                .withIcon(FontAwesome.Icon.faw_paint_brush)
                .withIconColor(mPrimaryColor).withSelectable(false);

        mColorItem2 = new PrimaryDrawerItem()
                .withIdentifier(ID_SECONDARY_COLOR)
                .withName(R.string.secondary_col)
                .withIcon(FontAwesome.Icon.faw_paint_brush)
                .withIconColor(mSecondaryColor)
                .withSelectable(false);

        mColorItem3 = new PrimaryDrawerItem()
                .withIdentifier(ID_TEXT_COLOR)
                .withName(R.string.text_col)
                .withIcon(FontAwesome.Icon.faw_paint_brush)
                .withIconColor(mTextColor)
                .withSelectable(false);

        //RIGHT DRAWER
        rightDrawerBuilder = new DrawerBuilder(this)
                .withActionBarDrawerToggleAnimated(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        new SectionDrawerItem().withName(R.string.color_prefs),
                        mColorItem1,
                        mColorItem2,
                        mColorItem3,
                        new PrimaryDrawerItem().withIdentifier(ID_RESET_COLOR_PREFS)
                                .withName(R.string.reset_color_prefs)
                                .withIcon(FontAwesome.Icon.faw_tint)
                                .withSelectable(false),
                        new SectionDrawerItem().withName(R.string.functions),
                        new PrimaryDrawerItem().withIdentifier(ID_CLEAR_DONE)
                                .withName(R.string.clear_done)
                                .withIcon(FontAwesome.Icon.faw_check_circle)
                                .withSelectable(false),
                        new PrimaryDrawerItem()
                                .withIdentifier(ID_SORT_BY_PRIORITY)
                                .withName(R.string.sort_priority)
                                .withIcon(FontAwesome.Icon.faw_sort_amount_desc)
                                .withSelectable(false)
                )
                .withDrawerGravity(Gravity.END)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null && !mNoProject) {
                            int id = (int) drawerItem.getIdentifier();
                            switch (id) {
                                case ID_PRIMARY_COLOR:
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

                                case ID_SECONDARY_COLOR:
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

                                case ID_TEXT_COLOR:
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

                                case ID_CLEAR_DONE:
                                    mDbHelper.clearDoneWithSnack(mViewPager);
                                    rightDrawer.closeDrawer();
                                    break;

                                case ID_SORT_BY_PRIORITY:
                                    mDbHelper.sortByAscPriority();
                                    rightDrawer.closeDrawer();
                                    break;

                                case ID_RESET_COLOR_PREFS:
                                    resetColorsDialog();
                                    break;
                            }
                        } else {
                            noProjectSnack();
                        }
                        return true;
                    }
                });

        rightDrawer = rightDrawerBuilder.build();
        //CURRENT PROJECT
        if (!mNoProject) {
            header.setActiveProfile(mProfiles.get(mSelectedProfileIndex));
            displayIdeasCount();
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

    // Creates the switches displayed in the drawer
    private void setRightDrawerSate() {
        if (mNoProject) {
            rightDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        } else {
            rightDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    // Creates the swicthes displayed in the drawer
    private void setUpSwitches() {
        fireStoreSwitch = new SwitchDrawerItem()
                .withName(R.string.firestore_col)
                .withLevel(2).withIdentifier(ID_FIRESTORE)
                .withOnCheckedChangeListener(this)
                .withChecked(mFireStore)
                .withSelectable(false);


        darkSwitch = new SwitchDrawerItem()
                .withName(R.string.dark_col)
                .withLevel(2).withIdentifier(ID_DARK_THEME)
                .withOnCheckedChangeListener(this)
                .withChecked(mDarkTheme)
                .withSelectable(false);

        doneSwitch = new SwitchDrawerItem()
                .withName(R.string.show_done_msg)
                .withLevel(2).withIdentifier(ID_TOGGLE_DONE)
                .withOnCheckedChangeListener(this)
                .withSelectable(false);

        if (mTinyDB.getBoolean(getString(R.string.show_done_pref)))
            doneSwitch.withChecked(true);
        else
            toggleDoneTab();

        bigTextSwitch = new SwitchDrawerItem()
                .withName(R.string.big_text_msg)
                .withLevel(2).withIdentifier(ID_TOGGLE_BIG_TEXT)
                .withOnCheckedChangeListener(this)
                .withSelectable(false);

        if (mTinyDB.getBoolean(getString(R.string.big_text_pref), false)) {
            bigTextSwitch.withChecked(true);
            HorizontalAdapter.setBigText(true);
        }

    }

    // DIALOG METHODS //

    // Shows an idea creation dialog
    public void newIdeaDialog() {

        mNewIdeaDialog = new LovelyCustomDialog(this, mDarkTheme ? R.style.EditTextTintThemeDark : R.style.EditTextTintTheme)
                .setView(R.layout.new_idea_form)
                .setTopColor(mPrimaryColor)
                .setIcon(R.drawable.ic_bulb)
                .setListener(R.id.doneButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendIdeaFromDialog();
                    }
                })
                .configureView(new LovelyCustomDialog.ViewConfigurator() {
                    @Override
                    public void configureView(View v) {
                        //get the view items
                        mRadioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
                        mIdeaError = (TextView) v.findViewById(R.id.new_error_message);
                        mIdeaField = (EditText) v.findViewById(R.id.editText);
                        mNoteField = (EditText) v.findViewById(R.id.editNote);

                        //set up listener for "ENTER" and text changed
                        mIdeaField.addTextChangedListener(MainActivity.this);
                        mIdeaField.setTag(1);
                        mIdeaField.setOnEditorActionListener(MainActivity.this);
                        mIdeaField.setHighlightColor(Color.LTGRAY);
                        mIdeaField.setOnFocusChangeListener(MainActivity.this);

                        mNoteField.setTag(2);
                        mNoteField.setOnEditorActionListener(MainActivity.this);
                        mNoteField.setHighlightColor(Color.LTGRAY);
                        mNoteField.setOnFocusChangeListener(MainActivity.this);


                        //request focus on the edit text

                    }
                })
                .show();

        if (mNoteField.requestFocus() && mIdeaField.requestFocus()) {
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
                mDbHelper.sortByAscPriority();

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

        newIdeaDialog();

        //check the right priority radio button
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

    // Shows an idea creation dialog
    // pre-fill the idea with the given text
    public void newIdeaDialog(String idea) {

        newIdeaDialog();

        //fill idea field with text
        mIdeaField.append(idea);

    }

    private void newProjectDialog() {

        mProjectDialog = new LovelyCustomDialog(this, mDarkTheme ? R.style.EditTextTintThemeDark : R.style.EditTextTintTheme)
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
        mProjectField.addTextChangedListener(this);
        mProjectField.setHighlightColor(Color.LTGRAY);
        mProjectField.setOnFocusChangeListener(this);

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
            IProfile newProfile = new ProfileDrawerItem().withName(projectName).withIcon(disk).withOnDrawerItemClickListener(this);
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
            if (preferencesEditor.getBoolean(getString(R.string.first_project_pref),true)) {
                leftDrawer.openDrawer();
                header.toggleSelectionList(getApplicationContext());
                firstProjectGuide();
            }
            setRightDrawerSate();
            refreshStar();

            mProjectDialog.dismiss();


        } else { //Error - project name is taken or empty, show the error
            mProjectError.setVisibility(View.VISIBLE);
        }

    }

    // Show a dialog to rename the current project
    private void renameProjectDialog() {

        mProjectDialog = new LovelyCustomDialog(this, mDarkTheme ? R.style.EditTextTintThemeDark : R.style.EditTextTintTheme)
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
        mProjectField.addTextChangedListener(this);

        //request focus on the edit text
        if (mProjectField.requestFocus()) {
            mProjectDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // Shows a dialog to delete the current project
    private void deleteProjectDialog() {

        new LovelyStandardDialog(this, mDarkTheme ? android.support.v7.appcompat.R.style.Theme_AppCompat_Dialog_Alert : 0)
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
                        setRightDrawerSate();
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
        new LovelyStandardDialog(this, mDarkTheme ? android.support.v7.appcompat.R.style.Theme_AppCompat_Dialog_Alert : 0)
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

    // Shows an activity to record voice inputs, to put them as a new idea after
    public void startVoiceRecognitionActivity() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // identifying your application to the Google service
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        // hint in the dialog
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_msg));
        // hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // number of results
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        // recognition language
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }


    // TUTORIAL AND INTRO METHODS //

    // Launch the app introduction only for the first start
    private void introOnFirstStart() {


        //  Create a new boolean and preference and set it to true
        boolean firstStart = preferencesEditor.getBoolean(getString(R.string.preference_firstStart), true);

        //  If the activity has never started before...
        if (firstStart) {
            forceIntro();

            preferencesEditor.edit().putBoolean(getString(R.string.preference_firstStart), false).commit();
        }

    }

    // Launch the app introduction
    private void forceIntro() {
        Intent i = new Intent(MainActivity.this, MyIntro.class);
        startActivity(i);
    }

    // Shows the tutorial for the first project creation
    private void firstProjectGuide() {
        new PreferencesManager(this).reset("first_project");

        new MyMaterialIntroView.Builder(this)
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

        preferencesEditor.edit().putBoolean(getString(R.string.first_project_pref),false).commit();
    }

    // Shows the tutorial for the first idea creation
    private void firstIdeaGuide() {

        new PreferencesManager(this).reset("first");

        new MyMaterialIntroView.Builder(this)
                .enableIcon(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.NORMAL)
                .setTargetPadding(30)
                .setDelayMillis(100)
                .enableFadeAnimation(true)
                .performClick(false)
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
        new MyMaterialIntroView.Builder(this)
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
        new MyMaterialIntroView.Builder(MainActivity.this)
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
                    public void onUserClicked(String MyMaterialIntroViewId) {
                        rightDrawer.openDrawer();
                    }
                })
                .setUsageId("right_drawer") //THIS SHOULD BE UNIQUE ID
                .show();

        mTinyDB.putBoolean(getString(R.string.right_drawer_pref), false);
    }

    private MyMaterialIntroView menuIdeaGuide(View fabView) {
        new PreferencesManager(this).reset("first_menu_idea");
        return new MyMaterialIntroView.Builder(this)
                .enableIcon(true)
                .enableDotAnimation(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.NORMAL)
                .enableFadeAnimation(false)
                .setTargetPadding(530)
                .setInfoText("Drag and drop to targets for different type of idea creation")
                .setInfoTextSize(13)
                .setTarget(fabView)
                .setUsageId("first_menu_idea") //THIS SHOULD BE UNIQUE ID
                .show();
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
            getWindow().setStatusBarColor(darken(mPrimaryColor));
        }

        if (rightDrawer != null) {
            mColorItem1.withIconColor(mPrimaryColor);
            rightDrawer.updateItem(mColorItem1);
        }

        RecyclerOnClickListener.setPrimaryColor(mPrimaryColor);
    }

    private void changeSecondaryColor() {

        //disable search mode for tabLayout
        disableSearchMode();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.setSelectedTabIndicatorColor(mSecondaryColor);
        mFab.setBackgroundTintList(ColorStateList.valueOf(mSecondaryColor));

        if (rightDrawer != null) {
            mColorItem2.withIconColor(mSecondaryColor);
            rightDrawer.updateItem(mColorItem2);
        }

        RecyclerOnClickListener.setSecondaryColor(mSecondaryColor);
    }

    private void changeTextColor() {

        //disable search mode for tabLayout
        disableSearchMode();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.setTabTextColors(slightDarken(mTextColor), mTextColor);
        mToolbar.setTitleTextColor(mTextColor);

        Drawable myFabSrc = getResources().getDrawable(R.drawable.add);
        Drawable newColorDrawable = changeDrawableColor(myFabSrc, mTextColor);

        mFab.setImageDrawable(newColorDrawable);

        ToolbarColorizeHelper.colorizeToolbar(mToolbar, mTextColor, this);

        if (rightDrawer != null) {
            mColorItem3.withIconColor(mTextColor);
            rightDrawer.updateItem(mColorItem3);
        }

    }

    private void toggleFireStore(boolean isFirestoreActive) {
        mTinyDB.putBoolean(getString(R.string.firestore_pref), isFirestoreActive);
        Context context = getApplicationContext();
        CharSequence text = "State: " + isFirestoreActive;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

    private void changeDarkTheme(boolean isDarkThemeEnabled) {
        mTinyDB.putBoolean(getString(R.string.dark_theme_pref), isDarkThemeEnabled);

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @NonNull
    private Drawable changeDrawableColor(Drawable myFabSrc, int textColor) {
        //get the drawable
        //copy it in a new one
        Drawable willBeWhite = myFabSrc.getConstantState().newDrawable();
        //set the color filter, you can use also Mode.SRC_ATOP
        willBeWhite.mutate().setColorFilter(textColor, PorterDuff.Mode.MULTIPLY);
        return willBeWhite;
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

    // Activate search mode
    private void activateSearch() {
        // Search bar
        if (mSearchBar == null) {
            CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mSearchBar = new MaterialSearchBar(this, null);
            mSearchBar.setTextColor(android.R.color.black);
            mSearchBar.setHint(getString(R.string.search));
            mSearchBar.setOnSearchActionListener(this);
            ((CoordinatorLayout) findViewById(R.id.main_content)).addView(mSearchBar, params);

            EditText searchEdit = (EditText) mSearchBar.findViewById(com.mancj.materialsearchbar.R.id.mt_editText);
            searchEdit.addTextChangedListener(this);
        }
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

    // Animate the items for the idea creation, moving them onto circles
    private void setUpIdeaMenuItems() {

        final int RADIUS = (int) (mFab.getWidth() * 1.8f);
        final int BIG_RADIUS = (int) (mFab.getWidth() * 3f);
        final int RADII = (int) (Math.sqrt(2) * RADIUS / 2);
        final int DURATION = 250;
        final int DELAY = 0;

        //ANIMATE ITEM P1
        final FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.item_p1);
        AnimationSet set1 = new AnimationSet(true);
        set1.setInterpolator(new AccelerateDecelerateInterpolator());
        set1.setDuration(DURATION);
        set1.setFillAfter(true);

        TranslateAnimation tr1 = new TranslateAnimation(0f, 0f, 0f, -RADIUS);
        ScaleAnimation scale = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        set1.addAnimation(tr1);
        set1.addAnimation(scale);
        set1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fab1.getLayoutParams();
                params.setMargins(0, 0, 0, RADIUS);
                fab1.setLayoutParams(params);
                fab1.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        //ANIMATE ITEM P2
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.item_p2);
        AnimationSet set2 = new AnimationSet(true);
        set2.setInterpolator(new AccelerateDecelerateInterpolator());
        set2.setDuration(DURATION);
        set2.setStartOffset(DELAY);
        set2.setFillAfter(true);

        TranslateAnimation tr2 = new TranslateAnimation(0f, -RADII, 0f, -RADII);

        set2.addAnimation(tr2);
        set2.addAnimation(scale);
        set2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fab2.getLayoutParams();
                params.setMargins(0, 0, RADII, RADII);
                fab2.setLayoutParams(params);
                fab2.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        //ANIMATE ITEM P3
        final FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.item_p3);
        AnimationSet set3 = new AnimationSet(true);
        set3.setInterpolator(new AccelerateDecelerateInterpolator());
        set3.setDuration(DURATION);
        set3.setStartOffset(2 * DELAY);
        set3.setFillAfter(true);

        TranslateAnimation tr3 = new TranslateAnimation(0f, -RADIUS, 0f, 0f);

        set3.addAnimation(tr3);
        set3.addAnimation(scale);
        set3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fab3.getLayoutParams();
                params.setMargins(0, 0, RADIUS, 0);
                fab3.setLayoutParams(params);
                fab3.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        //ANIMATE ITEM MIC
        final FloatingActionButton fabMic = (FloatingActionButton) findViewById(R.id.item_mic);
        AnimationSet setMic = new AnimationSet(true);
        setMic.setInterpolator(new AccelerateDecelerateInterpolator());
        setMic.setDuration(DURATION);
        setMic.setStartOffset(3 * DELAY);
        setMic.setFillAfter(true);

        TranslateAnimation trMic = new TranslateAnimation(0f, 0f, 0f, -BIG_RADIUS);

        setMic.addAnimation(trMic);
        setMic.addAnimation(scale);
        setMic.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fabMic.getLayoutParams();
                params.setMargins(0, 0, 0, BIG_RADIUS);
                fabMic.setLayoutParams(params);
                fabMic.clearAnimation();

                //notify the onDragListener that the items are ready for action
                IdeaMenuItemDragListener.setReady(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        //LAUNCH ALL ANIMATIONS
        mIdeasMenu.setVisibility(View.VISIBLE);
        IdeaMenuItemDragListener.setReady(false);
        fab1.startAnimation(set1);
        fab2.startAnimation(set2);
        fab3.startAnimation(set3);
        fabMic.startAnimation(setMic);

    }

    // Move the items for the idea creation back to the origin point
    public void rebootIdeaMenuItems() {

        mFab.setVisibility(View.VISIBLE);
        mIdeasMenu.setVisibility(View.INVISIBLE);

        FloatingActionButton fab_item;
        RelativeLayout.LayoutParams params;

        // Loop through all children
        for (int i = 0; i < mIdeasMenu.getChildCount(); i++) {
            fab_item = (FloatingActionButton) mIdeasMenu.getChildAt(i);
            params = (RelativeLayout.LayoutParams) fab_item.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            fab_item.setLayoutParams(params);
        }

        // Dismiss the guide if it was there
        if (mIdeasMenuGuide != null) {
            mIdeasMenuGuide.dismiss();
            mIdeasMenuGuide = null;
            mTinyDB.putBoolean(getString(R.string.idea_menu_pref), false);
        }
    }

    // Convert dp to px
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
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
                    .withOnDrawerItemClickListener(this));
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

        if (mProjects.size() == 0) {
            Project[] otherProjects = new Project[0];
            return otherProjects;
        }

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

    // INTERFACE LISTENERS METHODS //
    // TextView.OnEditorActionListener method
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_GO
                || actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_ACTION_NEXT
                || actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_NULL) {

            switch ((int) v.getTag()) {
                case 1:
                    mNoteField.requestFocus();
                    break;

                case 2:
                    sendIdeaFromDialog();
                    break;

                default:
                    break;
            }
            return true;
        }
        return false;
    }

    // TextWatcher methods
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mIdeaError != null) mIdeaError.setVisibility(View.GONE);
        if (mProjectError != null) mProjectError.setVisibility(View.GONE);
        if (searchMode) SearchListAdapter.changeSearch(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    // Drawer.OnDrawerItemClickListener method
    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

        if (drawerItem != null) {
            int id = (int) drawerItem.getIdentifier();
            switch (id) {
                case ID_RENAME_PROJECT:
                    if (!mNoProject) {
                        renameProjectDialog();
                    } else {
                        noProjectSnack();
                    }
                    break;

                case ID_DELETE_PROJECT:
                    if (!mNoProject) {
                        deleteProjectDialog();
                        leftDrawer.closeDrawer();
                    } else {
                        noProjectSnack();
                    }
                    break;

                case ID_NEW_PROJECT_AND_SWITCH:
                    newProjectDialog();
                    break;

                case ID_ALL_PROJECTS:
                    if (!mNoProject) {
                        header.toggleSelectionList(getApplicationContext());
                    } else {
                        noProjectSnack();
                    }
                    break;

                case ID_SEE_APP_INTRO_AGAIN:
                    forceIntro();
                    break;

                case ID_ACTIVATE_TUTORIAL_AGAIN:
                    leftDrawer.closeDrawer();
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), R.string.tuto_mode, Snackbar.LENGTH_SHORT)
                            .setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    mTinyDB.putBoolean(getString(R.string.handle_idea_pref), true);
                                    preferencesEditor.edit().putBoolean(getString(R.string.first_project_pref),true).commit();
                                    mTinyDB.putBoolean(getString(R.string.first_idea_pref), true);
                                    mTinyDB.putBoolean(getString(R.string.right_drawer_pref), true);
                                    mTinyDB.putBoolean(getString(R.string.idea_menu_pref), true);
                                }
                            });
                    snackbar.show();
                    break;

                case ID_SEND_FEEDBACK:
                    // Open browser to github issues section
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nserguier/IdeasTracker/issues"));
                    startActivity(browserIntent);
                    break;

                case ID_RATE_IDEAS_TRACKER:
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

                case ID_SOURCE_CODE:
                    // Open browser to github source code
                    Intent browserSource = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nserguier/IdeasTracker"));
                    startActivity(browserSource);
                    break;

                case ID_NEW_PROJECT_WITHOUT_SWITCH:
                    newProjectDialog();
                    return false;

            }
        }

        if (drawerItem != null && drawerItem instanceof IProfile) {
            String projectName = ((IProfile) drawerItem).getName().getText(MainActivity.this);
            switchToProject(projectName);
            leftDrawer.closeDrawer();
        }
        return false;
    }

    // Drawer.OnDrawerListener - Listener to trigger tutorial when drawer is closed
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

    // OnCheckedChangeListener - Listener for the settings switches in the left drawer
    @Override
    public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {

        int id = (int) drawerItem.getIdentifier();
        switch (id) {

            case ID_TOGGLE_DONE:
                toggleDoneTab();
                break;

            case ID_TOGGLE_BIG_TEXT:
                if (isChecked) {
                    HorizontalAdapter.setBigText(true);
                    mTinyDB.putBoolean(getString(R.string.big_text_pref), true);
                    DatabaseHelper.notifyAllLists();

                } else {
                    HorizontalAdapter.setBigText(false);
                    mTinyDB.putBoolean(getString(R.string.big_text_pref), false);
                    DatabaseHelper.notifyAllLists();
                }
                break;

            case ID_DARK_THEME:
                changeDarkTheme(isChecked);
                break;

            case ID_FIRESTORE:
                toggleFireStore(isChecked);
        }

    }

    // MaterialSearchBar.OnSearchActionListener - Listeners and watcher for the search tab
    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            disableSearchMode();
        }
    }

    @Override
    public void onSearchConfirmed(final CharSequence text) {
        SearchListAdapter.changeSearch(text.toString());
    }

    @Override
    public void onButtonClicked(int buttonCode) {
    }

    //MaterialFavoriteButton.OnFavoriteChangeListener
    @Override
    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
        if (favorite) {
            mTinyDB.putInt(getString(R.string.favorite_project), getProjectId());
        } else if (mTinyDB.getInt(getString(R.string.favorite_project)) == mSelectedProfileIndex) {
            mTinyDB.putInt(getString(R.string.favorite_project), -1); //no favorite
        }
    }

    // View.OnClickListener - Listener for the toolbar project name and the FAB
    @Override
    public void onClick(View v) {

        if (v instanceof FloatingActionButton) { // FAB click- new idea
            newIdeaDialog();
        } else { // Toolbar click - display othe rproject list
            // Drop down menu - droppy
            if (mDroppyBuilder == null) {
                mDroppyBuilder = new DroppyMenuPopup.Builder(MainActivity.this, mToolbar);
                mDroppyBuilder.triggerOnAnchorClick(false);
            }

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
            } else {
                noProjectSnack();
            }
        }
    }

    // View.OnLongClickListener - Listener for fab to trigger menu idea creation
    @Override
    public boolean onLongClick(View v) {

        //inflate idea menu layout
        if (mIdeasMenu == null) {
            mIdeasMenu = (RelativeLayout) getLayoutInflater().inflate(R.layout.idea_menu, null);
            CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                    CoordinatorLayout.LayoutParams.MATCH_PARENT,
                    CoordinatorLayout.LayoutParams.MATCH_PARENT
            );
            int marginPx = dpToPx(16);
            params.setMargins(marginPx, marginPx, marginPx, marginPx);
            ((CoordinatorLayout) findViewById(R.id.main_content)).addView(mIdeasMenu, params);

            //Set up click listeners on items in case the drag fails
            findViewById(R.id.item_p1).setOnClickListener(new IdeaMenuItemClickListener(1));
            findViewById(R.id.item_p2).setOnClickListener(new IdeaMenuItemClickListener(2));
            findViewById(R.id.item_p3).setOnClickListener(new IdeaMenuItemClickListener(3));
            findViewById(R.id.item_mic).setOnClickListener(new IdeaMenuItemClickListener(4));
        }

        //animation for the fab
        final Animation.AnimationListener fabAnimListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //Set up drag listeners on items
                findViewById(R.id.item_p1).setOnDragListener(new IdeaMenuItemDragListener(1));
                findViewById(R.id.item_p2).setOnDragListener(new IdeaMenuItemDragListener(2));
                findViewById(R.id.item_p3).setOnDragListener(new IdeaMenuItemDragListener(3));
                findViewById(R.id.item_mic).setOnDragListener(new IdeaMenuItemDragListener(4));

                //Move items on a circle
                setUpIdeaMenuItems();

                //Shadow to drop
                FabShadowBuilder shadowBuilder = new FabShadowBuilder(mFab);
                mFab.startDrag(ClipData.newPlainText("", ""), shadowBuilder, mFab, 0);
                mFab.setVisibility(View.INVISIBLE);

                //Guide on first use
                if (mTinyDB.getBoolean(getString(R.string.idea_menu_pref))) {
                    mIdeasMenuGuide = menuIdeaGuide(mFab);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };

        feedbackVibration();
        Animation anim = new ScaleAnimation(
                1f, 1.2f, // Start and end values for the X axis scaling
                1f, 1.2f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setDuration(350);
        anim.setInterpolator(new BounceInterpolator());
        anim.setAnimationListener(fabAnimListener);
        v.startAnimation(anim);
        return true;
    }

    @Override
    public boolean isSearchEnabled() {
        return searchMode;
    }

    @Override
    public void openProjectDialog() {
        newProjectDialog();
    }
}
