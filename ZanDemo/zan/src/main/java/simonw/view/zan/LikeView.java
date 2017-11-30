package simonw.view.zan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author chenpiaopiao
 * @date 2017/11/29 16:00
 */


public class LikeView extends View {
    /**
     * 每秒发射几颗爱心
     */
    private int countPerSecond = 5;
    /**
     * 新增点赞
     */
    private final int ADD_LIKE = 1;
    /**
     * 刷新View
     */
    private final int INVALIDATE_CANVAS = 2;
    /**
     * 屏幕最大点赞数量
     */
    private int MAX_LIKE = 20;

    private ArrayList<ZanBean> zanBeen = new ArrayList<>();
    private List<Drawable> mDrawbleList = new ArrayList<>();
    private Paint p;

    private boolean autoStart = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ADD_LIKE) {
                addZanXin();
            } else if (msg.what == INVALIDATE_CANVAS) {
                invalidate();
            }
        }
    };
    /**
     * 绘制开始时间
     */
    private long drawStartTime;
    /**
     * 下次是否继续刷新
     */
    private boolean invalidateNextTime;
    /**
     * 点赞循环增加
     */
    private Runnable zanRunnable = new Runnable() {
        @Override
        public void run() {
            addZanXin();
            addZanTick();
        }
    };


    public LikeView(Context context) {
        this(context, null);
    }

    public LikeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDrawbleList.add(context.getResources().getDrawable(R.mipmap.heart0));
        mDrawbleList.add(context.getResources().getDrawable(R.mipmap.heart1));
        mDrawbleList.add(context.getResources().getDrawable(R.mipmap.heart2));
        mDrawbleList.add(context.getResources().getDrawable(R.mipmap.heart3));
        mDrawbleList.add(context.getResources().getDrawable(R.mipmap.heart4));
        mDrawbleList.add(context.getResources().getDrawable(R.mipmap.heart5));
        init();
    }

    private void init() {
        p = new Paint();
        p.setAntiAlias(true);
    }


    /**
     * 点赞动作  添加心的函数 控制画面最大心的个数
     */
    public void addZanXin() {
        int width = getWidth();
        int height = getHeight();
        ZanBean zanBean = new ZanBean(((BitmapDrawable) mDrawbleList.get(new Random().nextInt(mDrawbleList.size() - 1))).getBitmap(), width, height);
        zanBeen.add(zanBean);
        if (zanBeen.size() > MAX_LIKE) {
            zanBeen.remove(0);
        }
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAddHeart();
        handler.removeCallbacks(null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (autoStart) {
            autoAddZan();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //用于记录下次是否刷新
        drawStartTime = System.currentTimeMillis();
        invalidateNextTime = false;
        if (canvas != null) {
            //刷新背景
            canvas.drawColor(Color.TRANSPARENT);
            for (int i = 0; i < zanBeen.size(); i++) {
                ZanBean zanBean = zanBeen.get(i);
                zanBean.draw(canvas, p);
                if (!zanBean.isEnd) {
                    invalidateNextTime = true;
                }
            }
            super.onDraw(canvas);
        }
        if (invalidateNextTime) {
            //保证60fps的刷新
            handler.sendEmptyMessageDelayed(INVALIDATE_CANVAS, Math.max(0, 16 - (System.currentTimeMillis() - drawStartTime)));
        }

    }

    /**
     * 停止自动增加点赞
     */
    public void stopAddHeart() {
        for (int i = 0; i < zanBeen.size(); i++) {
            zanBeen.get(i).stop();
        }
        zanBeen.clear();
        handler.removeCallbacks(zanRunnable);
    }

    /**
     * 定时点赞
     */
    public void addZanTick() {
        handler.postDelayed(zanRunnable, 1000 / countPerSecond);
    }

    /**
     * 自动增加点赞
     */
    public void autoAddZan() {
        stopAddHeart();
        autoStart = true;
        addZanTick();
    }
}
