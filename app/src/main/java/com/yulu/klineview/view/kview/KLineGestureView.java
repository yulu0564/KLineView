package com.yulu.klineview.view.kview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.yulu.klineview.utils.DateUtils;

/**
 * K线图(主线程绘制）
 */
public class KLineGestureView extends KLineStockView {

    protected GestureDetector mGestureDetector;


    private double nLenStart; // 手势缩放的时候使用


    public KLineGestureView(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(mContext, new GestureListener());
    }

    public KLineGestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(mContext, new GestureListener());
    }

    public KLineGestureView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        mGestureDetector = new GestureDetector(mContext, new GestureListener());
    }

    private float mDownPosX = 0;
    private float mDownPosY = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!isScreen) {
            return notSupportsGestures(event);
        } else {
            return supportsGestures(event);
        }
    }

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
            mGestureDetector.onTouchEvent(event);
        }

        return true;
    }

    /**
     * 不支持手势
     *
     * @param event
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

    private void onScroll(MotionEvent e) {
        if (isShowIndicateLine) {
            float x = e.getX();
            if (x >= topRect.left
                    && x <= lastX + kLWidth / 2) {
                scollX = x;
                invalidate();
            }
        }
    }

    private class GestureListener extends
            GestureDetector.SimpleOnGestureListener {
        /**
         * 双击down的时候触发
         */
        public boolean onDoubleTap(MotionEvent e) {
            if (null == e)
                return false;
            if (e.getX() > topRect.left && e.getX() < topRect.right && (e.getY() > topRect.top && e.getY() < topRect.bottom || e.getY() > bottomRect.top && e.getY() < bottomRect.bottom)) {
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
                KLineGestureView.this.onScroll(e);
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
                        && x <= lastX + kLWidth / 2) {
                    scollX = x;
                    invalidate();
                }
            } else {
                int addDeviant = (int) -Math
                        .rint((distanceX / kLWidth));
                if (distanceX < 0) {
                    // 往左滑动
                    // addDeviant+=1;
                    if (leftDeviant + valueStock < mDatas.size()) {
                        if (leftDeviant + addDeviant + valueStock < mDatas
                                .size()) {
                            leftDeviant += addDeviant;
                        } else {
                            leftDeviant = mDatas.size() - valueStock;
                        }
                        invalidate();
                    }
                    if (isEnableLoadMore
                            && finishLoadmore&&mDatas.size() != 0
                            && leftDeviant + valueStock > mDatas.size() - 30) {
                        finishLoadmore = false;
                        onDownload(DateUtils.getNextDay(mDatas.get(0)
                                .getTime()));
                    }
                } else {
                    // addDeviant-=1;
                    // 往右滑动
                    if (leftDeviant > 0) {
                        if (leftDeviant > -addDeviant) {
                            leftDeviant += addDeviant;
                        } else {
                            leftDeviant = 0;
                        }
                        invalidate();
                    }
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        // 单击不滑动
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            if (x > topRect.left
                    && x <= topRect.left + kLWidth
                    * showQuotationBeanList.size() && y > topRect.top && y < bottomRect.bottom) {
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
    }

}
