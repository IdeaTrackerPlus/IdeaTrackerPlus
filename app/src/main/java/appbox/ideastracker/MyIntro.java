package appbox.ideastracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class MyIntro extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Welcome", "learn how to use the app", R.drawable.disk, getResources().getColor(R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Welcome", "learn how to use the app", R.drawable.disk, getResources().getColor(R.color.md_deep_orange_300)));
        addSlide(AppIntroFragment.newInstance("Welcome", "learn how to use the app", R.drawable.disk, getResources().getColor(R.color.md_red_300)));

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
