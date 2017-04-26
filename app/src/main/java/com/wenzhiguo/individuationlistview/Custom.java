package com.wenzhiguo.individuationlistview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;

public class Custom extends ListView implements AbsListView.OnScrollListener {

    private Handler handler = new Handler();
    private View mScrollBarPanel;
    private int mWidthMesureSpec;
    private int mHeightMesureSpec;
    private OnPositionChangedListener mOnPositionChangedListener;
    //定义滑动的y坐标-->onScroll里面不断判断和赋值
    public int mScrollBarPanelPosition = 0;
    public Animation mInAnimation = null;
    public Animation mOutAnimation = null;
    //定义指示器在ListView中的y轴高度
    public int thumbOffset = 0;
    public int mLastPostion = -1;

    public Custom(Context context, AttributeSet attrs) {
        super(context, attrs);
        //监听listview的滑动
        super.setOnScrollListener(this);
        //初始化动画数据
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExtendedListView);
        int layoutId = typedArray.getResourceId(R.styleable.ExtendedListView_scrollBarPanel, -1);
        int inAnimation = typedArray.getResourceId(R.styleable.ExtendedListView_scrollBarPanelInAnimation, -1);
        int outAnimation = typedArray.getResourceId(R.styleable.ExtendedListView_scrollBarPaneloutAnimation, -1);
        //释放资源
        typedArray.recycle();
        //设置气泡
        setScrollBarPanel(layoutId);
        //进和出的动画
        mInAnimation = AnimationUtils.loadAnimation(context, inAnimation);
        mOutAnimation = AnimationUtils.loadAnimation(context, outAnimation);
        int duration = ViewConfiguration.getScrollBarFadeDuration();
        mOutAnimation.setDuration(duration);
        mOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //动画结束的时候,指示器隐藏
                if (mScrollBarPanel != null) {
                    mScrollBarPanel.setVisibility(GONE);
                }
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView absListView, int fistVisibleItem, int visibleItemCount, int totalItemCount) {
        //监听当前系统滑动在那个位置,设置自己的气泡的位置
        if (mScrollBarPanel != null) {
            //mScrollBarPanelPosition不断的控制这个值的变化
            //computeVerticalScrollExtent();//滑动条在纵向滑动范围(放大后)
            //computeVerticalScrollOffset();//滑动在纵向幅度的位置
            //computeVerticalScrollRange();//滑动的范围
            //滑块的高度,  滑块的高/listview高=extent/range
            int hight = Math.round(1.0f * getMeasuredHeight() * computeVerticalScrollExtent() / computeVerticalScrollRange());
            //得到滑块中间y的坐标   滑块的高/extent=y/offset
            thumbOffset = hight / computeVerticalScrollExtent() * computeVerticalScrollOffset();
            thumbOffset += hight / 2;
            mScrollBarPanelPosition = thumbOffset - mScrollBarPanel.getHeight() / 2;

            int left = getMeasuredWidth() - mScrollBarPanel.getMeasuredWidth() - getVerticalScrollbarWidth();
            mScrollBarPanel.layout(left, mScrollBarPanelPosition,
                    left + mScrollBarPanel.getMeasuredWidth(),
                    mScrollBarPanelPosition + mScrollBarPanel.getHeight());
            for (int j = 0; j < getChildCount(); j++) {
                View childAt = getChildAt(j);
                //是否在这个条目的top和buttom的范围
                if (childAt != null) {
                    if (thumbOffset + hight / 2 > childAt.getTop() && thumbOffset + hight / 2 < childAt.getBottom()) {
                        if (mLastPostion != fistVisibleItem + j) {
                            mLastPostion = fistVisibleItem + j;
                            mOnPositionChangedListener.onPostioinChanged(this,mLastPostion,mScrollBarPanel);
                            //加载的条目多了,那么数字的宽度会发生改变,需要重新测量一下
                            measureChild(mScrollBarPanel,mWidthMesureSpec,mHeightMesureSpec);
                        }
                    }
                }
            }
        }
    }

    public void setScrollBarPanel(int scrollBarPanel) {
        //设置气泡布局
        mScrollBarPanel = LayoutInflater.from(getContext()).inflate(scrollBarPanel, this, false);
        mScrollBarPanel.setVisibility(GONE);
        //调整大小以及绘制
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量气泡控件
        if (mScrollBarPanel != null && getAdapter() != null) {
            mWidthMesureSpec = widthMeasureSpec;
            mHeightMesureSpec = heightMeasureSpec;
            measureChild(mScrollBarPanel, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //摆放自己的气泡控件
        if (mScrollBarPanel != null && getAdapter() != null) {
            int left = getMeasuredWidth() - mScrollBarPanel.getMeasuredWidth() - getVerticalScrollbarWidth();
            mScrollBarPanel.layout(left, mScrollBarPanelPosition,
                    left + mScrollBarPanel.getMeasuredWidth(), mScrollBarPanelPosition + mScrollBarPanel.getMeasuredHeight());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //在viewgroup绘制的时候,在上面追加一个自己绘制的气泡布局上面
        if (mScrollBarPanel != null && mScrollBarPanel.getVisibility() == View.VISIBLE) {
            drawChild(canvas, mScrollBarPanel, getDrawingTime());
        }
    }

    public interface OnPositionChangedListener {
        void onPostioinChanged(Custom custom, int position, View scrollBarPanel);
    }

    public void setOnPositionChangedListener(OnPositionChangedListener listener) {
        this.mOnPositionChangedListener = listener;
    }

    @Override
    protected boolean awakenScrollBars(int startDelay, boolean invalidate) {
        //唤醒滑块,判断系统的滑块是否唤醒--true:显示自己的气泡
        boolean b = super.awakenScrollBars(startDelay, invalidate);
        if (b && mScrollBarPanel != null) {
            if (mScrollBarPanel.getVisibility() == View.GONE) {
                //动画的进来
                mScrollBarPanel.setVisibility(View.VISIBLE);
                if (mInAnimation != null) {
                    mScrollBarPanel.startAnimation(mInAnimation);
                }
            }
            //过半秒钟消失
            handler.removeCallbacks(mScrollRunnable);
            handler.postDelayed(mScrollRunnable, startDelay + AnimationUtils.currentAnimationTimeMillis());
        }
        return b;
    }

    private final Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (mScrollBarPanel != null) {
                //通知动画
                mScrollBarPanel.startAnimation(mOutAnimation);
            }
        }
    };
}