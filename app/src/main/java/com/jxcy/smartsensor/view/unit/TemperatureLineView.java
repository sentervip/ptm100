package com.jxcy.smartsensor.view.unit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.jxcy.smartsensor.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TemperatureLineView extends View {
    //是否为历史折线图
    private boolean isHistory = false;
    //viewType 1 : 默认图;2 历史记录图；3:历史详情记录
    private int viewType = 1;
    //xy坐标轴颜色
    private int xyLineColor = 0xffe2e2e2;
    //xy坐标轴宽度
    private int xyLineWidth = dpToPx(1);
    //xy坐标轴文字颜色
    private int xyTextColor = 0xff7e7e7e;
    //xy坐标轴文字大小
    private int xyTextSize = spToPx(12);
    //折线图中折线的颜色
    private int lineColor = 0xff02bbb7;
    //x轴各个坐标点水平间距
    private int interval = dpToPx(5);
    //背景颜色
    private int bgColor = 0xffffffff;
    //是否在ACTION_UP时，根据速度进行自滑动，没有要求，建议关闭，过于占用GPU
    private boolean isScroll = false;
    //绘制XY轴坐标对应的画笔
    private Paint xyPaint;
    //绘制XY轴的文本对应的画笔
    private Paint xyTextPaint;
    //画折线对应的画笔
    private Paint linePaint;
    private int width;
    private int height;
    //x轴的原点坐标
    private int xOri;
    //y轴的原点坐标
    private int yOri;
    //第一个点X的坐标
    private float xInit;
    private float xLabelInit;
    //第一个点对应的最大Y坐标
    private float maxXInit;
    //第一个点对应的最小X坐标
    private float minXInit;
    private List<Temperature> tempList = new ArrayList<>();
    private List<HistoryTemperature> historyList = new ArrayList<>();
    private List<String> xLabels = new ArrayList<>(5);
    private Map<String, Float> historyXLabels = new HashMap<>();
    private List<Integer> pointArray = new ArrayList<>();
    //X轴刻度文本对应的最大矩形，为了选中时，在x轴文本画的框框大小一致
    private Rect xValueRect;
    private final int maxYLen = 8;
    private List<Float> yFixValue = new ArrayList<>();
    private GestureDetector gestureDetector;
    private boolean keepMove = false;
    private Canvas mCanvas;

    public TemperatureLineView(Context context) {
        this(context, null);
    }

    public TemperatureLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TemperatureLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gestureDetector = new GestureDetector(context, new MyOnGestureListener());
        init(context, attrs, defStyleAttr);
        initPaint();
    }

    /**
     * 初始化畫筆
     */
    private void initPaint() {
        xyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xyPaint.setAntiAlias(true);
        xyPaint.setStrokeWidth(xyLineWidth);
        xyPaint.setStrokeCap(Paint.Cap.ROUND);
        xyPaint.setColor(xyLineColor);

        xyTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xyTextPaint.setAntiAlias(true);
        xyTextPaint.setTextSize(xyTextSize);
        xyTextPaint.setStrokeCap(Paint.Cap.ROUND);
        xyTextPaint.setColor(xyTextColor);
        xyTextPaint.setStyle(Paint.Style.STROKE);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(xyLineWidth);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setColor(lineColor);
        linePaint.setStyle(Paint.Style.STROKE);

    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.chartView, defStyleAttr, 0);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.chartView_xylinecolor://xy坐标轴颜色
                    xyLineColor = array.getColor(attr, xyLineColor);
                    break;
                case R.styleable.chartView_xylinewidth://xy坐标轴宽度
                    xyLineWidth = (int) array.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, xyLineWidth, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.chartView_xytextcolor://xy坐标轴文字颜色
                    xyTextColor = array.getColor(attr, xyTextColor);
                    break;
                case R.styleable.chartView_xytextsize://xy坐标轴文字大小
                    xyTextSize = (int) array.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, xyTextSize, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.chartView_linecolor://折线图中折线的颜色
                    lineColor = array.getColor(attr, lineColor);
                    break;
                case R.styleable.chartView_interval://x轴各个坐标点水平间距
                    interval = (int) array.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, interval, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.chartView_bgcolor: //背景颜色
                    bgColor = array.getColor(attr, bgColor);
                    break;
                case R.styleable.chartView_isScroll://是否在ACTION_UP时，根据速度进行自滑动
                    isScroll = array.getBoolean(attr, isScroll);
                    break;
                case R.styleable.chartView_isHistory://是否为历史记录
                    isHistory = array.getBoolean(attr, isHistory);
                    break;
                case R.styleable.chartView_viewType://是否为历史详情
                    viewType = array.getInteger(attr, viewType);
                    break;
            }
        }
        array.recycle();
        for (int u = 0; u < maxYLen; u++) {
            yFixValue.add(35.5f + (u * 0.5f));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //这里需要确定几个基本点，只有确定了xy轴原点坐标，第一个点的X坐标值及其最大最小值
        if (changed) {
            width = getWidth();
            height = getHeight();
            //Y轴文本最大宽度
            float textYWdith = getTextBounds("40.00", xyTextPaint).width();
            int dp2 = dpToPx(2);
            int dp3 = dpToPx(3);
            //X轴文本最大高度
            xValueRect = getTextBounds("00:00:00", xyTextPaint);
            float textXHeight = xValueRect.height();
            xOri = (int) (dp2 + textYWdith + dp2 + xyLineWidth);//dp2是y轴文本距离左边，以及距离y轴的距离
            yOri = (int) (height - dp2 - textXHeight - dp3 - xyLineWidth);//dp3是x轴文本距离底边，dp2是x轴文本距离x轴的距离
            if (viewType == 1 || viewType == 3) {
                xInit = interval * 30 + xOri;
            } else {
                xInit = interval * 60 + xOri;
            }
            xLabelInit = xInit;
            maxXInit = xInit;
            for (int i = 0; i < tempList.size(); i++) {//求取x轴文本最大的高度
                String value = tempList.get(i).getDateString();
                if (value != null) {
                    Rect rect = getTextBounds(value, xyTextPaint);
                    if (rect.width() > xValueRect.width()) {
                        xValueRect = rect;
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        canvas.drawColor(bgColor);
        minXInit = width - (width - xOri) * 0.1f - interval * (tempList.size() - 1);//减去0.1f是因为最后一个X周刻度距离右边的长度为X轴可见长度的10%
        drawXY(canvas);
        drawBrokenLineAndPoint(canvas);
    }

    private void cleanCanvas(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    /**
     * 绘制折线和折线交点处对应的点
     *
     * @param canvas
     */
    private void drawBrokenLineAndPoint(Canvas canvas) {
        if (tempList.size() <= 0)
            return;
        //重新开一个图层
        int layerId = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        if (isHistory) {
            initHistoryXY();
            drawHistoryLine(canvas);
            drawHistoryPoint(canvas);
            drawHistoryXLabel(canvas);
        } else {
            drawBrokenLine(canvas);
            drawBrokenPoint(canvas);
        }
        // 将折线超出x轴坐标的部分截取掉
        Paint cleanPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cleanPaint.setAntiAlias(true);
        cleanPaint.setStyle(Paint.Style.FILL);
        cleanPaint.setColor(bgColor);
        cleanPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        RectF rectF = new RectF(0, 0, xOri, yOri);
        canvas.drawRect(rectF, cleanPaint);
        cleanPaint.setXfermode(null);
        //保存图层
        canvas.restoreToCount(layerId);
    }

    /**
     * 绘制折线对应的点
     *
     * @param canvas
     */
    private void drawHistoryPoint(Canvas canvas) {
        float dp2 = dpToPx(2);
        float dp4 = dpToPx(4);
        float dp7 = dpToPx(7);
        //绘制节点对应的原点
        for (int i = 0; i < tempList.size(); i++) {
            float x = tempList.get(i).getX_value();
            float y = tempList.get(i).getY_value();
            //绘制普通的节点
     /*       if (i == 0 || i == tempList.size() - 1) {
                linePaint.setColor(Color.RED);
            } else {
                linePaint.setColor(Color.WHITE);
            }*/

            for (int m = 0; m < pointArray.size(); m++) {
                if (i == pointArray.get(m)) {
                    linePaint.setColor(Color.GREEN);
                    break;
                } else {
                    linePaint.setColor(Color.WHITE);
                }
            }

            linePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(x, y, dp2, linePaint);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setColor(lineColor);
            canvas.drawCircle(x, y, dp2, linePaint);
        }
    }

    /**
     * 绘制折线对应的点
     *
     * @param canvas
     */
    private void drawBrokenPoint(Canvas canvas) {
        float dp2 = dpToPx(2);
        float dp4 = dpToPx(4);
        float dp7 = dpToPx(7);
        //绘制节点对应的原点
        for (int i = 0; i < tempList.size(); i++) {
            float x = xInit + interval * i;
            float y = tempList.get(i).getY_value();
            //绘制普通的节点
            linePaint.setStyle(Paint.Style.FILL);
            linePaint.setColor(Color.WHITE);
            canvas.drawCircle(x, y, dp2, linePaint);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setColor(lineColor);
            canvas.drawCircle(x, y, dp2, linePaint);
        }
    }

    /**
     * 绘制显示Y值的浮动框
     *
     * @param canvas
     * @param x
     * @param y
     * @param text
     */
    private void drawFloatTextBox(Canvas canvas, float x, float y, float text) {
        int dp6 = dpToPx(6);
        int dp18 = dpToPx(18);
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x - dp6, y - dp6);
        path.lineTo(x - dp18, y - dp6);
        path.lineTo(x - dp18, y - dp6 - dp18);
        path.lineTo(x + dp18, y - dp6 - dp18);
        path.lineTo(x + dp18, y - dp6);
        path.lineTo(x + dp6, y - dp6);
        path.lineTo(x, y);
        canvas.drawPath(path, linePaint);
        linePaint.setColor(Color.WHITE);
        linePaint.setTextSize(spToPx(14));
        Rect rect = getTextBounds(String.valueOf(text), linePaint);
        canvas.drawText(String.valueOf(text) + "", x - rect.width() / 2, y - dp6 - (dp18 - rect.height()) / 2, linePaint);
    }

    /**
     * 绘制折线
     *
     * @param canvas
     */
    private void drawBrokenLine(Canvas canvas) {
        cleanCanvas(canvas);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(lineColor);
        linePaint.setAntiAlias(true);
        //绘制折线
        Path path = new Path();
        for (int i = 0; i < tempList.size(); i++) {
            Temperature temperature = tempList.get(i);
            float x = xInit + interval * i;
            float y = temperature.getY_value();
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        canvas.drawPath(path, linePaint);
    }

    /**
     * 绘制曲线-曲线图
     *
     * @param canvas
     */
    private void drawHistoryLine(Canvas canvas) {
        cleanCanvas(canvas);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(lineColor);
        linePaint.setAntiAlias(true);
        Point startp;
        Point endp;
        for (int i = 0; i < tempList.size() - 1; i++) {
            if (i + 1 >= tempList.size()) break;
            Temperature curTemp = tempList.get(i);
            Temperature nextTemp = tempList.get(i + 1);
            startp = new Point((int) tempList.get(i).getX_value(), (int) (curTemp.getY_value()));
            endp = new Point((int) tempList.get(i + 1).getX_value(), (int) (nextTemp.getY_value()));
            int wt = (startp.x + endp.x) / 2;
            Point p3 = new Point();
            Point p4 = new Point();
            p3.y = startp.y;
            p3.x = wt;
            p4.y = endp.y;
            p4.x = wt;
            Path path = new Path();
            path.moveTo(startp.x, startp.y);
            path.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
            canvas.drawPath(path, linePaint);
        }
    }


    /**
     * 绘制XY坐标
     *
     * @param canvas
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawXY(Canvas canvas) {
        xyPaint.setStyle(Paint.Style.STROKE);
        drawY(canvas);
        drawX(canvas);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawX(Canvas canvas) {
        //绘制X轴坐标
        canvas.drawLine(xOri, yOri + xyLineWidth / 2, width, yOri + xyLineWidth / 2, xyPaint);
        xyPaint.setStyle(Paint.Style.STROKE);
        //绘制x轴刻度
        for (int i = 0; i < tempList.size(); i++) {
            float x = xInit + interval * i;
            if (viewType != 3) {
                if (x >= xOri && x <= width) {
                    xyTextPaint.setColor(xyTextColor);
                    tempList.get(i).setX_value(x);
                }
            } else {
                xyTextPaint.setColor(xyTextColor);
                tempList.get(i).setX_value(x);
            }
        }
        drawXLabel(canvas);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawXLabel(Canvas canvas) {
        int length = dpToPx(4);
        xyPaint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < xLabels.size(); i++) {
            float x = xLabelInit + (interval * 30) * i;
            String text = xLabels.get(i);
            if (text != null) {
                Rect rect = getTextBounds(text, xyTextPaint);
                canvas.drawText(text, 0, text.length(), x - rect.width() / 2, yOri + xyLineWidth + dpToPx(2) + rect.height(), xyTextPaint);
            }
            canvas.drawLine(x, yOri, x, yOri - length, xyPaint);
        }
    }


    private void drawHistoryXLabel(Canvas canvas) {
        int length = dpToPx(4);
        xyPaint.setStyle(Paint.Style.STROKE);
        Iterator<Map.Entry<String, Float>> iterator = (Iterator<Map.Entry<String, Float>>) historyXLabels.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Float> entity = iterator.next();
            String timeKey = entity.getKey();
            float x_value = entity.getValue();
            if (timeKey != null) {
                Rect rect = getTextBounds(timeKey, xyTextPaint);
                canvas.drawText(timeKey, 0, timeKey.length(), x_value - rect.width() / 2, yOri + xyLineWidth + dpToPx(2) + rect.height(), xyTextPaint);
            }
            canvas.drawLine(x_value, yOri, x_value, yOri - length, xyPaint);
        }
    }


    private void drawY(Canvas canvas) {
        int yLength = (int) (yOri * (1 - 0.1f) / maxYLen);//y轴上面空出10%,计算出y轴刻度间距
        for (int i = 0; i < tempList.size(); i++) {
            xyTextPaint.setColor(xyTextColor);
            float curValue = tempList.get(i).getTemper_value();
            Rect rect = getTextBounds(String.valueOf(curValue), xyTextPaint);
            float curY = yOri - (yLength * 0.5f) - (7 * yLength * (curValue - 35.5f) / 3.5f);
            if (curY >= yOri) {
                curY = yOri;
            } else if (curY <= 0) {
                curY = 0;
            }
            tempList.get(i).setY_value(curY);
        }

        for (int m = 0; m < yFixValue.size(); m++) {
            String fixY = String.valueOf(yFixValue.get(m));
            Rect rect = getTextBounds(fixY, xyTextPaint);
            canvas.drawText(fixY, 0, fixY.length(), xOri - xyLineWidth - dpToPx(2) - rect.width(), yOri - yLength * (m + 0.5f) + rect.height() / 2, xyTextPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.getParent().requestDisallowInterceptTouchEvent(true);//当该view获得点击事件，就请求父控件不拦截事件
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    clickAction(event);
                }
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        gestureDetector.onTouchEvent(event);
        return true;
    }

    /**
     * 手势事件
     */
    class MyOnGestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) { // 按下事件
            return false;
        }

        // 按下停留时间超过瞬时，并且按下时没有松开或拖动，就会执行此方法
        @Override
        public void onShowPress(MotionEvent motionEvent) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) { // 单击抬起
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isScroll) return true;
            if (e1.getX() > xOri && e1.getX() < width && e1.getY() < height) {
                //注意：这里的distanceX是e1.getX()-e2.getX()
                distanceX = -distanceX;
                int len = tempList.size();
                if (xInit + distanceX > width) {
                    xInit = width - 60 * interval;
                } else if (xInit + distanceX < -len * interval) {
                    xInit = -len * interval;
                } else {
                    xInit = (int) (xInit + distanceX);
                }
                xLabelInit = xInit;
                invalidate();
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
        } // 长按事件

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    /**
     * 点击X轴坐标或者折线节点
     *
     * @param event
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void clickAction(MotionEvent event) {
        int dp8 = dpToPx(8);
        float eventX = event.getX();
        float eventY = event.getY();
        /**
         * 绘画点坐标框
         */
        for (int i = 0; i < tempList.size(); i++) {
            float x = tempList.get(i).getX_value();
            float y = tempList.get(i).getY_value();
            if (eventX >= x - dp8 && eventX <= x + dp8 &&
                    eventY >= y - dp8 && eventY <= y + dp8) {//每个节点周围8dp都是可点击区域
                drawFloatTextBox(mCanvas, x, y, tempList.get(i).getTemper_value());
                invalidate();
                return;
            }
        }
        /**
         * 绘画x坐标框
         */
        for (int j = 0; j < xLabels.size(); j++) {
            String text = xLabels.get(j);
            if (text != null) {
                Rect xValueRect = getTextBounds(text, xyTextPaint);
                float x = xLabelInit + (interval * 30) * j;
                float y = yOri + xyLineWidth + dpToPx(2);
                if (eventX >= x - xValueRect.width() / 2 - dp8 && eventX <= x + xValueRect.width() + dp8 / 2 &&
                        eventY >= y - dp8 && eventY <= y + xValueRect.height() + dp8) {
                    xyTextPaint.setColor(lineColor);
                    mCanvas.drawRoundRect(x - xValueRect.width() / 2 - dpToPx(3), yOri + xyLineWidth + dpToPx(1), x + xValueRect.width() / 2 + dpToPx(3), yOri + xyLineWidth + dpToPx(2) + xValueRect.height() + dpToPx(2), dpToPx(2), dpToPx(2), xyTextPaint);
                    invalidate();
                    return;
                }
            }
        }
        Iterator<Map.Entry<String, Float>> iterator = historyXLabels.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Float> entry = iterator.next();
            String xKey = entry.getKey();
            float value = entry.getValue();
            if (xKey != null) {
                Rect xValueRect = getTextBounds(xKey, xyTextPaint);
                float x = value;
                float y = yOri + xyLineWidth + dpToPx(2);
                if (eventX >= x - xValueRect.width() / 2 - dp8 && eventX <= x + xValueRect.width() + dp8 / 2 &&
                        eventY >= y - dp8 && eventY <= y + xValueRect.height() + dp8) {
                    xyTextPaint.setColor(lineColor);
                    mCanvas.drawRoundRect(x - xValueRect.width() / 2 - dpToPx(3), yOri + xyLineWidth + dpToPx(1), x + xValueRect.width() / 2 + dpToPx(3), yOri + xyLineWidth + dpToPx(2) + xValueRect.height() + dpToPx(2), dpToPx(2), dpToPx(2), xyTextPaint);
                    invalidate();
                    return;
                }
            }
        }
    }

    public void setTemperature(List<Temperature> values) {
        tempList = values;
        invalidate();
    }

    /**
     * 历史记录数据初始化
     *
     * @param values
     */
    public void setHistoryTemperatures(List<HistoryTemperature> values) {
        historyList.clear();
        historyXLabels.clear();
        tempList.clear();
        historyList = values;
        initHistoryXY();
        invalidate();
    }

    private void initHistoryXY() {
        int len = historyList.size();
        if (historyList != null && len > 0) {
            tempList.clear();
            pointArray.clear();
            float end_x = 0;
            float start_x = 0;
            for (int j = 0; j < historyList.size(); j++) {
                HistoryTemperature historyData = historyList.get(j);
                List<Temperature> data = historyData.getData();
                String start_time = historyData.getStartTime();
                String end_time = historyData.getEndTime();
                if (data != null) {
                    int size = data.size();
                    if (j == 0) {
                        start_x = (float) (xOri + (j + 1) * xInit);
                        end_x = size * interval + start_x;
                        historyXLabels.put(start_time, start_x);
                        historyXLabels.put(end_time, end_x);

                        for (int m = 0; m < size; m++) {
                            Temperature temp = data.get(m);
                            temp.setX_value(m * interval + start_x);
                            tempList.add(temp);
                        }
                        pointArray.add(0);
                        pointArray.add(tempList.size());
                    } else {
                        if (viewType == 2) {
                            start_x = end_x + interval * 60 + xOri;
                        } else {
                            start_x = end_x + interval * 30 + xOri;
                        }
                        end_x = size * interval + start_x;
                        historyXLabels.put(start_time, start_x);
                        historyXLabels.put(end_time, end_x);

                        for (int m = 0; m < size; m++) {
                            Temperature temp = data.get(m);
                            temp.setX_value(m * interval + start_x);
                            tempList.add(temp);
                        }
                        pointArray.add(tempList.size());
                    }
                }
            }
        }
    }

    boolean moveFirst = true;
    int xCount;

    /**
     * 实时数据
     *
     * @param t
     */
    public void addTemperature(Temperature t) {
        synchronized (tempList) {
            int size = tempList.size();
            int d_value = moveFirst ? (size - 180) : (size - 210);
            if ((d_value > 0 && d_value < 30)) {
                keepMove = true;
                keepXAmation();
            } else if (d_value == 30) {
                keepMove = false;
                moveFirst = false;
                xLabelInit = xOri + interval * 30;
                addXLabels();
            } else if (d_value > 30) {
                tempList.remove(0);
                xCount++;
                if (xCount == 30) {
                    xLabelInit = xOri + interval * 30;
                    addXLabels();
                    xCount = 0;
                } else {
                    keepMove = true;
                    keepXLableAmation();
                }
            }
            if (size == 1) {
                moveFirst = true;
                addXLabels();
                interval = (int) ((width - xOri) / 180);
                xInit = interval * 30 + xOri;
                xLabelInit = xInit;
                maxXInit = xInit;
            }
            tempList.add(t);
            invalidate();
        }
    }

    /**
     * 设置x轴label
     */
    public void addXLabels() {
        if (tempList.size() > 0) {
            xLabels.clear();
            String firstLable = tempList.get(0).getDateString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            long first_time = 0;
            try {
                Date date = dateFormat.parse(firstLable);
                first_time = date.getTime();
                for (int j = 0; j <= 5; j++) {
                    long cur_time = first_time + 1000 * 60 * j;
                    String xValue = dateFormat.format(cur_time);
                    xLabels.add(xValue);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 设置x轴label
     */
    public void addNormalXLabels(List<String> xLabels) {
        this.xLabels = xLabels;
    }


    /**
     * 获取丈量文本的矩形
     *
     * @param text
     * @param paint
     * @return
     */
    private Rect getTextBounds(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    /**
     * dp转化成为px
     *
     * @param dp
     * @return
     */
    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f * (dp >= 0 ? 1 : -1));
    }

    /**
     * sp转化为px
     *
     * @param sp
     * @return
     */
    private int spToPx(int sp) {
        float scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (scaledDensity * sp + 0.5f * (sp >= 0 ? 1 : -1));
    }


    public void setKeepMove(boolean keepMove) {
        this.keepMove = keepMove;
    }

    public void keepXAmation() {
        if (keepMove) {
            xInit = (int) (xInit - interval);
            xLabelInit = (int) (xLabelInit - interval);
            invalidate();
        }
    }


    public void keepXLableAmation() {
        if (keepMove) {
            xLabelInit = (int) (xLabelInit - interval);
            invalidate();
        }
    }
}
