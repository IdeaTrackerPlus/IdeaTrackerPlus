package manparvesh.ideatrackerplus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.preference.PreferenceManager;

import org.junit.Before;
import org.junit.Rule;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

class AbstractUITest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Before
    public void cleanUpConfig() {
        Context context = getInstrumentation().getTargetContext();
        SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        // Set SharedPreferences data
        preferencesEditor.putBoolean(context.getString(R.string.preference_firstStart), isIntrosActive());
        preferencesEditor.putBoolean(context.getString(R.string.first_project_pref), isIntrosActive());
        preferencesEditor.putBoolean(context.getString(R.string.clean_data), isCleanData());
        preferencesEditor.commit();
        // Launch activity
        mActivityTestRule.launchActivity(new Intent());

    }

    public boolean isCleanData() {
        return false;
    }

    protected boolean isIntrosActive() {
        return true;
    }
}
