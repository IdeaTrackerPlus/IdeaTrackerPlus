package appbox.ideastracker.ideamenu;

import android.view.DragEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import appbox.ideastracker.MainActivity;

/**
 * Created by Nicklos on 03/10/2016.
 */
public class IdeaMenuItemListener implements View.OnDragListener {

    private static ImageView mScreenDim;
    private int mActionId;

    public IdeaMenuItemListener(int actionId) {
        super();
        mActionId = actionId;
    }

    public static void setScreenDim(ImageView screenDim) {
        mScreenDim = screenDim;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {

        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator());
        //Scale animation
        Animation scale = new ScaleAnimation(
                1.2f, 3f, // Start and end values for the X axis scaling
                1.2f, 3f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        scale.setDuration(500);
        set.addAnimation(scale);
        //Alpha animation
        Animation alpha = new AlphaAnimation(1.0f, 0.3f);
        alpha.setDuration(350);
        set.addAnimation(alpha);

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;

            case DragEvent.ACTION_DRAG_ENTERED:
                //Vibration feedback
                MainActivity.getInstance().feedbackVibration();

                //Scale animation
                Animation anim = new ScaleAnimation(
                        1f, 1.2f, // Start and end values for the X axis scaling
                        1f, 1.2f, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                anim.setDuration(350);
                anim.setInterpolator(new BounceInterpolator());
                anim.setFillAfter(true);
                v.startAnimation(anim);
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                v.clearAnimation();
                break;

            case DragEvent.ACTION_DRAG_LOCATION:
                break;

            case DragEvent.ACTION_DRAG_ENDED:
                mScreenDim.setAlpha(0f);
                final View droppedView = (View) event.getLocalState();
                droppedView.post(new Runnable() {
                    @Override
                    public void run() {
                        droppedView.setVisibility(View.VISIBLE);
                    }
                });
                break;

            case DragEvent.ACTION_DROP:
                v.startAnimation(set);

                MainActivity main = MainActivity.getInstance();
                switch (mActionId) {
                    default:
                        main.newIdeaDialog(mActionId);
                }

                break;

            default:
                break;
        }
        return true;
    }
}
