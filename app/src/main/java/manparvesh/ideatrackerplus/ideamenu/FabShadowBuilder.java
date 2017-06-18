package manparvesh.ideatrackerplus.ideamenu;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

public class FabShadowBuilder extends View.DragShadowBuilder {

    private Point mScaleFactor;

    public FabShadowBuilder(View v) {
        super(v);
    }

    // Defines a callback that sends the drag shadow dimensions and touch point back to the
    // system.
    @Override
    public void onProvideShadowMetrics(Point size, Point touch) {
        int width;
        int height;

        width = (int) (getView().getWidth() * 1.2f);
        height = (int) (getView().getHeight() * 1.2f);

        size.set(width, height);
        // Sets size parameter to member that will be used for scaling shadow image.
        mScaleFactor = size;

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(width / 2, height / 2 + 50);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {

        // Draws the ColorDrawable in the Canvas passed in from the system.
        canvas.scale(mScaleFactor.x / (float) getView().getWidth(), mScaleFactor.y / (float) getView().getHeight());
        getView().draw(canvas);
    }

}
