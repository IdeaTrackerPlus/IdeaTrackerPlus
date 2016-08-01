package appbox.ideastracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * App introduction activity, showing a short slide show
 * and describing the app
 */
public class MyIntro extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Welcome to Ideas Tracker", "All your ideas in one place", R.drawable.launcher, getResources().getColor(R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Keep it simple", "Capture ideas faster with little information recquired", R.drawable.fast_idea, getResources().getColor(R.color.md_blue_400)));
        addSlide(AppIntroFragment.newInstance("Stay organized", "Class your ideas in projects to stay on focus", R.drawable.focus_projects, getResources().getColor(R.color.md_amber_400)));
        addSlide(AppIntroFragment.newInstance("Get things done", "Your ideas become tasks with different priorities", R.drawable.multi_check, getResources().getColor(R.color.md_red_400)));

        showStatusBar(false);


    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        goToMainActivity();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        goToMainActivity();
    }

    private void goToMainActivity() {
        Intent i = new Intent(MyIntro.this, MainActivity.class);
        startActivity(i);
    }
}
