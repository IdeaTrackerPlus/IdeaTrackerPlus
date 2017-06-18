package manparvesh.ideatrackerplus.customviews;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.mobiwise.materialintro.MaterialIntroConfiguration;
import co.mobiwise.materialintro.animation.AnimationFactory;
import co.mobiwise.materialintro.animation.AnimationListener;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.prefs.PreferencesManager;
import co.mobiwise.materialintro.shape.Circle;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.target.Target;
import co.mobiwise.materialintro.target.ViewTarget;
import co.mobiwise.materialintro.utils.Constants;
import co.mobiwise.materialintro.utils.Utils;

public class MyMaterialIntroView extends RelativeLayout {
    private int maskColor;
    private long delayMillis;
    private boolean isReady;
    private boolean isFadeAnimationEnabled;
    private long fadeAnimationDuration;
    private Circle circleShape;
    private Focus focusType;
    private FocusGravity focusGravity;
    private Target targetView;
    private Paint eraser;
    private Handler handler;
    private Bitmap bitmap;
    private Canvas canvas;
    private int padding;
    private int width;
    private int height;
    private boolean dismissOnTouch;
    private View infoView;
    private TextView textViewInfo;
    private int colorTextViewInfo;
    private boolean isInfoEnabled;
    private View dotView;
    private boolean isDotViewEnabled;
    private ImageView imageViewIcon;
    private boolean isImageViewEnabled;
    private PreferencesManager preferencesManager;
    private String MyMaterialIntroViewId;
    private boolean isLayoutCompleted;
    private MaterialIntroListener materialIntroListener;
    private boolean isPerformClick;

    public MyMaterialIntroView(Context context) {
        super(context);
        this.init(context);
    }

    public MyMaterialIntroView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public MyMaterialIntroView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @TargetApi(21)
    public MyMaterialIntroView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    private void init(Context context) {
        this.setWillNotDraw(false);
        this.setVisibility(INVISIBLE);
        this.maskColor = Constants.DEFAULT_MASK_COLOR;
        this.delayMillis = Constants.DEFAULT_DELAY_MILLIS;
        this.fadeAnimationDuration = Constants.DEFAULT_FADE_DURATION;
        this.padding = Constants.DEFAULT_TARGET_PADDING;
        this.colorTextViewInfo = Constants.DEFAULT_COLOR_TEXTVIEW_INFO;
        this.focusType = Focus.ALL;
        this.focusGravity = FocusGravity.CENTER;
        this.isReady = false;
        this.isFadeAnimationEnabled = true;
        this.dismissOnTouch = false;
        this.isLayoutCompleted = false;
        this.isInfoEnabled = false;
        this.isDotViewEnabled = false;
        this.isPerformClick = false;
        this.isImageViewEnabled = true;
        this.handler = new Handler();
        this.preferencesManager = new PreferencesManager(context);
        this.eraser = new Paint();
        this.eraser.setColor(-1);
        this.eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.eraser.setFlags(1);
        View layoutInfo = LayoutInflater.from(this.getContext()).inflate(co.mobiwise.materialintro.R.layout.material_intro_card, (ViewGroup)null);
        this.infoView = layoutInfo.findViewById(co.mobiwise.materialintro.R.id.info_layout);
        this.textViewInfo = (TextView)layoutInfo.findViewById(co.mobiwise.materialintro.R.id.textview_info);
        this.textViewInfo.setTextColor(this.colorTextViewInfo);
        this.imageViewIcon = (ImageView)layoutInfo.findViewById(co.mobiwise.materialintro.R.id.imageview_icon);
        this.dotView = LayoutInflater.from(this.getContext()).inflate(co.mobiwise.materialintro.R.layout.dotview, (ViewGroup)null);
        this.dotView.measure(0, 0);
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                MyMaterialIntroView.this.circleShape.reCalculateAll();
                if(MyMaterialIntroView.this.circleShape != null && MyMaterialIntroView.this.circleShape.getPoint().y != 0 && !MyMaterialIntroView.this.isLayoutCompleted) {
                    if(MyMaterialIntroView.this.isInfoEnabled) {
                        MyMaterialIntroView.this.setInfoLayout();
                    }

                    if(MyMaterialIntroView.this.isDotViewEnabled) {
                        MyMaterialIntroView.this.setDotViewLayout();
                    }

                    MyMaterialIntroView.removeOnGlobalLayoutListener(MyMaterialIntroView.this, this);
                }

            }
        });
    }

    @TargetApi(16)
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if(Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.width = this.getMeasuredWidth();
        this.height = this.getMeasuredHeight();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(this.isReady) {
            if(this.bitmap == null || canvas == null) {
                if(this.bitmap != null) {
                    this.bitmap.recycle();
                }

                this.bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
                this.canvas = new Canvas(this.bitmap);
            }

            this.canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            this.canvas.drawColor(this.maskColor);
            this.circleShape.draw(this.canvas, this.eraser, this.padding);
            canvas.drawBitmap(this.bitmap, 0.0F, 0.0F, (Paint)null);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        float xT = event.getX();
        float yT = event.getY();
        int xV = this.circleShape.getPoint().x;
        int yV = this.circleShape.getPoint().y;
        int radius = this.circleShape.getRadius();
        double dx = Math.pow((double)(xT - (float)xV), 2.0D);
        double dy = Math.pow((double)(yT - (float)yV), 2.0D);
        boolean isTouchOnFocus = dx + dy <= Math.pow((double)radius, 2.0D);
        switch(event.getAction()) {
            case 0:
                if(isTouchOnFocus && this.isPerformClick) {
                    this.targetView.getView().setPressed(true);
                    this.targetView.getView().invalidate();
                }

                return true;
            case 1:
                if(isTouchOnFocus || this.dismissOnTouch) {
                    this.dismiss();
                }

                if(isTouchOnFocus && this.isPerformClick) {
                    this.targetView.getView().performClick();
                    this.targetView.getView().setPressed(true);
                    this.targetView.getView().invalidate();
                    this.targetView.getView().setPressed(false);
                    this.targetView.getView().invalidate();
                }

                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void show(Activity activity) {
        if(!this.preferencesManager.isDisplayed(this.MyMaterialIntroViewId)) {
            ((ViewGroup)activity.getWindow().getDecorView()).addView(this);
            this.setReady(true);
            this.handler.postDelayed(new Runnable() {
                public void run() {
                    if(MyMaterialIntroView.this.isFadeAnimationEnabled) {
                        AnimationFactory.animateFadeIn(MyMaterialIntroView.this, MyMaterialIntroView.this.fadeAnimationDuration, new AnimationListener.OnAnimationStartListener() {
                            public void onAnimationStart() {
                                MyMaterialIntroView.this.setVisibility(VISIBLE);
                            }
                        });
                    } else {
                        MyMaterialIntroView.this.setVisibility(VISIBLE);
                    }

                }
            }, this.delayMillis);
        }
    }

    public void dismiss() {
        this.preferencesManager.setDisplayed(this.MyMaterialIntroViewId);
        AnimationFactory.animateFadeOut(this, this.fadeAnimationDuration, new AnimationListener.OnAnimationEndListener() {
            public void onAnimationEnd() {
                MyMaterialIntroView.this.setVisibility(GONE);
                MyMaterialIntroView.this.removeMaterialView();
                if(MyMaterialIntroView.this.materialIntroListener != null) {
                    MyMaterialIntroView.this.materialIntroListener.onUserClicked(MyMaterialIntroView.this.MyMaterialIntroViewId);
                }

            }
        });
    }

    private void removeMaterialView() {
        if(this.getParent() != null) {
            ((ViewGroup)this.getParent()).removeView(this);
        }

    }

    private void setInfoLayout() {
        this.handler.post(new Runnable() {
            public void run() {
                MyMaterialIntroView.this.isLayoutCompleted = true;
                if(MyMaterialIntroView.this.infoView.getParent() != null) {
                    ((ViewGroup)MyMaterialIntroView.this.infoView.getParent()).removeView(MyMaterialIntroView.this.infoView);
                }

                LayoutParams infoDialogParams = new LayoutParams(-1, -1);
                if(MyMaterialIntroView.this.circleShape.getPoint().y < MyMaterialIntroView.this.height / 2) {
                    ((RelativeLayout)MyMaterialIntroView.this.infoView).setGravity(48);
                    infoDialogParams.setMargins(0, MyMaterialIntroView.this.circleShape.getPoint().y + MyMaterialIntroView.this.circleShape.getRadius(), 0, 0);
                } else {
                    ((RelativeLayout)MyMaterialIntroView.this.infoView).setGravity(80);
                    infoDialogParams.setMargins(0, 0, 0, MyMaterialIntroView.this.height - (MyMaterialIntroView.this.circleShape.getPoint().y + MyMaterialIntroView.this.circleShape.getRadius()) + 2 * MyMaterialIntroView.this.circleShape.getRadius());
                }

                MyMaterialIntroView.this.infoView.setLayoutParams(infoDialogParams);
                MyMaterialIntroView.this.infoView.postInvalidate();
                MyMaterialIntroView.this.addView(MyMaterialIntroView.this.infoView);
                if(!MyMaterialIntroView.this.isImageViewEnabled) {
                    MyMaterialIntroView.this.imageViewIcon.setVisibility(GONE);
                }

                MyMaterialIntroView.this.infoView.setVisibility(VISIBLE);
            }
        });
    }

    private void setDotViewLayout() {
        this.handler.post(new Runnable() {
            public void run() {
                if(MyMaterialIntroView.this.dotView.getParent() != null) {
                    ((ViewGroup)MyMaterialIntroView.this.dotView.getParent()).removeView(MyMaterialIntroView.this.dotView);
                }

                LayoutParams dotViewLayoutParams = new LayoutParams(-1, -1);
                dotViewLayoutParams.height = Utils.dpToPx(Constants.DEFAULT_DOT_SIZE);
                dotViewLayoutParams.width = Utils.dpToPx(Constants.DEFAULT_DOT_SIZE);
                dotViewLayoutParams.setMargins(MyMaterialIntroView.this.circleShape.getPoint().x - dotViewLayoutParams.width / 2, MyMaterialIntroView.this.circleShape.getPoint().y - dotViewLayoutParams.height / 2, 0, 0);
                MyMaterialIntroView.this.dotView.setLayoutParams(dotViewLayoutParams);
                MyMaterialIntroView.this.dotView.postInvalidate();
                MyMaterialIntroView.this.addView(MyMaterialIntroView.this.dotView);
                MyMaterialIntroView.this.dotView.setVisibility(VISIBLE);
                AnimationFactory.performAnimation(MyMaterialIntroView.this.dotView);
            }
        });
    }

    private void setMaskColor(int maskColor) {
        this.maskColor = maskColor;
    }

    private void setDelay(int delayMillis) {
        this.delayMillis = (long)delayMillis;
    }

    private void enableFadeAnimation(boolean isFadeAnimationEnabled) {
        this.isFadeAnimationEnabled = isFadeAnimationEnabled;
    }

    private void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    private void setTarget(Target target) {
        this.targetView = target;
    }

    private void setFocusType(Focus focusType) {
        this.focusType = focusType;
    }

    private void setCircle(Circle circleShape) {
        this.circleShape = circleShape;
    }

    private void setPadding(int padding) {
        this.padding = padding;
    }

    private void setDismissOnTouch(boolean dismissOnTouch) {
        this.dismissOnTouch = dismissOnTouch;
    }

    private void setFocusGravity(FocusGravity focusGravity) {
        this.focusGravity = focusGravity;
    }

    private void setColorTextViewInfo(int colorTextViewInfo) {
        this.colorTextViewInfo = colorTextViewInfo;
        this.textViewInfo.setTextColor(this.colorTextViewInfo);
    }

    private void setTextViewInfo(String textViewInfo) {
        this.textViewInfo.setText(textViewInfo);
    }

    private void setTextViewInfoSize(int textViewInfoSize) {
        this.textViewInfo.setTextSize(2, (float)textViewInfoSize);
    }

    private void enableInfoDialog(boolean isInfoEnabled) {
        this.isInfoEnabled = isInfoEnabled;
    }

    private void enableImageViewIcon(boolean isImageViewEnabled) {
        this.isImageViewEnabled = isImageViewEnabled;
    }

    private void enableDotView(boolean isDotViewEnabled) {
        this.isDotViewEnabled = isDotViewEnabled;
    }

    public void setConfiguration(MaterialIntroConfiguration configuration) {
        if(configuration != null) {
            this.maskColor = configuration.getMaskColor();
            this.delayMillis = configuration.getDelayMillis();
            this.isFadeAnimationEnabled = configuration.isFadeAnimationEnabled();
            this.colorTextViewInfo = configuration.getColorTextViewInfo();
            this.isDotViewEnabled = configuration.isDotViewEnabled();
            this.dismissOnTouch = configuration.isDismissOnTouch();
            this.colorTextViewInfo = configuration.getColorTextViewInfo();
            this.focusType = configuration.getFocusType();
            this.focusGravity = configuration.getFocusGravity();
        }

    }

    private void setUsageId(String MyMaterialIntroViewId) {
        this.MyMaterialIntroViewId = MyMaterialIntroViewId;
    }

    private void setListener(MaterialIntroListener materialIntroListener) {
        this.materialIntroListener = materialIntroListener;
    }

    private void setPerformClick(boolean isPerformClick) {
        this.isPerformClick = isPerformClick;
    }

    public static class Builder {
        private MyMaterialIntroView MyMaterialIntroView;
        private Activity activity;
        private Focus focusType;

        public Builder(Activity activity) {
            this.focusType = Focus.MINIMUM;
            this.activity = activity;
            this.MyMaterialIntroView = new MyMaterialIntroView(activity);
        }

        public MyMaterialIntroView.Builder setMaskColor(int maskColor) {
            this.MyMaterialIntroView.setMaskColor(maskColor);
            return this;
        }

        public MyMaterialIntroView.Builder setDelayMillis(int delayMillis) {
            this.MyMaterialIntroView.setDelay(delayMillis);
            return this;
        }

        public MyMaterialIntroView.Builder enableFadeAnimation(boolean isFadeAnimationEnabled) {
            this.MyMaterialIntroView.enableFadeAnimation(isFadeAnimationEnabled);
            return this;
        }

        public MyMaterialIntroView.Builder setFocusType(Focus focusType) {
            this.MyMaterialIntroView.setFocusType(focusType);
            return this;
        }

        public MyMaterialIntroView.Builder setFocusGravity(FocusGravity focusGravity) {
            this.MyMaterialIntroView.setFocusGravity(focusGravity);
            return this;
        }

        public MyMaterialIntroView.Builder setTarget(View view) {
            this.MyMaterialIntroView.setTarget(new ViewTarget(view));
            return this;
        }

        public MyMaterialIntroView.Builder setTargetPadding(int padding) {
            this.MyMaterialIntroView.setPadding(padding);
            return this;
        }

        public MyMaterialIntroView.Builder setTextColor(int textColor) {
            this.MyMaterialIntroView.setColorTextViewInfo(textColor);
            return this;
        }

        public MyMaterialIntroView.Builder setInfoText(String infoText) {
            this.MyMaterialIntroView.enableInfoDialog(true);
            this.MyMaterialIntroView.setTextViewInfo(infoText);
            return this;
        }

        public MyMaterialIntroView.Builder setInfoTextSize(int textSize) {
            this.MyMaterialIntroView.setTextViewInfoSize(textSize);
            return this;
        }

        public MyMaterialIntroView.Builder dismissOnTouch(boolean dismissOnTouch) {
            this.MyMaterialIntroView.setDismissOnTouch(dismissOnTouch);
            return this;
        }

        public MyMaterialIntroView.Builder setUsageId(String MyMaterialIntroViewId) {
            this.MyMaterialIntroView.setUsageId(MyMaterialIntroViewId);
            return this;
        }

        public MyMaterialIntroView.Builder enableDotAnimation(boolean isDotAnimationEnabled) {
            this.MyMaterialIntroView.enableDotView(isDotAnimationEnabled);
            return this;
        }

        public MyMaterialIntroView.Builder enableIcon(boolean isImageViewIconEnabled) {
            this.MyMaterialIntroView.enableImageViewIcon(isImageViewIconEnabled);
            return this;
        }

        public MyMaterialIntroView.Builder setConfiguration(MaterialIntroConfiguration configuration) {
            this.MyMaterialIntroView.setConfiguration(configuration);
            return this;
        }

        public MyMaterialIntroView.Builder setListener(MaterialIntroListener materialIntroListener) {
            this.MyMaterialIntroView.setListener(materialIntroListener);
            return this;
        }

        public MyMaterialIntroView.Builder performClick(boolean isPerformClick) {
            this.MyMaterialIntroView.setPerformClick(isPerformClick);
            return this;
        }

        public MyMaterialIntroView build() {
            Circle circle = new Circle(this.MyMaterialIntroView.targetView, this.MyMaterialIntroView.focusType, this.MyMaterialIntroView.focusGravity, this.MyMaterialIntroView.padding);
            this.MyMaterialIntroView.setCircle(circle);
            return this.MyMaterialIntroView;
        }

        public MyMaterialIntroView show() {
            this.build().show(this.activity);
            return this.MyMaterialIntroView;
        }
    }
}

