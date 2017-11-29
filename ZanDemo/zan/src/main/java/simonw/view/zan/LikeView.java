package simonw.view.zan;

import android.annotation.SuppressLint;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author chenpiaopiao
 * @date 2017/11/29 16:00
 */


public class LikeView extends View {

    private ArrayList<ZanBean> zanBeen = new ArrayList<>();
    private List<Drawable> mDrawbleList = new ArrayList<>();
    private Paint p;

    private boolean autoStart = false;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private ScheduledExecutorService scheduledExecutorService;


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
        if (zanBeen.size() > 20) {
            zanBeen.remove(0);
        }
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAddHeart();
        mZanHandler = null;
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
        boolean invalidateNextTime = false;
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
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            }, 10);
        }

    }

    private Handler mZanHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            addZanXin();
        }
    };

    public synchronized void stopAddHeart() {
        try {
            for (int i = 0; i < zanBeen.size(); i++) {
                zanBeen.get(i).stop();
            }
            zanBeen.clear();
            if (scheduledExecutorService != null) {
                scheduledExecutorService.shutdownNow();
                scheduledExecutorService = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void autoAddZan() {
        stopAddHeart();
        autoStart = true;
        scheduledExecutorService = Executors.newScheduledThreadPool(20);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (mZanHandler != null) {
                    Message msg = mZanHandler.obtainMessage();
                    msg.sendToTarget();
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }
}
