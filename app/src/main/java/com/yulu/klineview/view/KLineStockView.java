//package com.yulu.klineview.view;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.DashPathEffect;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PathEffect;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//
//import com.hexun.news.R;
//import com.hexun.news.data.entity.TargetManager;
//
///**
// * K线图
// */
//public class KLineStockView extends BaseStockView {
//	private float[] kLWidthArray = new float[5]; // K线图的宽度
//	private int kLWidthSub = 1;
//	private List<KLineModel> kLineModelList = new ArrayList<KLineModel>(); // K线数据
//	private List<KLineModel> showKLineModelList = new ArrayList<KLineModel>(); // 可见的K线数据
//
//	private float topHeight; // 头部展示的高度
//	private float leftAxisWidth; // 左边坐标的宽度
//	private float bottomAxisHeight; // 下边坐标的高度
//	private float margin; // 头部图的上下左右的边距
//	private float centerHeight; // 中间显示的高度
//	private float kLCanvasHeight; // K线图绘图的高度
//	private float kLCanvasWidth; // K线图绘图的宽度
//	private float figureHeight; // 下方图的高度
//	private int valueStock; // 显示的数量
//
//	private int colorAvlData5 = 0xFF05CFCE;// 0x00FFB400;五日平均线颜色值
//	private int colorAvlData10 = 0xFFFAAD4F;// 0x00F5A2FF;十日平均线颜色值
//	private int colorAvlData30 = 0xFFCC00CC;// 0x00105194;三十日平均线颜色值
//
//	private int colorCenterBg = 0xFFF2F2F2; // 中间的背景
//
//	private long[] initAverageData5 = null;
//	private long[] initAverageData10 = null;
//	private long[] initAverageData30 = null;
//
//	private int deviant = 0; // 偏移量
//	private int leftDeviant = 0; // 左划了多少
//	// private int addDeviant = 0; //计划滑动的距离
//	private GestureDetector mGestureDetector;
//	private String[] targetStrs = { "VOL", "MACD", "KDJ", "RSI", "BIAS", "CCI" };
//	// private int targetSub = 0; // 副图的值 0为VOL，1为MACD，2为KDJ，3为RSI，4为BIAS，5为CCI
//
//	private double nLenStart0, nLenStart1 = 0; // 手势缩放的时候使用
//
//	/** 5、10、30日均线 */
//	private Map<String, long[]> averageMap = null;
//
//	/**
//	 * Macd数据
//	 */
//	private Map<String, float[]> macdMap = null;
//	/**
//	 * rsi数据
//	 */
//	private Map<String, int[]> rsiMap = null;
//	/**
//	 * kdj数据
//	 */
//	private Map<String, int[]> kdjMap = null;
//
//	/**
//	 * bias数据
//	 */
//	private Map<String, int[]> biasMap = null;
//	/**
//	 * cci数据
//	 */
//	private Map<String, int[]> cciMap = null;
//
//	private Map<String, long[]> bollMap = null;
//
//	private String[] divideExcepts = { "复权", "后复权", "除权" };
//	private int divideIndex = 2; // 复权的下标
//	private boolean isHideDivide = false; // 是否显示复权
//	private boolean isDay;
//	/**
//	 * 主图技术指标索引
//	 */
//	// private int TARGET_HEADER_INDEX = 0; // 0为 5、10、30日均线（MA均线） ，1为boll数据
//
//	private OnClickSurfaceListener mOnClickSurfaceListener;
//
//	public KLineStockView(Context context) {
//		super(context);
//		initView();
//	}
//
//	public KLineStockView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		initView();
//	}
//
//	public KLineStockView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		initView();
//	}
//
//	protected void initView() {
//		mGestureDetector = new GestureDetector(mContext, new GestureListener());
//		kLWidthArray[0] = dip2px(3);
//		kLWidthArray[1] = dip2px(6);
//		kLWidthArray[2] = dip2px(9);
//		kLWidthArray[3] = dip2px(12);
//		kLWidthArray[4] = dip2px(15);
//		topHeight = dip2px(20);
//		leftAxisWidth = dip2px(35);
//		bottomAxisHeight = dip2px(20);
//		margin = dip2px(10);
//		centerHeight = dip2px(30);
//		popupBorderHeight = dip2px(80);
//		// setFocusable(true);
//		// setFocusableInTouchMode(true);
//		// this.setKeepScreenOn(true);
//		mPath = new Path();
//		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if (event.getPointerCount() == 2) {
//					int xlen = Math.abs((int) event.getX(0)
//							- (int) event.getX(1));
//					int ylen = Math.abs((int) event.getY(0)
//							- (int) event.getY(1));
//					double nLenEnd = Math.sqrt((double) xlen * xlen
//							+ (double) ylen * ylen);
//					if (nLenStart0 == 0 || nLenStart1 == 0) {
//						nLenStart0 = nLenEnd;
//						nLenStart1 = nLenEnd;
//						return true;
//					}
//					if (nLenEnd > nLenStart0 + dip2px(30))// 通过两个手指开始距离和结束距离，来判断放大缩小
//					{
//						// 放大
//						if (kLWidthSub < kLWidthArray.length - 1) {
//							kLWidthSub++;
//							valueStock = (int) (kLCanvasWidth / kLWidthArray[kLWidthSub]); // 修改显示数量
//							int centerDeviant = (showKLineModelList.size() + valueStock)
//									/ 2 + 1 + deviant;
//							if (centerDeviant < kLineModelList.size()) {
//								leftDeviant = kLineModelList.size()
//										- centerDeviant;
//							} else {
//								leftDeviant = 0;
//							}
//							invalidate();
//						}
//						nLenStart0 = nLenEnd + dip2px(40);
//						nLenStart1 = nLenEnd;
//					} else if (nLenEnd < nLenStart1 - dip2px(30)) {
//						// 缩放
//						if (kLWidthSub > 0) {
//							kLWidthSub--;
//							valueStock = (int) (kLCanvasWidth / kLWidthArray[kLWidthSub]); // 修改显示数量
//							int centerDeviant = (showKLineModelList.size() + valueStock)
//									/ 2 + 1 + deviant; // 偏移后最后一个坐标点的坐标
//							if (centerDeviant < kLineModelList.size()) {
//								leftDeviant = kLineModelList.size()
//										- centerDeviant;
//							} else {
//								leftDeviant = 0;
//							}
//							invalidate();
//						}
//						nLenStart0 = nLenEnd;
//						nLenStart1 = nLenEnd - dip2px(40);
//					}
//				} else {
//					nLenStart0 = 0;
//					nLenStart1 = 0;
//					mGestureDetector.onTouchEvent(event);
//				}
//				return true;
//			}
//		});
//	}
//
//	// @Override
//	// public boolean onTouchEvent(MotionEvent event) {
//	//
//	// return super.onTouchEvent(event);
//	// }
//
//	@Override
//	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		mIsDrawing = true;
//		this.canvasWidth = getWidth();
//		this.canvasHeight = getHeight();
//		float remainHeight = canvasHeight - margin * 4 - topHeight
//				- bottomAxisHeight - centerHeight;
//		kLCanvasHeight = remainHeight * 2 / 3;
//		figureHeight = remainHeight - kLCanvasHeight;
//		kLCanvasWidth = canvasWidth - margin * 2 - leftAxisWidth;
//		valueStock = (int) (kLCanvasWidth / kLWidthArray[kLWidthSub]);
//	}
//
//	@Override
//	protected void onDraw(Canvas mCanvas) {
//		// mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//		mCanvas.drawPaint(mPaint);
//		// mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
//
//		mCanvas.drawColor(Color.WHITE);
//		mCanvas.drawPath(mPath, mPaint);
//		try {
//			drawLine(mCanvas);
//		} catch (Exception e) {
//		}
//	}
//
//	/**
//	 * 图形绘制
//	 */
//	private void drawLine(Canvas mCanvas) {
//
//		if (valueStock < kLineModelList.size()) {
//			if (valueStock + leftDeviant > kLineModelList.size()) {
//				leftDeviant = kLineModelList.size() - valueStock;
//			}
//			showKLineModelList = kLineModelList.subList(kLineModelList.size()
//					- valueStock - leftDeviant, kLineModelList.size()
//					- leftDeviant);
//			deviant = kLineModelList.size() - valueStock - leftDeviant;
//		} else {
//			showKLineModelList = kLineModelList;
//			deviant = 0;
//		}
//
//		if (TargetManager.TARGET_HEADER_INDEX == 0) {
//			String[] target_MA = TargetManager.target_value
//					.get(TargetManager.TARGET_MA);
//			if (averageMap != null) {
//				initAverageData5 = averageMap.get(target_MA[0]);
//				initAverageData10 = averageMap.get(target_MA[1]);
//				initAverageData30 = averageMap.get(target_MA[2]);
//
//			} else {
//				averageMap = new HashMap<String, long[]>();
//				int day5 = Integer.parseInt(target_MA[0]);
//				int day10 = Integer.parseInt(target_MA[1]);
//				int day30 = Integer.parseInt(target_MA[2]);
//				initAverageData5 = StockAverageUtils.calcAverageData(
//						kLineModelList, day5);
//				initAverageData10 = StockAverageUtils.calcAverageData(
//						kLineModelList, day10);
//				initAverageData30 = StockAverageUtils.calcAverageData(
//						kLineModelList, day30);
//				averageMap.put(target_MA[0], initAverageData5);
//				averageMap.put(target_MA[1], initAverageData10);
//				averageMap.put(target_MA[2], initAverageData30);
//			}
//		} else if (TargetManager.TARGET_HEADER_INDEX == 1) {
//			if (bollMap == null)
//				bollMap = StockAverageUtils.getBollData(kLineModelList);
//			if (bollMap != null) {
//				initAverageData5 = bollMap.get("midBoll");
//				initAverageData10 = bollMap.get("upBoll");
//				initAverageData30 = bollMap.get("lowBoll");
//			}
//		}
//		setKLMaxAndMin();
//		switch (TargetManager.TARGET_FOOTER_INDEX) {
//		case 0:
//			// setFTMaxAndMin(rsiMap.get("rsi6"), rsiMap.get("rsi12"),
//			// rsiMap.get("rsi24"));
//			break;
//		case 1:
//			if (macdMap == null) {
//				macdMap = StockAverageUtils.getInitMacdData(kLineModelList);
//			}
//			if (macdMap != null) {
//				setFTMaxAndMin(macdMap.get("dea"), macdMap.get("diff"),
//						macdMap.get("macd"));
//			}
//			break;
//		case 2:
//			if (kdjMap == null) {
//				kdjMap = StockAverageUtils.getKDJ(kLineModelList);
//			}
//			if (kdjMap != null) {
//				setFTMaxAndMin(kdjMap.get("k"), kdjMap.get("d"),
//						kdjMap.get("j"));
//			}
//			break;
//		case 3:
//			if (rsiMap == null) {
//				rsiMap = StockAverageUtils.getRSIData(kLineModelList);
//			}
//			minFT = 0;
//			maxFT = 100;
//			break;
//		case 4:
//			if (biasMap == null) {
//				biasMap = StockAverageUtils.getBias(kLineModelList);
//			}
//			if (biasMap != null) {
//				setFTMaxAndMin(biasMap.get("bias6"), biasMap.get("bias12"),
//						biasMap.get("bias24"));
//			}
//			break;
//		case 5:
//			cciMap = StockAverageUtils.getCCI(kLineModelList);
//			if (cciMap != null) {
//				setFTMaxAndMin(cciMap.get("cci"));
//			}
//			break;
//		}
//
//		// 绘制K线图横坐标
//		drawAllXLine(mCanvas);
//		// 绘制K线图纵坐标
//		drawAllYLine(mCanvas);
//		mPaint.setStyle(Paint.Style.STROKE);
//		mPaint.setStrokeWidth(dip2px(1));
//		mPaint.setColor(borderColor);
//		float statrtX = margin + leftAxisWidth;
//		float endX = canvasWidth - margin;
//		mCanvas.drawRect(statrtX, margin + topHeight - dip2px(1), endX, margin
//				+ topHeight + kLCanvasHeight + dip2px(1), mPaint);
//		mCanvas.drawRect(statrtX, margin * 3 + topHeight + kLCanvasHeight
//				+ bottomAxisHeight + centerHeight - dip2px(1), endX, margin * 3
//				+ topHeight + kLCanvasHeight + bottomAxisHeight + centerHeight
//				+ figureHeight + dip2px(1), mPaint);
//		// 绘制图形
//		drawKLine(mCanvas);
//
//	}
//
//	/**
//	 * 画所有横向表格，包括X轴
//	 */
//	private void drawAllXLine(Canvas mCanvas) {
//		float ordinateValue = 0;
//
//		if (minKL != 0 && maxKL > minKL) {
//			ordinateValue = (maxKL - minKL) / 4.0f;
//		}
//		float cutoffHeight = kLCanvasHeight / 4.0f;
//		mPaint.setColor(colorCoordinates);
//		mPaint.setStrokeWidth(dip2px(0.7f));
//		for (int i = 0; i < 5; i++) {
//			float cutoffY = cutoffHeight * i + margin + topHeight;
//			if (i == 2) {
//				mPaint.setPathEffect(null);
//				mCanvas.drawLine(leftAxisWidth + margin, cutoffY, canvasWidth
//						- margin, cutoffY, mPaint);// X坐标
//			} else if (i == 1 || i == 3) {
//				PathEffect effects = new DashPathEffect(new float[] {
//						dip2px(3), dip2px(3), dip2px(3), dip2px(3) }, 1);
//				mPaint.setPathEffect(effects);
//				mCanvas.drawLine(leftAxisWidth + margin, cutoffY, canvasWidth
//						- margin, cutoffY, mPaint);// X坐标
//			}
//			mPaint.setPathEffect(null);
//			if (ordinateValue != 0) {
//				int textColor;
//				if (i < 2) {
//					if (i == 0) {
//						cutoffY += dip2px(6);
//					} else {
//						cutoffY += dip2px(3);
//					}
//					textColor = colorRise;
//				} else if (i == 2) {
//					textColor = Color.BLACK;
//					cutoffY += dip2px(3);
//				} else {
//					textColor = colorFall;
//					if (i != 4) {
//						cutoffY += dip2px(3);
//					}
//				}
//				setText(getTwoStep((maxKL - ordinateValue * i) / priceWeight),
//						margin / 2, cutoffY, mCanvas, Paint.Align.LEFT,
//						textColor, 9);
//			}
//		}
//
//		// 均线绘制
//		cutoffHeight = figureHeight / 3;
//		ordinateValue = (maxFT - minFT) / 3.0f;
//		for (int i = 0; i < 4; i++) {
//			float cutoffY = cutoffHeight * i + margin * 3 + topHeight
//					+ kLCanvasHeight + centerHeight + bottomAxisHeight;
//			if (i != 0 && i != 3) {
//				mPaint.setColor(colorCoordinates);
//				PathEffect effects = new DashPathEffect(new float[] {
//						dip2px(3), dip2px(3), dip2px(3), dip2px(3) }, 1);
//				mPaint.setPathEffect(effects);
//				mPaint.setStrokeWidth(dip2px(0.7f));
//				mCanvas.drawLine(leftAxisWidth + margin, cutoffY, canvasWidth
//						- margin, cutoffY, mPaint);// X坐标
//			}
//			if (i == 0) {
//				cutoffY += dip2px(6);
//			} else if (i == 1) {
//				cutoffY += dip2px(3);
//			}
//			// 设置副图的坐标
//			if (maxFT > minFT) {
//				String title = "";
//				switch (TargetManager.TARGET_FOOTER_INDEX) {
//				case 0:
//					if (i == 3) {
//						title = "0";
//						break;
//					}
//					if (maxFT > 100000000 * 100.0f) {
//						title = getTwoStep((maxFT - ordinateValue * i) / 100000000 / 100.0f)
//								+ "亿";
//					} else if (maxFT > 10000) {
//						title = getTwoStep((maxFT - ordinateValue * i) / 10000 / 100.0f)
//								+ "万";
//					} else {
//						title = getTwoStep((maxFT - ordinateValue * i) / 100.0f);
//					}
//					break;
//				case 1:
//					if (i == 0) {
//						title = getTwoStep(maxFT / 100.0f);
//					} else if (i == 3) {
//						title = getTwoStep(minFT / 100.0f);
//					}
//					break;
//				case 2:
//					if (i == 0) {
//						title = getTwoStep(maxFT / 100.0f);
//					} else if (i == 3) {
//						title = getTwoStep(minFT / 100.0f);
//					}
//					break;
//				case 3:
//					if (i == 0) {
//						title = "100";
//					} else if (i == 3) {
//						title = "0";
//					} else {
//						title = "";
//					}
//					break;
//				case 4:
//
//				case 5:
//					if (i == 0) {
//						title = getTwoStep(maxFT / 100.0);
//					} else if (i == 3) {
//						title = getTwoStep(minFT / 100.0);
//					}
//					break;
//				}
//				setText(title, margin + leftAxisWidth - dip2px(5), cutoffY,
//						mCanvas, Paint.Align.RIGHT, Color.BLACK, 9);
//			}
//
//		}
//	}
//
//	/**
//	 * 画所有纵向表格，包括Y轴
//	 */
//	private void drawAllYLine(Canvas mCanvas) {
//		float cutoffWidth = kLCanvasWidth / 4.0f;
//		mPaint.setStrokeWidth(dip2px(1));
//		lastDate = null;
//		// mPaint.setColor(Color.parseColor("#363636"));
//		for (int i = 0; i < 5; i++) {
//			float cutoffX = cutoffWidth * i + margin + leftAxisWidth;
//			if (i == 0) {
//				cutoffX--;
//				if (showKLineModelList != null && showKLineModelList.size() > 0) {
//					String time = getStockDate(showKLineModelList.get(0)
//							.getTime());
//					setText(time, cutoffX, margin + topHeight + kLCanvasHeight
//							+ margin, mCanvas, Paint.Align.LEFT, Color.BLACK,
//							10);
//				}
//			} else if (i == 4) {
//				cutoffX++;
//				if (showKLineModelList != null
//						&& showKLineModelList.size() == valueStock) {
//					String time = getStockDate(showKLineModelList.get(
//							showKLineModelList.size() - 1).getTime());
//					setText(time, cutoffX, margin + topHeight + kLCanvasHeight
//							+ margin, mCanvas, Paint.Align.RIGHT, Color.BLACK,
//							10);
//				}
//			} else {
//				PathEffect effects = new DashPathEffect(new float[] {
//						dip2px(3), dip2px(3), dip2px(3), dip2px(3) }, 1);
//				mPaint.setPathEffect(effects);
//				mPaint.setColor(colorCoordinates);
//				mPaint.setStrokeWidth(dip2px(0.7f));
//				if (i != 1 && showKLineModelList != null
//						&& showKLineModelList.size() > valueStock * i / 4) {
//					String time = getStockDate(showKLineModelList.get(
//							valueStock * i / 4).getTime());
//					setText(time, cutoffX, margin + topHeight + kLCanvasHeight
//							+ margin, mCanvas, Paint.Align.CENTER, Color.BLACK,
//							10);
//				}
//				mCanvas.drawLine(cutoffX, margin + topHeight, cutoffX, margin
//						+ topHeight + kLCanvasHeight, mPaint);// Y坐标
//			}
//		}
//		mPaint.setPathEffect(null);
//	}
//
//	// 绘制K线图
//	private void drawKLine(Canvas mCanvas) {
//		if (kLineModelList.size() == 0)
//			return;
//
//		int indicateLineIndex = 0;
//		float indicateLineY = 0;
//		float lastY5 = -1;
//		float lastY10 = -1;
//		float lastY30 = -1;
//		lastX = -1;
//
//		// rsi
//		int[] rsi6 = null;
//		int[] rsi12 = null;
//		int[] rsi24 = null;
//		float rsi6Y = -1;
//		float rsi12Y = -1;
//		float rsi24Y = -1;
//
//		// macd
//		float[] dea = null;
//		float[] diff = null;
//		float[] macd = null;
//		float diffY = -1;
//		float deaY = -1;
//
//		// KDJ
//		float kY = -1;
//		float dY = -1;
//		float jY = -1;
//		int[] k = null;
//		int[] d = null;
//		int[] j = null;
//
//		// bias
//		float bias6Y = -1;
//		float bias12Y = -1;
//		float bias24Y = -1;
//		int[] bias6 = null;
//		int[] bias12 = null;
//		int[] bias24 = null;
//
//		// cci
//		float cciY = -1;
//		int[] cci = null;
//
//		switch (TargetManager.TARGET_FOOTER_INDEX) {
//		case 0:
//			break;
//		case 1:
//			// 绘制MACD图
//			if (macdMap != null) {
//				dea = macdMap.get("dea");
//				diff = macdMap.get("diff");
//				macd = macdMap.get("macd");
//			}
//			break;
//		case 2:
//			// 绘制KDJ图
//			if (kdjMap != null) {
//				k = kdjMap.get("k");
//				d = kdjMap.get("d");
//				j = kdjMap.get("j");
//			}
//			break;
//		case 3:
//			// 绘制RIS相对强弱指标
//			if (rsiMap != null) {
//				rsi6 = rsiMap.get("rsi6");
//				rsi12 = rsiMap.get("rsi12");
//				rsi24 = rsiMap.get("rsi24");
//			}
//			break;
//		case 4:
//			if (biasMap != null) {
//				bias6 = biasMap.get("bias6");
//				bias12 = biasMap.get("bias12");
//				bias24 = biasMap.get("bias24");
//			}
//			break;
//		case 5:
//			if (cciMap != null) {
//				cci = cciMap.get("cci");
//			}
//			break;
//		}
//
//		float startX = leftAxisWidth + margin;
//		float farPointsY = 0;
//		if (TargetManager.TARGET_FOOTER_INDEX == 1) {
//			farPointsY = getCutoffFTY(0);
//			if (farPointsY > margin * 3 + topHeight + kLCanvasHeight
//					+ centerHeight + bottomAxisHeight + dip2px(15)
//					&& farPointsY < margin * 3 + topHeight + kLCanvasHeight
//							+ centerHeight + bottomAxisHeight + figureHeight
//							- dip2px(15)) {
//				setText("0", margin + leftAxisWidth - dip2px(5), farPointsY
//						+ dip2px(3), mCanvas, Paint.Align.RIGHT, Color.BLACK, 9);
//			}
//		}
//		for (int i = 0; i < showKLineModelList.size(); i++) {
//			KLineModel mKLineModel = showKLineModelList.get(i);
//			float open = mKLineModel.getOpen(); // 开盘价
//			float close = mKLineModel.getClose(); // 收盘价
//			float high = mKLineModel.getHigh(); // 最高价
//			float low = mKLineModel.getLow(); // 最低价
//			float volume = mKLineModel.getVolume(); // 成交量
//			float amount = mKLineModel.getAmount(); // 成交额
//			float highY = getCutoffKLY(high); // 最高价的坐标
//			float lowY = getCutoffKLY(low); // 最低价的坐标
//			float openY = getCutoffKLY(open); // 开盘价的坐标
//			float closeY = getCutoffKLY(close); // 收盘价的坐标
//
//			// 五日十日三十日均线
//			float avgY5 = 0;
//			float avgY10 = 0;
//			float avgY30 = 0;
//
//			if (initAverageData5 != null
//					&& initAverageData5.length > i + deviant) {
//				avgY5 = getCutoffKLY((initAverageData5[i + deviant]));
//			}
//			if (initAverageData10 != null
//					&& initAverageData10.length > i + deviant) {
//				avgY10 = getCutoffKLY((initAverageData10[i + deviant]));
//			}
//			if (initAverageData30 != null
//					&& initAverageData30.length > i + deviant) {
//				avgY30 = getCutoffKLY((initAverageData30[i + deviant]));
//			}
//			mPaint.setStrokeWidth(dip2px(1));
//			float teamLastX = startX + kLWidthArray[kLWidthSub] / 2;
//			if (i != 0) {
//				// mPaint.setPathEffect(new CornerPathEffect(10));
//				// mPaint.setStyle(Paint.Style.FILL);
//				if (initAverageData5[i + deviant - 1] > 0) {
//					mPaint.setColor(colorAvlData5);
//					mCanvas.drawLine(lastX, lastY5, teamLastX, avgY5, mPaint);
//					// Path path = new Path();
//					// path.moveTo(lastX, lastY5);
//					// path.lineTo(teamLastX, avgY5);
//					// mCanvas.drawPath(path, mPaint);
//				}
//				if (initAverageData10[i + deviant - 1] > 0) {
//					mPaint.setColor(colorAvlData10);
//					mCanvas.drawLine(lastX, lastY10, teamLastX, avgY10, mPaint);
//				}
//				if (initAverageData30[i + deviant - 1] > 0) {
//					mPaint.setColor(colorAvlData30);
//					mCanvas.drawLine(lastX, lastY30, teamLastX, avgY30, mPaint);
//				}
//			}
//			float kLstartX = startX + 1;
//			float endX = startX + kLWidthArray[kLWidthSub] - 1;
//			mPaint.setStrokeWidth(dip2px(0.7f));
//			if (close < open) {
//				mPaint.setColor(colorFall); // 跌
//				mPaint.setStyle(Paint.Style.FILL);
//				mCanvas.drawLine(startX + kLWidthArray[kLWidthSub] / 2, closeY,
//						teamLastX, lowY, mPaint);
//				mCanvas.drawLine(startX + kLWidthArray[kLWidthSub] / 2, openY,
//						teamLastX, highY, mPaint);
//				kLstartX += 1;
//				endX -= 1;
//			} else {
//				mPaint.setColor(colorRise); // 涨
//				mPaint.setStyle(Paint.Style.STROKE);
//				mCanvas.drawLine(startX + kLWidthArray[kLWidthSub] / 2, closeY,
//						teamLastX, highY, mPaint);
//				mCanvas.drawLine(startX + kLWidthArray[kLWidthSub] / 2, openY,
//						teamLastX, lowY, mPaint);
//				kLstartX += dip2px(0.3f);
//				endX -= dip2px(0.3f);
//			}
//			if (openY < closeY) {
//				mCanvas.drawRect(kLstartX, openY, endX, closeY, mPaint);
//			} else {
//				mCanvas.drawRect(kLstartX, closeY, endX, openY, mPaint);
//			}
//
//			switch (TargetManager.TARGET_FOOTER_INDEX) {
//			case 0:
//				// VOL图
//				mPaint.setStrokeWidth(dip2px(0.7f));
//				if (close < open) {
//					mPaint.setColor(colorFall); // 跌
//					mPaint.setStyle(Paint.Style.FILL);
//				} else {
//					mPaint.setColor(colorRise); // 涨
//					mPaint.setStyle(Paint.Style.STROKE);
//				}
//				mCanvas.drawRect(kLstartX, getCutoffFTY(volume), endX, margin
//						* 3 + topHeight + kLCanvasHeight + centerHeight
//						+ bottomAxisHeight + figureHeight, mPaint);
//				break;
//			case 1:
//				// 绘制MACD图
//				if (macdMap != null) {
//					mPaint.setStyle(Paint.Style.FILL);
//					mPaint.setStrokeWidth(dip2px(1));
//
//					if (macd != null && macd[i + deviant] != 0) {
//						if (macd[i + deviant] > 0) {
//							mPaint.setColor(colorRise); // 涨
//						} else {
//							mPaint.setColor(colorFall);
//						}
//						mCanvas.drawLine(startX + kLWidthArray[kLWidthSub] / 2,
//								farPointsY, teamLastX, getCutoffFTY(macd[i
//										+ deviant]), mPaint);
//					}
//
//					if (diff != null) {
//						float tempDiffY = getCutoffFTY(diff[i + deviant]); // 最新数据
//						if (i != 0) {
//							mPaint.setColor(colorAvlData30);
//							mCanvas.drawLine(lastX, diffY, teamLastX,
//									tempDiffY, mPaint);
//						}
//						diffY = tempDiffY;
//					}
//					if (dea != null) {
//						float tempDeaY = getCutoffFTY(dea[i + deviant]); // 最新数据
//						if (i != 0) {
//							mPaint.setColor(colorAvlData10);
//							mCanvas.drawLine(lastX, deaY, teamLastX, tempDeaY,
//									mPaint);
//						}
//						deaY = tempDeaY;
//					}
//				}
//				break;
//			case 2:
//				// 绘制KDJ图
//				if (kdjMap != null) {
//					if (k != null) {
//						float tempKY = getCutoffFTY(k[i + deviant]);
//						if (i != 0) {
//							mPaint.setColor(colorAvlData5);
//							mCanvas.drawLine(lastX, kY, teamLastX, tempKY,
//									mPaint);
//						}
//						kY = tempKY;
//					}
//					if (d != null) {
//						float tempDY = getCutoffFTY(d[i + deviant]);
//						if (i != 0) {
//							mPaint.setColor(colorAvlData10);
//							mCanvas.drawLine(lastX, dY, teamLastX, tempDY,
//									mPaint);
//						}
//						dY = tempDY;
//					}
//					if (j != null) {
//						float tempJY = getCutoffFTY(j[i + deviant]);
//						if (i != 0) {
//							mPaint.setColor(colorAvlData30);
//							mCanvas.drawLine(lastX, jY, teamLastX, tempJY,
//									mPaint);
//						}
//						jY = tempJY;
//					}
//
//				}
//				break;
//			case 3:
//				// 绘制RIS相对强弱指标
//				if (rsiMap != null) {
//					mPaint.setStrokeWidth(dip2px(1));
//					float tempRsi6 = getCutoffFTY(rsi6[deviant + i] / 100.0);
//					float tempRsi12 = getCutoffFTY(rsi12[deviant + i] / 100.0);
//					float tempRsi24 = getCutoffFTY(rsi24[deviant + i] / 100.0);
//					if (i != 0) {
//						if (rsi6 != null && rsi6[deviant + i - 1] >= 0) {
//							mPaint.setColor(colorAvlData5);
//							mCanvas.drawLine(lastX, rsi6Y, teamLastX, tempRsi6,
//									mPaint);
//
//						}
//						if (rsi12 != null && rsi12[deviant + i - 1] >= 0) {
//							mPaint.setColor(colorAvlData10);
//							mCanvas.drawLine(lastX, rsi12Y, teamLastX,
//									tempRsi12, mPaint);
//
//						}
//						if (rsi24 != null && rsi24[deviant + i - 1] >= 0) {
//							mPaint.setColor(colorAvlData30);
//							mCanvas.drawLine(lastX, rsi24Y, teamLastX,
//									tempRsi24, mPaint);
//						}
//					}
//					rsi6Y = tempRsi6;
//					rsi12Y = tempRsi12;
//					rsi24Y = tempRsi24;
//
//				}
//				break;
//			case 4:
//				// BIAS线
//				if (biasMap != null) {
//					int bias1 = Integer.valueOf(TargetManager.target_value
//							.get(TargetManager.TARGET_BIAS)[0]);
//					int bias2 = Integer.valueOf(TargetManager.target_value
//							.get(TargetManager.TARGET_BIAS)[1]);
//					int bias3 = Integer.valueOf(TargetManager.target_value
//							.get(TargetManager.TARGET_BIAS)[2]);
//					if (bias6 != null) {
//						float tempBias6Y = getCutoffFTY(bias6[i + deviant]);
//						if (i + deviant >= bias1 && i != 0) {
//							mPaint.setColor(colorAvlData5);
//							if (bias6Y > 0) {
//								mCanvas.drawLine(teamLastX, tempBias6Y, lastX,
//										bias6Y, mPaint);
//							} else {
//								mCanvas.drawLine(teamLastX, tempBias6Y + 2,
//										teamLastX, tempBias6Y + 3, mPaint);
//							}
//						}
//						bias6Y = tempBias6Y;
//					}
//
//					if (bias12 != null) {
//						float tempBias12Y = getCutoffFTY(bias12[i + deviant]);
//						if (i + deviant >= bias2 && i != 0) {
//							mPaint.setColor(colorAvlData10);
//							if (bias12Y > 0) {
//								mCanvas.drawLine(teamLastX, tempBias12Y, lastX,
//										bias12Y, mPaint);
//							} else {
//								mCanvas.drawLine(teamLastX, tempBias12Y + 2,
//										teamLastX, tempBias12Y + 3, mPaint);
//							}
//						}
//						bias12Y = tempBias12Y;
//					}
//
//					if (bias24 != null) {
//						float tempBias24Y = getCutoffFTY(bias24[i + deviant]);
//						if (i + deviant >= bias3 && i != 0) {
//							mPaint.setColor(colorAvlData30);
//							if (bias12Y > 0) {
//								mCanvas.drawLine(teamLastX, tempBias24Y, lastX,
//										bias24Y, mPaint);
//							} else {
//								mCanvas.drawLine(teamLastX, tempBias24Y + 2,
//										teamLastX, tempBias24Y + 3, mPaint);
//							}
//						}
//						bias24Y = tempBias24Y;
//					}
//				}
//				break;
//			case 5:
//				// CCI线
//				if (cci != null) {
//					int cciValue = Integer.valueOf(TargetManager.target_value
//							.get(TargetManager.TARGET_CCI)[0]);
//					float tempCCIY = getCutoffFTY(cci[i + deviant]);
//					if (i != 0 && i + deviant >= cciValue) {
//						mPaint.setColor(colorAvlData5);
//						if (cciY > 0) {
//							mCanvas.drawLine(teamLastX, tempCCIY, lastX, cciY,
//									mPaint);
//						} else {
//							mCanvas.drawLine(teamLastX, tempCCIY + 2,
//									teamLastX, tempCCIY + 3, mPaint);
//						}
//					}
//					cciY = tempCCIY;
//				}
//				break;
//			}
//			if (isShowIndicateLine && scollX >= startX
//					&& scollX < startX + kLWidthArray[kLWidthSub]) {
//				scollX = teamLastX;
//				indicateLineIndex = i;
//				indicateLineY = closeY;
//			}
//			lastY5 = avgY5;
//			lastY10 = avgY10;
//			lastY30 = avgY30;
//			lastX = teamLastX;
//			startX += kLWidthArray[kLWidthSub];
//
//		}
//
//		// 显示弹出框
//		if (isShowIndicateLine) {
//			mPaint.setStrokeWidth(dip2px(1));
//			mPaint.setColor(indexLineColor);
//			mCanvas.drawLine(scollX, margin + topHeight - dip2px(1), scollX,
//					margin * 3 + topHeight + kLCanvasHeight + centerHeight
//							+ bottomAxisHeight + figureHeight, mPaint);
//			mCanvas.drawLine(margin + leftAxisWidth, indicateLineY, canvasWidth
//					- margin, indicateLineY, mPaint);
//
//			int textColor = popupBorderColor;
//			float statrtX;
//			float statrtY;
//			if (indicateLineIndex < valueStock / 2) {
//				statrtX = canvasWidth - margin - popupBorderHeight - dip2px(2);
//			} else {
//				statrtX = margin + leftAxisWidth + dip2px(2);
//			}
//			mPaint.setStrokeWidth(dip2px(0.7f));
//			mPaint.setStyle(Paint.Style.FILL);
//			mPaint.setColor(Color.WHITE);
//			statrtY = margin + topHeight + dip2px(1);
//			mCanvas.drawRect(statrtX, statrtY, statrtX + popupBorderHeight,
//					statrtY + popupBorderHeight, mPaint);
//			mPaint.setStyle(Paint.Style.STROKE);
//
//			mPaint.setColor(popupBorderColor);
//			mCanvas.drawRect(statrtX, statrtY, statrtX + popupBorderHeight,
//					statrtY + popupBorderHeight, mPaint);
//			KLineModel mKLineModel = showKLineModelList.get(indicateLineIndex);
//			float popupBorderTabHeight = (popupBorderHeight - 1) / 6;
//			mCanvas.drawLine(statrtX, statrtY + popupBorderTabHeight
//					+ dip2px(0.5f), statrtX + popupBorderHeight, statrtY
//					+ popupBorderTabHeight + dip2px(0.5f), mPaint);
//			statrtY = statrtY + popupBorderTabHeight - dip2px(2);
//			String timeType = "yyyy-MM-dd";
//			if (!isDay) {
//				timeType = "MM-dd HH:mm";
//			}
//			setText(getMinutes(mKLineModel.getTime(), timeType), statrtX
//					+ popupBorderHeight / 2, statrtY, mCanvas,
//					Paint.Align.CENTER, popupBorderColor, 10);
//
//			statrtY = statrtY + popupBorderTabHeight + dip2px(1);
//			setText("开：", statrtX + dip2px(2), statrtY, mCanvas,
//					Paint.Align.LEFT, popupBorderColor, 10);
//			float open = mKLineModel.getOpen(); // 开盘价
//			float close = mKLineModel.getClose(); // 收盘价
//			float high = mKLineModel.getHigh(); // 最高价
//			float low = mKLineModel.getLow(); // 最低价
//			float volume = mKLineModel.getVolume(); // 成交量
//			float amount = mKLineModel.getAmount(); // 成交额
//			float lastClose = mKLineModel.getLastClose();
//			if (open > lastClose) {
//				textColor = colorRise;
//			} else if (open < lastClose) {
//				textColor = colorFall;
//			} else {
//				textColor = popupBorderColor;
//			}
//			setText(getTwoStep(open / priceWeight), statrtX + popupBorderHeight
//					- dip2px(2), statrtY, mCanvas, Paint.Align.RIGHT,
//					textColor, 10);
//
//			statrtY += popupBorderTabHeight;
//			setText("高：", statrtX + dip2px(2), statrtY, mCanvas,
//					Paint.Align.LEFT, popupBorderColor, 10);
//			if (high > lastClose) {
//				textColor = colorRise;
//			} else if (high < lastClose) {
//				textColor = colorFall;
//			} else {
//				textColor = popupBorderColor;
//			}
//			setText(getTwoStep(high / priceWeight), statrtX + popupBorderHeight
//					- dip2px(2), statrtY, mCanvas, Paint.Align.RIGHT,
//					textColor, 10);
//
//			mPaint.setColor(popupBorderColor);
//			statrtY += popupBorderTabHeight;
//
//			setText("低：", statrtX + dip2px(2), statrtY, mCanvas,
//					Paint.Align.LEFT, popupBorderColor, 10);
//			if (low > lastClose) {
//				textColor = colorRise;
//			} else if (low < lastClose) {
//				textColor = colorFall;
//			} else {
//				textColor = popupBorderColor;
//			}
//			setText(getTwoStep(low / priceWeight), statrtX + popupBorderHeight
//					- dip2px(2), statrtY, mCanvas, Paint.Align.RIGHT,
//					textColor, 10);
//
//			mPaint.setColor(popupBorderColor);
//			statrtY += popupBorderTabHeight;
//			setText("收：", statrtX + dip2px(2), statrtY, mCanvas,
//					Paint.Align.LEFT, popupBorderColor, 10);
//			if (close > lastClose) {
//				textColor = colorRise;
//			} else if (close < lastClose) {
//				textColor = colorFall;
//			} else {
//				textColor = popupBorderColor;
//			}
//			setText(getTwoStep(close / priceWeight), statrtX
//					+ popupBorderHeight - dip2px(2), statrtY, mCanvas,
//					Paint.Align.RIGHT, textColor, 10);
//			mPaint.setColor(popupBorderColor);
//			statrtY += popupBorderTabHeight;
//			setText("涨：", statrtX + dip2px(2), statrtY, mCanvas,
//					Paint.Align.LEFT, popupBorderColor, 10);
//			float priceChangeRatio = close / lastClose - 1; // 涨跌幅
//			if (priceChangeRatio > 0) {
//				textColor = colorRise;
//			} else if (priceChangeRatio < 0) {
//				textColor = colorFall;
//			} else {
//				textColor = popupBorderColor;
//			}
//			setText(getTwoStep(priceChangeRatio * 100) + "%", statrtX
//					+ popupBorderHeight - dip2px(2), statrtY, mCanvas,
//					Paint.Align.RIGHT, textColor, 10);
//			drawTop(mCanvas, kLineModelList.size() - indicateLineIndex
//					- deviant);
//			drawCenter(mCanvas, kLineModelList.size() - indicateLineIndex
//					- deviant);
//		} else {
//			drawTop(mCanvas, 1);
//			drawCenter(mCanvas, 1);
//		}
//	}
//
//	/**
//	 * 绘制头部M5等一些的内容
//	 */
//	private void drawTop(Canvas mCanvas, int indicateLineIndex) {
//		String titleM5;
//		String titleM10;
//		String titleM30;
//		if (TargetManager.TARGET_HEADER_INDEX == 0) {
//			titleM5 = "M"
//					+ TargetManager.target_value.get(TargetManager.TARGET_MA)[0]
//					+ ":";
//			titleM10 = "M"
//					+ TargetManager.target_value.get(TargetManager.TARGET_MA)[1]
//					+ ":";
//			titleM30 = "M"
//					+ TargetManager.target_value.get(TargetManager.TARGET_MA)[2]
//					+ ":";
//		} else {
//			titleM5 = "M:";
//			titleM10 = "U:";
//			titleM30 = "L:";
//		}
//		if (initAverageData5 != null
//				&& initAverageData5.length > initAverageData5.length
//						- indicateLineIndex
//				&& initAverageData5[initAverageData5.length - indicateLineIndex] > 0) {
//			setText(titleM5
//					+ getTwoStep(initAverageData5[initAverageData5.length
//							- indicateLineIndex]
//							/ priceWeight), leftAxisWidth + margin, margin
//					+ topHeight - dip2px(10), mCanvas, Paint.Align.LEFT,
//					colorAvlData5, 12);
//		} else {
//			setText(titleM5 + "--", leftAxisWidth + margin, margin + topHeight
//					- dip2px(10), mCanvas, Paint.Align.LEFT, colorAvlData5, 12);
//		}
//		if (initAverageData10 != null
//				&& initAverageData10.length > initAverageData10.length
//						- indicateLineIndex
//				&& initAverageData10[initAverageData10.length
//						- indicateLineIndex] > 0) {
//			setText(titleM10
//					+ getTwoStep(initAverageData10[initAverageData10.length
//							- indicateLineIndex]
//							/ priceWeight),
//					leftAxisWidth + margin + dip2px(80), margin + topHeight
//							- dip2px(10), mCanvas, Paint.Align.LEFT,
//					colorAvlData10, 12);
//		} else {
//			setText(titleM10 + "--", leftAxisWidth + margin + dip2px(80),
//					margin + topHeight - dip2px(10), mCanvas, Paint.Align.LEFT,
//					colorAvlData10, 12);
//		}
//		if (initAverageData30 != null
//				&& initAverageData30.length > initAverageData30.length
//						- indicateLineIndex
//				&& initAverageData30[initAverageData30.length
//						- indicateLineIndex] > 0) {
//			setText(titleM30
//					+ getTwoStep(initAverageData30[initAverageData30.length
//							- indicateLineIndex]
//							/ priceWeight), leftAxisWidth + margin
//					+ dip2px(160), margin + topHeight - dip2px(10), mCanvas,
//					Paint.Align.LEFT, colorAvlData30, 12);
//		} else {
//			setText(titleM30 + "--", leftAxisWidth + margin + dip2px(160),
//					margin + topHeight - dip2px(10), mCanvas, Paint.Align.LEFT,
//					colorAvlData30, 12);
//		}
//		if (divideIndex < 3) {
//			setText(divideExcepts[divideIndex], canvasWidth - margin
//					- dip2px(15), margin + topHeight - dip2px(10), mCanvas,
//					Paint.Align.RIGHT, Color.BLACK, 12);
//			drawImage(mCanvas, (int) (margin + topHeight - dip2px(19)),
//					(int) (margin + topHeight - dip2px(11)), (int) (canvasWidth
//							- margin - dip2px(12)),
//					(int) (canvasWidth - margin), R.drawable.stock_down,
//					Color.WHITE);
//		}
//	}
//
//	/**
//	 * 绘制头部VOL等一些的内容
//	 */
//	private void drawCenter(Canvas mCanvas, int indicateLineIndex) {
//		mPaint.setColor(colorCenterBg);
//		mPaint.setStyle(Paint.Style.FILL);
//		mCanvas.drawRect(0, margin * 2 + topHeight + kLCanvasHeight
//				+ bottomAxisHeight, canvasWidth, margin * 2 + topHeight
//				+ kLCanvasHeight + bottomAxisHeight + centerHeight, mPaint);
//		float textY = margin * 2 + topHeight + kLCanvasHeight
//				+ bottomAxisHeight; // Y的坐标
//		setText(targetStrs[TargetManager.TARGET_FOOTER_INDEX], margin
//				+ dip2px(25), textY + dip2px(20), mCanvas, Paint.Align.RIGHT,
//				Color.BLACK, 12);
//		drawImage(mCanvas, (int) (textY + centerHeight / 2 - dip2px(4)),
//				(int) (textY + centerHeight / 2 + dip2px(4)),
//				(int) (margin + dip2px(30)), (int) (margin + dip2px(42)),
//				R.drawable.stock_down, colorCenterBg);
//		switch (TargetManager.TARGET_FOOTER_INDEX) {
//		case 0:
//			if (kLineModelList.size() >= indicateLineIndex) {
//				float volume = kLineModelList.get(
//						kLineModelList.size() - indicateLineIndex).getVolume() / 100.0f;
//				String volumeStr = "VOL:";
//				if (volume > 100000000) {
//					volumeStr = volumeStr + getTwoStep(volume / 100000000)
//							+ "亿";
//				} else if (volume > 10000) {
//					volumeStr = volumeStr + getTwoStep(volume / 10000) + "万";
//				} else {
//					volumeStr = volumeStr + getTwoStep(volume);
//				}
//				setText(volumeStr, margin + dip2px(50), textY + dip2px(20),
//						mCanvas, Paint.Align.LEFT, Color.BLACK, 12);
//			}
//			break;
//		case 1:
//			if (macdMap != null) {
//				float[] dea = macdMap.get("dea");
//				float[] diff = macdMap.get("diff");
//				if (diff != null && diff.length >= indicateLineIndex) {
//					setText("DIF:"
//							+ getTwoStep(diff[diff.length - indicateLineIndex]
//									/ priceWeight), margin + dip2px(50), textY
//							+ dip2px(20), mCanvas, Paint.Align.LEFT,
//							colorAvlData30, 12);
//				}
//				if (dea != null && dea.length >= indicateLineIndex) {
//					setText("DEA:"
//							+ getTwoStep(dea[dea.length - indicateLineIndex]
//									/ priceWeight), margin + dip2px(120), textY
//							+ dip2px(20), mCanvas, Paint.Align.LEFT,
//							colorAvlData10, 12);
//				}
//			}
//			break;
//		case 2:
//			if (kdjMap != null) {
//				int[] k = kdjMap.get("k");
//				int[] d = kdjMap.get("d");
//				int[] j = kdjMap.get("j");
//				if (k != null && k.length >= indicateLineIndex) {
//					setText("K:"
//							+ getTwoStep(k[k.length - indicateLineIndex] / 100.0f),
//							margin + dip2px(50), textY + dip2px(20), mCanvas,
//							Paint.Align.LEFT, colorAvlData5, 11);
//				}
//				if (d != null && d.length >= indicateLineIndex) {
//					setText("D:"
//							+ getTwoStep(d[d.length - indicateLineIndex] / 100.0f),
//							margin + dip2px(100), textY + dip2px(20), mCanvas,
//							Paint.Align.LEFT, colorAvlData10, 11);
//				}
//				if (j != null && j.length >= indicateLineIndex) {
//					setText("J:"
//							+ getTwoStep(j[j.length - indicateLineIndex] / 100.0f),
//							margin + dip2px(150), textY + dip2px(20), mCanvas,
//							Paint.Align.LEFT, colorAvlData30, 11);
//				}
//			}
//			break;
//		case 3:
//			if (rsiMap != null) {
//				int[] rsi6 = rsiMap.get("rsi6");
//				int[] rsi12 = rsiMap.get("rsi12");
//				int[] rsi24 = rsiMap.get("rsi24");
//				if (rsi6 != null && rsi6.length >= indicateLineIndex) {
//					setText(TargetManager.target_value
//							.get(TargetManager.TARGET_RSI)[0]
//							+ ":"
//							+ getTwoStep(rsi6[rsi6.length - indicateLineIndex] / 100.0f),
//							margin + dip2px(50), textY + dip2px(20), mCanvas,
//							Paint.Align.LEFT, colorAvlData5, 11);
//				}
//				if (rsi12 != null && rsi12.length >= indicateLineIndex) {
//					setText(TargetManager.target_value
//							.get(TargetManager.TARGET_RSI)[1]
//							+ ":"
//							+ getTwoStep(rsi12[rsi12.length - indicateLineIndex] / 100.0f),
//							margin + dip2px(100), textY + dip2px(20), mCanvas,
//							Paint.Align.LEFT, colorAvlData10, 11);
//				}
//				if (rsi24 != null && rsi24.length >= indicateLineIndex) {
//					setText(TargetManager.target_value
//							.get(TargetManager.TARGET_RSI)[2]
//							+ ":"
//							+ getTwoStep(rsi24[rsi24.length - indicateLineIndex] / 100.0f),
//							margin + dip2px(150), textY + dip2px(20), mCanvas,
//							Paint.Align.LEFT, colorAvlData30, 11);
//				}
//			}
//			break;
//		case 4:
//			if (biasMap != null) {
//				int[] bias6 = biasMap.get("bias6");
//				int[] bias12 = biasMap.get("bias12");
//				int[] bias24 = biasMap.get("bias24");
//				if (bias6 != null && bias6.length >= indicateLineIndex) {
//					setText("b"
//							+ TargetManager.target_value
//									.get(TargetManager.TARGET_BIAS)[0]
//							+ ":"
//							+ getTwoStep(bias6[bias6.length - indicateLineIndex] / 100.0f),
//							margin + dip2px(50), textY + dip2px(20), mCanvas,
//							Paint.Align.LEFT, colorAvlData5, 11);
//				}
//				if (bias12 != null && bias12.length >= indicateLineIndex) {
//					setText("b"
//							+ TargetManager.target_value.get(TargetManager.TARGET_BIAS)[1]
//							+ ":"
//							+ getTwoStep(bias12[bias12.length
//									- indicateLineIndex] / 100.0f), margin
//							+ dip2px(100), textY + dip2px(20), mCanvas,
//							Paint.Align.LEFT, colorAvlData10, 11);
//				}
//				if (bias24 != null && bias24.length >= indicateLineIndex) {
//					setText("b"
//							+ TargetManager.target_value.get(TargetManager.TARGET_BIAS)[2]
//							+ ":"
//							+ getTwoStep(bias24[bias24.length
//									- indicateLineIndex] / 100.0f), margin
//							+ dip2px(150), textY + dip2px(20), mCanvas,
//							Paint.Align.LEFT, colorAvlData30, 11);
//				}
//			}
//			break;
//		case 5:
//			if (cciMap != null) {
//				int[] cci = cciMap.get("cci");
//				if (cci != null && cci.length > 0) {
//					setText("CCI:" + getTwoStep(cci[cci.length - 1] / 100.0),
//							margin + dip2px(50), textY + dip2px(20), mCanvas,
//							Paint.Align.LEFT, colorAvlData5, 11);
//				}
//			}
//		}
//		drawImage(mCanvas, (int) textY, (int) (textY + centerHeight),
//				(int) (canvasWidth - dip2px(49)), (int) canvasWidth,
//				R.drawable.stock_shrink, colorCenterBg); // 放大
//		drawImage(mCanvas, (int) textY, (int) (textY + centerHeight),
//				(int) (canvasWidth - dip2px(99)),
//				(int) (canvasWidth - dip2px(49)), R.drawable.stock_magnify,
//				colorCenterBg); // 缩小
//	}
//
//	public List<KLineModel> getkLineModelList() {
//		return kLineModelList;
//	}
//
//	public void cleanKLineModelList() {
//		kLineModelList.clear();
//		isMore = false;
//		initData();
//	}
//
//	public void setKLineModelList(List<KLineModel> kLineModelList) {
//		this.kLineModelList.clear();
//		this.kLineModelList.addAll(kLineModelList);
//		if (this.kLineModelList != null && this.kLineModelList.size() > 0) {
//			priceWeight = this.kLineModelList.get(0).getPriceWeight();
//			if (mOnClickSurfaceListener != null) {
//				mOnClickSurfaceListener.onUpdate(kLineModelList
//						.get(kLineModelList.size() - 1));
//			}
//		}
//		if (kLineModelList.size() % 480 == 0) {
//			isMore = true;
//		} else {
//			isMore = false;
//		}
//		initData();
//		invalidate();
//	}
//
//	public void setLleftDeviant(int leftDeviant) {
//		this.leftDeviant = leftDeviant;
//	}
//
//	public void addKLineModelList(List<KLineModel> kLineModelList) {
//		this.kLineModelList.addAll(0, kLineModelList);
//		if (this.kLineModelList != null && this.kLineModelList.size() > 0) {
//			priceWeight = this.kLineModelList.get(0).getPriceWeight();
//		}
//		if (kLineModelList.size() % 480 == 0) {
//			isMore = true;
//		} else {
//			isMore = false;
//		}
//		initData();
//		invalidate();
//	}
//
//	private void initData() {
//		valueStock = (int) (kLCanvasWidth / kLWidthArray[kLWidthSub]);
//		averageMap = null;
//		macdMap = null;
//		rsiMap = null;
//		kdjMap = null;
//		cciMap = null;
//		bollMap = null;
//		biasMap = null;
//	}
//
//	private float maxKL = 0; // 坐标最大值
//	private float minKL = 0; // 坐标最小值
//	private float maxFT = 0; // 坐标最大值
//	private float minFT = 0; // 坐标最小值
//
//	/**
//	 * 设置K线坐标的最大和最小值
//	 */
//	public void setKLMaxAndMin() {
//		if (showKLineModelList.size() == 0)
//			return;
//		minKL = showKLineModelList.get(0).getLow();
//		maxKL = showKLineModelList.get(0).getHigh();
//		minFT = 0;
//		maxFT = showKLineModelList.get(0).getVolume();
//		for (int i = 0; i < showKLineModelList.size(); i++) {
//			KLineModel mKLineModel = showKLineModelList.get(i);
//			minKL = minKL < mKLineModel.getLow() ? minKL : mKLineModel.getLow();
//			maxKL = maxKL > mKLineModel.getHigh() ? maxKL : mKLineModel
//					.getHigh();
//
//			// minFT = minFT < mKLineModel.getVolume() ? minFT :
//			// mKLineModel.getVolume();
//			maxFT = maxFT > mKLineModel.getVolume() ? maxFT : mKLineModel
//					.getVolume();
//			if (initAverageData5 != null
//					&& initAverageData5.length > i + deviant
//					&& initAverageData5[i + deviant] > 0) {
//				minKL = minKL < initAverageData5[i + deviant] ? minKL
//						: initAverageData5[i + deviant];
//				maxKL = maxKL > initAverageData5[i + deviant] ? maxKL
//						: initAverageData5[i + deviant];
//			}
//			if (initAverageData10 != null
//					&& initAverageData10.length > i + deviant
//					&& initAverageData10[i + deviant] > 0) {
//				minKL = minKL < initAverageData10[i + deviant] ? minKL
//						: initAverageData10[i + deviant];
//				maxKL = maxKL > initAverageData10[i + deviant] ? maxKL
//						: initAverageData10[i + deviant];
//			}
//			if (initAverageData30 != null
//					&& initAverageData30.length > i + deviant
//					&& initAverageData30[i + deviant] > 0) {
//				minKL = minKL < initAverageData30[i + deviant] ? minKL
//						: initAverageData30[i + deviant];
//				maxKL = maxKL > initAverageData30[i + deviant] ? maxKL
//						: initAverageData30[i + deviant];
//			}
//		}
//	}
//
//	/**
//	 * 设置副图坐标的最大和最小值
//	 */
//	public void setFTMaxAndMin(float[]... array) {
//		minFT = 0;
//		maxFT = 0;
//		if (array == null) {
//			return;
//		}
//		if (array[0] != null && array[0].length > 0) {
//			maxFT = array[0][deviant];
//			minFT = array[0][deviant];
//		}
//		for (int i = 0; i < array.length; i++) {
//			float[] s = array[i];
//			if (s == null) {
//				continue;
//			}
//			for (int ii = 0; ii < showKLineModelList.size(); ii++) {
//				minFT = minFT < s[ii + deviant] ? minFT : s[ii + deviant];
//				maxFT = maxFT > s[ii + deviant] ? maxFT : s[ii + deviant];
//			}
//		}
//	}
//
//	public void setFTMaxAndMin(long[]... array) {
//		minFT = 0;
//		maxFT = 0;
//		if (array == null) {
//			return;
//		}
//		if (array[0] != null && array[0].length > 0) {
//			maxFT = array[0][deviant];
//			minFT = array[0][deviant];
//		}
//		for (int i = 0; i < array.length; i++) {
//			long[] s = array[i];
//			if (s == null) {
//				continue;
//			}
//			for (int ii = 0; ii < showKLineModelList.size(); ii++) {
//				minFT = minFT < s[ii + deviant] ? minFT : s[ii + deviant];
//				maxFT = maxFT > s[ii + deviant] ? maxFT : s[ii + deviant];
//			}
//		}
//	}
//
//	public void setFTMaxAndMin(int[]... array) {
//		minFT = 0;
//		maxFT = 0;
//		if (array == null) {
//			return;
//		}
//		if (array[0] != null && array[0].length > 0) {
//			maxFT = array[0][deviant];
//			minFT = array[0][deviant];
//		}
//		for (int i = 0; i < array.length; i++) {
//			int[] s = array[i];
//			if (s == null) {
//				continue;
//			}
//			for (int ii = 0; ii < showKLineModelList.size(); ii++) {
//				minFT = minFT < s[ii + deviant] ? minFT : s[ii + deviant];
//				maxFT = maxFT > s[ii + deviant] ? maxFT : s[ii + deviant];
//			}
//		}
//	}
//
//	/**
//	 * 根据数据大小返回Y坐标
//	 */
//	private float getCutoffKLY(float price) {
//		float priceY = kLCanvasHeight + topHeight + margin - kLCanvasHeight
//				* (price - minKL) / (maxKL - minKL);
//		if (priceY < topHeight + margin)
//			priceY = topHeight + margin;
//		if (priceY > kLCanvasHeight + topHeight + margin)
//			priceY = kLCanvasHeight + topHeight + margin;
//		return priceY;
//	}
//
//	private float getCutoffFTY(double price) {
//		if (price > maxFT || price < minFT) {
//			Log.i("price", "price:" + price + ";maxFT:" + maxFT + ";minFT:"
//					+ minFT);
//		}
//		float priceY = (float) (margin * 3 + topHeight + kLCanvasHeight
//				+ centerHeight + bottomAxisHeight + figureHeight - figureHeight
//				* (price - minFT) / (maxFT - minFT));
//		if (priceY < topHeight + margin)
//			priceY = topHeight + margin;
//		if (priceY > margin * 3 + topHeight + kLCanvasHeight + centerHeight
//				+ bottomAxisHeight + figureHeight)
//			priceY = margin * 3 + topHeight + kLCanvasHeight + centerHeight
//					+ bottomAxisHeight + figureHeight;
//		return priceY;
//	}
//
//	private class GestureListener extends
//			GestureDetector.SimpleOnGestureListener {
//
//		/**
//		 * 双击down的时候触发
//		 */
//		public boolean onDoubleTap(MotionEvent e) {
//			if (null == e)
//				return false;
//			float textY = margin * 2 + topHeight + kLCanvasHeight
//					+ bottomAxisHeight; // Y的坐标
//			if (e.getX() > canvasWidth - dip2px(49)
//					&& e.getY() > textY - dip2px(15)
//					&& e.getY() < textY + dip2px(45)) {
//				// 缩放
//				if (kLWidthSub > 0) {
//					kLWidthSub--;
//					valueStock = (int) (kLCanvasWidth / kLWidthArray[kLWidthSub]); // 修改显示数量
//					int centerDeviant = (showKLineModelList.size() + valueStock)
//							/ 2 + 1 + deviant; // 偏移后最后一个坐标点的坐标
//					if (centerDeviant < kLineModelList.size()) {
//						leftDeviant = kLineModelList.size() - centerDeviant;
//					} else {
//						leftDeviant = 0;
//					}
//					invalidate();
//				}
//				return true;
//			}
//			if (e.getX() > canvasWidth - dip2px(99)
//					&& e.getX() < canvasWidth - dip2px(49)
//					&& e.getY() > textY - dip2px(15)
//					&& e.getY() < textY + dip2px(45)) {
//				// 放大
//				if (kLWidthSub < kLWidthArray.length - 1) {
//					kLWidthSub++;
//					valueStock = (int) (kLCanvasWidth / kLWidthArray[kLWidthSub]); // 修改显示数量
//					int centerDeviant = (showKLineModelList.size() + valueStock)
//							/ 2 + 1 + deviant;
//					if (centerDeviant < kLineModelList.size()) {
//						leftDeviant = kLineModelList.size() - centerDeviant;
//					} else {
//						leftDeviant = 0;
//					}
//					invalidate();
//				}
//				return true;
//			}
//
//			kLWidthSub = (kLWidthSub + 1) % 5;
//			valueStock = (int) (kLCanvasWidth / kLWidthArray[kLWidthSub]); // 修改显示数量
//			int centerDeviant = (showKLineModelList.size() + valueStock) / 2
//					+ 1 + deviant; // 偏移后最后一个坐标点的坐标
//			if (centerDeviant < kLineModelList.size()) {
//				leftDeviant = kLineModelList.size() - centerDeviant;
//			} else {
//				leftDeviant = 0;
//			}
//			invalidate();
//			return true;
//		}
//
//		//
//		// /**
//		// * 双击的第二下 down和up都会触发，可用e.getAction()区分。
//		// *
//		// * @param e
//		// * @return
//		// */
//		// @Override
//		// public boolean onDoubleTapEvent(MotionEvent e) {
//		// return super.onDoubleTapEvent(e);
//		// }
//
//		/**
//		 * down时触发
//		 *
//		 * @param e
//		 * @return
//		 */
//		@Override
//		public boolean onDown(MotionEvent e) {
//			return super.onDown(e);
//		}
//
//		/**
//		 * Touch了滑动一点距离后，up时触发。
//		 *
//		 * @param e1
//		 * @param e2
//		 * @param velocityX
//		 * @param velocityY
//		 * @return
//		 */
//		@Override
//		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//				float velocityY) {
//			return super.onFling(e1, e2, velocityX, velocityY);
//		}
//
//		/**
//		 * Touch了不移动一直 down时触发
//		 *
//		 * @param e
//		 */
//		@Override
//		public void onLongPress(MotionEvent e) {
//			super.onLongPress(e);
//		}
//
//		/**
//		 * Touch了滑动时触发
//		 */
//		@Override
//		public boolean onScroll(MotionEvent e1, MotionEvent e2,
//				float distanceX, float distanceY) {
//			// if(Math.abs(distanceX)<50){
//			// return super.onScroll(e1, e2, distanceX, distanceY);
//			// }
//			if (isShowIndicateLine) {
//				float x = e2.getX();
//				if (x >= margin + leftAxisWidth
//						&& x <= lastX + kLWidthArray[kLWidthSub] / 2) {
//					scollX = x;
//					invalidate();
//				}
//			} else {
//				int addDeviant = (int) -Math
//						.rint((distanceX / kLWidthArray[kLWidthSub]));
//				if (distanceX < 0) {
//					// 往左滑动
//					// addDeviant+=1;
//					if (leftDeviant + valueStock < kLineModelList.size()) {
//						if (leftDeviant + addDeviant + valueStock < kLineModelList
//								.size()) {
//							leftDeviant += addDeviant;
//						} else {
//							leftDeviant = kLineModelList.size() - valueStock;
//						}
//						invalidate();
//					}
//					if (isMore
//							&& kLineModelList.size() != 0
//							&& leftDeviant + valueStock > kLineModelList.size() - 30) {
//						// new Thread(new Runnable() {
//						// @Override
//						// public void run() {
//						isMore = false;
//						mOnClickSurfaceListener
//								.onDownload(getNextDay(kLineModelList.get(0)
//										.getTime()));
//
//						// }
//
//						// }).start();
//
//					}
//				} else {
//					// addDeviant-=1;
//					// 往右滑动
//					if (leftDeviant > 0) {
//						if (leftDeviant > -addDeviant) {
//							leftDeviant += addDeviant;
//						} else {
//							leftDeviant = 0;
//						}
//						invalidate();
//					}
//				}
//			}
//			return super.onScroll(e1, e2, distanceX, distanceY);
//		}
//
//		/**
//		 * Touch了还没有滑动时触发
//		 */
//		@Override
//		public void onShowPress(MotionEvent e) {
//			super.onShowPress(e);
//		}
//
//		// 单击不滑动
//		@Override
//		public boolean onSingleTapConfirmed(MotionEvent e) {
//			float textY = margin * 2 + topHeight + kLCanvasHeight
//					+ bottomAxisHeight; // Y的坐标
//			if (e.getX() > canvasWidth - dip2px(49)
//					&& e.getY() > textY - dip2px(15)
//					&& e.getY() < textY + dip2px(45)) {
//				// 缩放
//				if (kLWidthSub > 0) {
//					kLWidthSub--;
//					valueStock = (int) (kLCanvasWidth / kLWidthArray[kLWidthSub]); // 修改显示数量
//					int centerDeviant = (showKLineModelList.size() + valueStock)
//							/ 2 + 1 + deviant; // 偏移后最后一个坐标点的坐标
//					if (centerDeviant < kLineModelList.size()) {
//						leftDeviant = kLineModelList.size() - centerDeviant;
//					} else {
//						leftDeviant = 0;
//					}
//					invalidate();
//				}
//			} else if (e.getX() > canvasWidth - dip2px(99)
//					&& e.getX() < canvasWidth - dip2px(49)
//					&& e.getY() > textY - dip2px(15)
//					&& e.getY() < textY + dip2px(45)) {
//				// 放大
//				if (kLWidthSub < kLWidthArray.length - 1) {
//					kLWidthSub++;
//					valueStock = (int) (kLCanvasWidth / kLWidthArray[kLWidthSub]); // 修改显示数量
//					int centerDeviant = (showKLineModelList.size() + valueStock)
//							/ 2 + 1 + deviant;
//					if (centerDeviant < kLineModelList.size()) {
//						leftDeviant = kLineModelList.size() - centerDeviant;
//					} else {
//						leftDeviant = 0;
//					}
//					invalidate();
//				}
//			} else if (e.getY() < margin + topHeight
//					&& e.getX() > canvasWidth - margin - dip2px(50)) {
//				if (divideIndex < 3 && mOnClickSurfaceListener != null) {
//					mOnClickSurfaceListener.onFuQuanClick(); // 点击复权的按钮
//
//				}
//			} else if (e.getX() > margin && e.getX() < margin + dip2px(50)
//					&& e.getY() > textY - dip2px(15)
//					&& e.getY() < textY + dip2px(45)) {
//				if (mOnClickSurfaceListener != null) {
//					mOnClickSurfaceListener.onVOLClick(); // 点击VOL的按钮
//
//				}
//			} else if (isScreen
//					&& e.getX() > margin + leftAxisWidth
//					&& e.getX() <= margin + leftAxisWidth
//							+ kLWidthArray[kLWidthSub]
//							* showKLineModelList.size()) {
//				if (!isShowIndicateLine) {
//					// 显示弹出框
//					isShowIndicateLine = true;
//					scollX = e.getX();
//					invalidate();
//				} else {
//					isShowIndicateLine = false;
//					invalidate();
//				}
//			} else {
//				mOnClickSurfaceListener.onKLClick(); // 点击K线图，去全屏显示K线图
//
//			}
//			return super.onSingleTapConfirmed(e);
//		}
//
//		@Override
//		public boolean onSingleTapUp(MotionEvent e) {
//			return super.onSingleTapUp(e);
//		}
//	}
//
//	private Date lastDate; // 上一个时间
//
//	public String getStockDate(String time) {
//		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//		String newTime = "";
//		try {
//			Date date = df.parse(time);
//			if (lastDate == null || !isSameYear(date, lastDate)) {
//				SimpleDateFormat newDf = new SimpleDateFormat("yyyy/MM/dd");
//				newTime = newDf.format(date);
//			} else {
//				SimpleDateFormat newDf = new SimpleDateFormat("MM/dd");
//				newTime = newDf.format(date);
//			}
//			lastDate = date;
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		return newTime;
//	}
//
//	public void setOnClickSurfaceListener(OnClickSurfaceListener l) {
//		mOnClickSurfaceListener = l;
//	}
//
//	public interface OnClickSurfaceListener {
//		void onFuQuanClick();
//
//		void onVOLClick();
//
//		void onDownload(String time);
//
//		void onKLClick();
//
//		void onUpdate(KLineModel newKLineModel);
//	}
//
//	private boolean isMore = true;
//
//	/**
//	 * 设置左滑动的时候是否需要加载更多
//	 */
//	public void IsMore(boolean isMore) {
//		this.isMore = isMore;
//	}
//
//	public void onRefresh() {
//		leftDeviant = 0;
//		initData();
//		invalidate();
//	}
//
//	public int getDivideIndex() {
//		return divideIndex;
//	}
//
//	public void setDivideIndex(int divideIndex) {
//		if (isHideDivide) {
//			this.divideIndex = 3;
//		} else {
//			this.divideIndex = divideIndex;
//		}
//	}
//
//	public boolean isHideDivide() {
//		return isHideDivide;
//	}
//
//	public void setHideDivide(boolean isHideDivide) {
//		this.isHideDivide = isHideDivide;
//		if (isHideDivide) {
//			this.divideIndex = 3;
//		} else {
//			isDay = true;
//			this.divideIndex = 2;
//		}
//	}
//
//	public boolean isDay() {
//		return isDay;
//	}
//
//	public void setDay(boolean isDay) {
//		this.isDay = isDay;
//		if (!isDay) {
//			setHideDivide(true);
//		}
//	}
//
//}
