package manparvesh.ideatrackerplus.ideamenu;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import manparvesh.ideatrackerplus.MainActivity;

/**
 * Created by Nicklos on 10/12/2016.
 */
public class IdeaMenuItemClickListener implements View.OnClickListener{

    private int mActionId;
    private AnimationSet explodeAnimation;

    public IdeaMenuItemClickListener(int actionId) {
        super();
        mActionId = actionId;

        explodeAnimation = new AnimationSet(true);
        explodeAnimation.setInterpolator(new DecelerateInterpolator());
        explodeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                MainActivity.getInstance().rebootIdeaMenuItems();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        }); //Hide items after animation

        //Scale animation
        Animation scale = new ScaleAnimation(
                1.2f, 3f, // Start and end values for the X axis scaling
                1.2f, 3f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        scale.setDuration(300);
        explodeAnimation.addAnimation(scale);
        //Alpha animation
        Animation alpha = new AlphaAnimation(1.0f, 0.2f);
        alpha.setDuration(350);
        explodeAnimation.addAnimation(alpha);
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(explodeAnimation);
        MainActivity main = MainActivity.getInstance();
        switch (mActionId) {
            case 4:
                main.startVoiceRecognitionActivity();
                break;

            default:
                main.newIdeaDialog(mActionId);
                break;
        }
    }
}
