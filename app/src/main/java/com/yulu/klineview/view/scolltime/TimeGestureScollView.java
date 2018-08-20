package com.yulu.klineview.view.scolltime;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Scroller;

/**
 * 分时K线图手势
 */
public class TimeGestureScollView extends TimeScollView {
    protected GestureDetector mGestureDetector;
    private Scroller mScroller;

    public TimeGestureScollView(Context context) {
        this(context, null);
    }

    public TimeGestureScollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeGestureScollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void initBaseKline(AttributeSet attrs) {
        mGestureDetector = new GestureDetector(mContext, new GestureListener());
        mScroller = new Scroller(mContext);
        super.initBaseKline(attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!isScreen) {
            return notSupportsGestures(event);
        } else {
            return supportsGestures(event);
        }
    }

    private double nLenStart = 0; // 手势缩放的时候使用
    /**
     * actionmove滑动的距离
     */
    private float mMoveDist;

    /**
     * 支持手势
     */
    private boolean supportsGestures(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            closeIndicateLine();
            int xlen = Math.abs((int) event.getX(0)
                    - (int) event.getX(1));
            int ylen = Math.abs((int) event.getY(0)
                    - (int) event.getY(1));
            double nLenEnd = Math.sqrt((double) xlen * xlen
                    + (double) ylen * ylen);
            if (nLenStart == 0) {
                nLenStart = nLenEnd;
                return true;
            }
            float kLWidthNews = (float) (kLWidth * nLenEnd / nLenStart);
            if (kLWidthNews > maxKLwidth) {
                kLWidthNews = maxKLwidth;
            }
            if (kLWidthNews < minKLwidth) {
                kLWidthNews = minKLwidth;
            }
            setkLWidth(kLWidthNews, true);
            nLenStart = nLenEnd;

        } else {
            nLenStart = 0;
//            final int action = event.getAction();
//            float curX = event.getX();
//            switch (action) {
//                case MotionEvent.ACTION_DOWN:
//                    getVelocityTracker();
//                    mVelocityTracker.addMovement(event);
//                    mMoveDist = curX;
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    mVelocityTracker.addMovement(event);
//                    mVelocityTracker.computeCurrentVelocity(100);//计算速度
//                    break;
//                case MotionEvent.ACTION_UP:
//                    mMoveDist = curX - mMoveDist;
//
//                    if (Math.abs(mMoveDist) < ViewConfiguration.get(mContext).getScaledTouchSlop()) {//过滤点击不小心滑动
//                        //如果是点击操作
//                        //do something
//                    } else {
//                        //手势滑动之后继续滚动
//                        float xVelocity = Math.abs(mVelocityTracker.getXVelocity());
//                        if (xVelocity > 200) {
//                            mScroller.startScroll(getScrollX(), getScrollY(), (int) -mMoveDist * 3, 0, 1000);
//                        }
//                    }
//                    recycleVelocityTracker();
//                    break;
//            }
            mGestureDetector.onTouchEvent(event);
        }
        return true;
    }

    private float mDownPosX = 0;
    private float mDownPosY = 0;

    /**
     * 不支持手势
     */
    private boolean notSupportsGestures(MotionEvent event) {
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
                closeIndicateLine();
                break;
        }
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private class GestureListener extends
            GestureDetector.SimpleOnGestureListener {
        /**
         * 双击down的时候触发
         */
        public boolean onDoubleTap(MotionEvent e) {
            if (null == e)
                return false;
            if (e.getX() > topRect.left
                    && e.getX() < topRect.right
                    && (e.getY() > topRect.top
                    && e.getY() < topRect.bottom
                    || e.getY() > bottomRect.top
                    && e.getY() < bottomRect.bottom)) {
                float kLWidthNews;
                if (kLWidth >= maxKLwidth) {
                    kLWidthNews = kLWidthOld;
                } else {
                    kLWidthNews = 1.5f * kLWidth;
                }
                if (kLWidthNews > maxKLwidth) {
                    kLWidthNews = maxKLwidth;
                }
                setkLWidth(kLWidthNews, true);
            }
            return true;
        }

        public void onLongPress(MotionEvent e) {
            if (!isShowIndicateLine) {
                closeIndicateLine();
                TimeGestureScollView.this.onScroll(e);
            }
        }

        /**
         * Touch了滑动时触发
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if (isShowIndicateLine) {
                float x = e2.getX();
                if (x >= topRect.left
                        && x <= lastX - offsetWidth + kLWidth * 0.5f) {
                    scollX = x;
                    invalidate();
                }
            } else {
                if (mDatas.size() <= horizontalNum) {
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }
                onScrollTo(distanceX);
            }
            return true;
        }

        // 单击不滑动
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            if (x > topRect.left
                    && x <= topRect.left + maxWidthNum * kLWidth - offsetWidth
                    && y > topRect.top
                    && y < bottomRect.bottom) {
                if (isScreen) {
                    if (!isShowIndicateLine) {
                        // 显示弹出框
                        isShowIndicateLine = true;
                        scollX = x;
                    } else {
                        closeIndicateLine();
                    }
                    invalidate();
                } else {
                    if (mOnClickSurfaceListener != null) {
                        mOnClickSurfaceListener.onKLClick(); // 点击K线图，去全屏显示K线图
                    }
                }
            } else {
                if (mOnClickSurfaceListener != null) {
                    mOnClickSurfaceListener.onKLClick(); // 点击K线图，去全屏显示K线图
                }
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (!isScreen && isShowIndicateLine) {
                closeIndicateLine();
            }
            return super.onSingleTapUp(e);
        }

//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            if (Math.abs(e1.getX() - e2.getX()) > 300) {
//                mScroller.startScroll(getScrollX(), getScrollY(), (int) ((e1.getX() - e2.getX()) * 10), 0, 1500);
//                return true;
//            } else {
//                return false;
//            }
////            return super.onFling(e1, e2, velocityX, velocityY);
//        }
    }

    private void onScrollTo(float distanceX) {
        if (distanceX < 0) {
            // 往左滑动
            if (offsetWidth > 0) {
                offsetWidth += distanceX;
                if (offsetWidth < 0) {
                    offsetWidth = 0;
                }
                setOffsetWidth(offsetWidth);
            }
        } else {
            // 往右滑动
            if (offsetWidth < offsetWidthMax) {
                offsetWidth = offsetWidth + distanceX < offsetWidthMax ? Math.round(offsetWidth + distanceX) : offsetWidthMax;
                setOffsetWidth(Math.round(offsetWidth));
            }
        }
    }

    private void onScroll(MotionEvent e) {
        if (isShowIndicateLine) {
            float x = e.getX();
            if (x >= topRect.left
                    && x <= lastX + kLWidth / 2 - offsetWidth) {
                scollX = x;
                invalidate();
            }
        }
    }

//    @Override
//    public void computeScroll() {
//        if (mScroller.computeScrollOffset()) {
//            onScrollTo(mScroller.getCurrX()-getScrollX());
//        }
//        super.computeScroll();
//    }

    /**
     * 获取速度追踪器
     *
     * @return
     */
    VelocityTracker mVelocityTracker;

    private VelocityTracker getVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        return mVelocityTracker;
    }

    /**
     * 回收速度追踪器
     */
    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}
