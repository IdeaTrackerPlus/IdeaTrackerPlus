package manparvesh.ideatrackerplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

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

        addSlide(AppIntroFragment.newInstance(getString(R.string.welcome), getString(R.string.welcome_content), R.drawable.launcher, ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.simple), getString(R.string.simple_content), R.drawable.fast_idea, ContextCompat.getColor(this, R.color.md_blue_400)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.organized), getString(R.string.organized_content), R.drawable.focus_projects, ContextCompat.getColor(this, R.color.md_amber_400)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.things_done), getString(R.string.things_done_content), R.drawable.multi_check, ContextCompat.getColor(this, R.color.md_red_400)));

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
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
