package appbox.ideastracker.ideamenu;

import android.view.DragEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import appbox.ideastracker.MainActivity;

/**
 * Created by Nicklos on 03/10/2016.
 */
public class IdeaMenuItemListener implements View.OnDragListener {

    private int mActionId;

    private static boolean ready;
    private static boolean dragging;
    private static boolean dropping;

    public IdeaMenuItemListener(int actionId) {
        super();
        mActionId = actionId;
    }

    public static void setReady(boolean b) {
        ready = b;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {

        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator());
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                MainActivity.getInstance().rebootIdeaMenuItems();
                dropping = false;
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
        set.addAnimation(scale);
        //Alpha animation
        Animation alpha = new AlphaAnimation(1.0f, 0.2f);
        alpha.setDuration(350);
        set.addAnimation(alpha);

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                dragging = true;
                break;

            case DragEvent.ACTION_DRAG_ENTERED:
                if (ready) {
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
                }
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                if (ready) {
                    v.clearAnimation();
                }
                break;

            case DragEvent.ACTION_DRAG_LOCATION:
                break;

            case DragEvent.ACTION_DROP:
                if (ready) {
                    dropping = true;
                    v.startAnimation(set);

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
                break;

            case DragEvent.ACTION_DRAG_ENDED:
                if (dragging && !dropping) { // avoid doing this as many times as there is listeners
                    final View droppedView = (View) event.getLocalState();
                    droppedView.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.getInstance().rebootIdeaMenuItems();
                        }
                    });
                    dragging = false;
                }
                break;

            default:
                break;
        }
        return true;
    }
}
