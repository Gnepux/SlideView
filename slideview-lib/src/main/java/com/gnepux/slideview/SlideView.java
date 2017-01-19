package com.gnepux.slideview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Gnepux on 16/12/29.
 */
public class SlideView extends ViewGroup {

    private static final String TAG = "SlideView";

    // SlideIcon在父view中的水平偏移量
    private static int MARGIN_HORIZONTAL = 0;

    // SlideIcon在父view中的水平便宜量
    private static int MARGIN_VERTICAL = 0;

    // SlideIcon实例
    private SlideIcon mSlideIcon;

    // SlideIcon的X坐标
    private int mIconX = 0;

    // SlideIcon拖动时的X轴偏移量
    private int mDistanceX = 0;

    // 监听
    private MotionListener mMotionListener = null;

    // 背景文字的Paint
    private Paint mBgTextPaint;

    // 背景文字的测量类
    private Paint.FontMetrics mBgTextFontMetrics;

    // 拖动过的部分的Paint
    private Paint mSecondaryPaint;

    // attr: 最小高度
    private int mMinHeight;

    // attr: 背景图
    private int mBgResId;

    // attr: 背景文字
    private String mBgText = "";

    // attr: 拖动完成后的背景文字
    private String mBgTextComplete = "";

    // attr: 背景文字的颜色
    private int mBgTextColor;

    // attr: 背景文字的大小
    private float mBgTextSize;

    // attr: Icon背景图
    private int mIconResId;

    // attr: Icon上显示的文字
    private String mIconText = "";

    // attr: Icon上文字的颜色
    private int mIconTextColor;

    // attr: Icon上文字的大小
    private float mIconTextSize;

    // attr: Icon的宽度占总长的比例
    private float mIconRatio;

    // attr: 滑动到一半松手时是否回到初始状态
    private boolean mResetWhenNotFull;

    // attr: 拖动结束后是否可以再次操作
    private boolean mEnableWhenFull;

    // attr: 拖动过的部分的颜色
    private int mSecondaryColor;

    // attr: 背景文字渐变色1
    private int mGradientColor1;

    // attr: 背景文字渐变色1
    private int mGradientColor2;

    // attr: 背景文字渐变色1
    private int mGradientColor3;

    private OnSlideListener mListener = null;

    private CustTextView mTextView;

    // 控件滑动的回调
    public interface OnSlideListener {
        /**
         * 滑动完成的回调
         */
        void onSlideSuccess();
    }

    public SlideView(Context context) {
        this(context, null);
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlideView, 0, 0);
        try {
            mResetWhenNotFull = a.getBoolean(R.styleable.SlideView_reset_not_full, true);
            mEnableWhenFull = a.getBoolean(R.styleable.SlideView_enable_when_full, false);

            mBgResId = a.getResourceId(R.styleable.SlideView_bg_drawable, android.R.drawable.btn_default);
            mIconResId = a.getResourceId(R.styleable.SlideView_icon_drawable, android.R.drawable.btn_default);
            mMinHeight = a.getDimensionPixelSize(R.styleable.SlideView_min_height, 240);

            mIconText = a.getString(R.styleable.SlideView_icon_text);
            mIconTextColor = a.getColor(R.styleable.SlideView_icon_text_color, Color.WHITE);
            mIconTextSize = a.getDimensionPixelSize(R.styleable.SlideView_icon_text_size, 44);
            mIconRatio = a.getFloat(R.styleable.SlideView_icon_ratio, 0.2f);

            mBgText = a.getString(R.styleable.SlideView_bg_text);
            mBgTextComplete = a.getString(R.styleable.SlideView_bg_text_complete);
            mBgTextColor = a.getColor(R.styleable.SlideView_bg_text_color, Color.BLACK);
            mBgTextSize = a.getDimensionPixelSize(R.styleable.SlideView_bg_text_size, 44);

            mSecondaryColor = a.getColor(R.styleable.SlideView_secondary_color, Color.TRANSPARENT);

            mGradientColor1 = a.getColor(R.styleable.SlideView_gradient_color1, Color.WHITE);
            mGradientColor2 = a.getColor(R.styleable.SlideView_gradient_color2, Color.WHITE);
            mGradientColor3 = a.getColor(R.styleable.SlideView_gradient_color3, Color.WHITE);

        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        // 设置背景文字Paint
        mBgTextPaint = new Paint();
        mBgTextPaint.setTextAlign(Paint.Align.CENTER);
        mBgTextPaint.setColor(mBgTextColor);
        mBgTextPaint.setTextSize(mBgTextSize);

        // 获取背景文字测量类
        mBgTextFontMetrics = mBgTextPaint.getFontMetrics();

        // 设置拖动过的部分的Paint
        mSecondaryPaint = new Paint();
        mSecondaryPaint.setColor(mSecondaryColor);

        // 设置背景图
        setBackgroundResource(mBgResId);

        mTextView = new CustTextView(getContext());
        mTextView.setText(mBgText);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mTextView.setLayoutParams(layoutParams);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(Color.WHITE);
        mTextView.setTextSize(20);
        addView(mTextView);

        // 创建一个SlideIcon,设置LayoutParams并添加到ViewGroup中
        mSlideIcon = new SlideIcon(getContext());
        /**
         * Important:
         * 此处需要设置IconView的LayoutParams,这样才能在布局文件中正确通过wrap_content设置布局
         */
        mSlideIcon.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        addView(mSlideIcon);

        // 设置监听
        mMotionListener = new MotionListener() {
            @Override
            public void onActionMove(int distanceX) {
                // SlideIcon拖动时根据X轴偏移量重新计算位置并绘制
                if (mSlideIcon != null) {
                    mDistanceX = distanceX;
                    requestLayout();
                    invalidate();
                }
            }

            @Override
            public void onActionUp(int x) {
                mIconX = x;
                mDistanceX = 0;
                if (mIconX + mSlideIcon.getMeasuredWidth() < getMeasuredWidth()) {  // SlideIcon为拖动到底
                    if (mResetWhenNotFull) {  // 重置
                        mIconX = 0;
                        mSlideIcon.resetIcon();
                        requestLayout();
                        invalidate();
                    }
                } else {  // SlideIcon拖动到底
                    if (!mEnableWhenFull) {  // 松开后是否可以继续操作
                        mSlideIcon.setEnable(false);
                    }
                    if (mListener != null) {  // 触发回调
                        mListener.onSlideSuccess();
                    }
                }
            }
        };

        mSlideIcon.setListener(mMotionListener);
    }

    public void addSlideListener(OnSlideListener listener) {
        this.mListener = listener;
    }

    public void reset() {
        mIconX = 0;
        mDistanceX = 0;
        if (mSlideIcon != null) {
            mSlideIcon.resetIcon();
        }
        requestLayout();
        invalidate();
    }

    public void enableWhenFull(boolean enable) {
        this.mEnableWhenFull = enable;
    }

    public boolean isEnableWhenFull() {
        return mEnableWhenFull;
    }

    public void resetWhenNotFull(boolean reset) {
        this.mResetWhenNotFull = reset;
    }

    public boolean isResetWhenNotFull() {
        return mResetWhenNotFull;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 计算子View的尺寸
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        // 因为只有一个子View,直接取出来
        mTextView = (CustTextView) getChildAt(0);
        mSlideIcon = (SlideIcon) getChildAt(1);
        // 根据SlideIcon的高度设置ViewGroup的高度
        setMeasuredDimension(widthMeasureSpec, mSlideIcon.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mIconX + mDistanceX <= 0) { // 控制SlideIcon不能超过左边界限
            mSlideIcon.layout(MARGIN_HORIZONTAL, MARGIN_VERTICAL,
                    MARGIN_HORIZONTAL + mSlideIcon.getMeasuredWidth(),
                    mSlideIcon.getMeasuredHeight() - MARGIN_VERTICAL);
        } else if (mIconX + mDistanceX + mSlideIcon.getMeasuredWidth() >= getMeasuredWidth()) { // 控制SlideIcon不能超过左边界限
            mSlideIcon.layout(getMeasuredWidth() - mSlideIcon.getMeasuredWidth() - MARGIN_HORIZONTAL, MARGIN_VERTICAL,
                    getMeasuredWidth() - MARGIN_HORIZONTAL,
                    mSlideIcon.getMeasuredHeight() - MARGIN_VERTICAL);
        } else {  // 根据SlideIcon的X坐标和偏移量计算位置
            mSlideIcon.layout(mIconX + mDistanceX + MARGIN_HORIZONTAL, MARGIN_VERTICAL,
                    mIconX + mDistanceX + mSlideIcon.getMeasuredWidth() + MARGIN_HORIZONTAL,
                    mSlideIcon.getMeasuredHeight() - MARGIN_VERTICAL);
        }

        int l = getMeasuredWidth()/2 - mTextView.getMeasuredWidth()/2;
        int t = getMeasuredHeight()/2 - mTextView.getMeasuredHeight()/2;
        int r = getMeasuredWidth()/2 + mTextView.getMeasuredWidth()/2;
        int b = getMeasuredHeight()/2 + mTextView.getMeasuredHeight()/2;
        mTextView.layout(l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制已拖动过的区域
        if (mIconX + mDistanceX > 0) {

            RectF rectF = new RectF(MARGIN_HORIZONTAL, MARGIN_VERTICAL, mIconX + mDistanceX + MARGIN_HORIZONTAL + mSlideIcon.getMeasuredHeight() / 2,
                    getMeasuredHeight() - MARGIN_VERTICAL);
            canvas.drawRoundRect(rectF, 12, 12, mSecondaryPaint);
//            canvas.drawRect(MARGIN_HORIZONTAL, MARGIN_VERTICAL, mIconX + mDistanceX + MARGIN_HORIZONTAL + mSlideIcon.getMeasuredHeight() / 2,
//                    getMeasuredHeight() - MARGIN_VERTICAL, mSecondaryPaint);
        }

        // 绘制背景文字
//        float baselineY = (getMeasuredHeight() - mBgTextFontMetrics.top - mBgTextFontMetrics.bottom) / 2;
        if (mIconX + mDistanceX + mSlideIcon.getMeasuredWidth() >= getMeasuredWidth()) {
//            canvas.drawText(mBgTextComplete == null ? "":mBgTextComplete, getMeasuredWidth() / 2, baselineY, mBgTextPaint);
            mTextView.setText(mBgTextComplete);
        } else {
//            canvas.drawText(mBgText == null ? "":mBgText, getMeasuredWidth() / 2, baselineY, mBgTextPaint);
            mTextView.setText(mBgText);
        }
    }

    /**
     * 可拖动的View
     */
    private class SlideIcon extends View {
        // 用来控制触摸事件是否可用
        private boolean mEnable;

        // 提示文字的Paint
        private Paint mTextPaint = null;

        // 提示文字的字体测量类
        private Paint.FontMetrics mFontMetrics;

        // 回调
        private MotionListener listener = null;

        // 手指按下时SlideIcon的X坐标
        private float mDownX = 0;

        // SlideIcon在非拖动状态下的X坐标
        private float mX = 0;

        // SliedIcon在拖动状态下X轴的偏移量
        private float mDistanceX = 0;

        public SlideIcon(Context context) {
            this(context, null);
        }

        public SlideIcon(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public void setListener(MotionListener listener) {
            this.listener = listener;
        }

        public void setEnable(boolean enable) {
            this.mEnable = enable;
        }

        public boolean getEnable() {
            return mEnable;
        }

        private void init() {
            // 设置文字Paint
            mTextPaint = new Paint();
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setColor(mIconTextColor);
            mTextPaint.setTextSize(mIconTextSize);

            // 获取字体测量类
            mFontMetrics = mTextPaint.getFontMetrics();

            // 设置背景图
            setBackgroundResource(mIconResId);

            // 设置可用
            mEnable = true;
        }

        /**
         * 重置SlideIcon
         */
        public void resetIcon() {
            mDownX = 0;
            mDistanceX = 0;
            mX = 0;
            mEnable = true;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // 宽度和宽Mode
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);

            // 高度和高Mode
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);

            switch (heightMode) {
                case MeasureSpec.AT_MOST:   // layout_height为"wrap_content"时显示最小高度
                    setMeasuredDimension(MeasureSpec.makeMeasureSpec((int)(widthSize * mIconRatio), widthMode),
                            MeasureSpec.makeMeasureSpec(mMinHeight, heightMode));
                    break;
                default:    // layout_height为"match_parent"或指定具体高度时显示默认高度
                    setMeasuredDimension(MeasureSpec.makeMeasureSpec((int)(widthSize * mIconRatio), widthMode),
                            MeasureSpec.makeMeasureSpec(heightSize, heightMode));
                    break;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // 获取文字baseline的Y坐标
            float baselineY = (getMeasuredHeight() - mFontMetrics.top - mFontMetrics.bottom) / 2;
            // 绘制文字
            canvas.drawText(mIconText == null ? "":mIconText, getMeasuredWidth() / 2, baselineY, mTextPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (mEnable) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 记录手指按下时SlideIcon的X坐标
                    mDownX = event.getRawX();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // 设置手指松开时SlideIcon的X坐标
                    mDownX = 0;
                    mX = mX + mDistanceX;
                    mDistanceX = 0;
                    // 触发松开回调并传入当前SlideIcon的X坐标
                    if (listener != null) {
                        listener.onActionUp((int) mX);
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    // 记录SlideIcon在X轴上的拖动距离
                    mDistanceX = event.getRawX() - mDownX;
                    // 触发拖动回调并传入当前SlideIcon的拖动距离
                    if (listener != null) {
                        listener.onActionMove((int) mDistanceX);
                    }
                    return true;
                }
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 触摸事件的回调
     */
    private interface MotionListener {
        /**
         * 拖动时的回调
         * @param distanceX SlideIcon的X轴偏移量
         */
        void onActionMove(int distanceX);

        /**
         * 松开时的回调
         * @param x SlideIcon的X坐标
         */
        void onActionUp(int x);
    }

    private class CustTextView extends TextView {
        private int mViewWith;
        private int mTranleate;
        private Paint mPaint;
        private LinearGradient mLinearGradient;
        private Matrix mMatrix;

        public CustTextView(Context context) {
            super(context, null);
        }

        public CustTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (mViewWith == 0) {
                mViewWith = getMeasuredWidth();
                if (mViewWith > 0) {
                    mPaint = getPaint();
                    /**
                     * LinearGradient第一个和第二个参数表示颜色渐变的起点 第三、第四表示颜色渐变的终点 第五个参数表示渐变的颜色
                     * 第六个参数可以为空,表示坐标,值为0-1 new float[] {0.25f, 0.5f, 0.75f, 1 }
                     *如果这是空的，颜色均匀分布，沿梯度线
                     *  第七个表示平铺方式
                     * CLAMP重复最后一个颜色至最后
                     * MIRROR重复着色的图像水平或垂直方向已镜像方式填充会有翻转效果
                     *REPEAT重复着色的图像水平或垂直方向
                     */
                    mLinearGradient = new LinearGradient(0, 0, mViewWith, 0,
                            new int[]{mGradientColor1, mGradientColor2, mGradientColor3},
                            null, Shader.TileMode.MIRROR);
                    mPaint.setShader(mLinearGradient);
                    mMatrix = new Matrix();
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (mMatrix != null) {
                mTranleate += mViewWith / 14;
                if (mTranleate > 2 * mViewWith) {
                    mTranleate = -mViewWith;
                }
                mMatrix.setTranslate(mTranleate, 0);
                mLinearGradient.setLocalMatrix(mMatrix);
                postInvalidateDelayed(250);
            }
        }
    }
}
