package com.yulu.klineview.view.time;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class TimeGestureView extends TimeStockView {
    protected GestureDetector mGestureDetector;

    public TimeGestureView(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(mContext, new GestureListener());
    }

    public TimeGestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(mContext, new GestureListener());
    }

    public TimeGestureView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        mGestureDetector = new GestureDetector(mContext, new GestureListener());
    }


    private float mDownPosX = 0;
    private float mDownPosY = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownPosX = x;
                mDownPosY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaX = Math.abs(x - mDownPosX);
                final float deltaY = Math.abs(y - mDownPosY);
                if (deltaX > deltaY || isShowIndicateLine) {
                    //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
                    getParent().requestDisallowInterceptTouchEvent(true);
                    onScroll(event);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isShowIndicateLine) {
                    isShowIndicateLine = false;
                    invalidate();
                    if (mOnClickSurfaceListener != null) {
                        mOnClickSurfaceListener.hideIndicateQuotation();
                    }
                    return false;
                }
                break;
        }
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private void onScroll(MotionEvent e) {
        float x = e.getX();
        if (isShowIndicateLine
                && taggingTopPaths.size() > 0
                && x >= topRect.left
                && x <= taggingTopPaths.get(taggingTopPaths.size() - 1).getX()) {
            scollX = x;
            invalidate();
        }
    }

    private class GestureListener extends
            GestureDetector.SimpleOnGestureListener {

        public void onLongPress(MotionEvent e) {
            isShowIndicateLine = true;
            TimeGestureView.this.onScroll(e);
        }

        // 单击不滑动
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            if (isShowIndicateLine) {
                isShowIndicateLine = false;
                invalidate();
                if (mOnClickSurfaceListener != null) {
                    mOnClickSurfaceListener.hideIndicateQuotation();
                }
            } else if (mOnClickSurfaceListener != null) {
                if (x >= topRect.left && x <= topRect.right && y > topRect.top && y < bottomRect.bottom
                        ) {
                    mOnClickSurfaceListener.onTimeClick();
                }
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (isShowIndicateLine) {
                isShowIndicateLine = false;
                invalidate();
                if (mOnClickSurfaceListener != null) {
                    mOnClickSurfaceListener.hideIndicateQuotation();
                }
            }
            return super.onSingleTapUp(e);
        }
    }
}
