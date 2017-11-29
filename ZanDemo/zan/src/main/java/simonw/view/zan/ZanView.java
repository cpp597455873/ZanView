package simonw.view.zan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ZanView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private List<Drawable> mDrawbleList = new ArrayList<>();
    private Context context;


    /**
     * 心的个数
     */
    private ArrayList<ZanBean> zanBeen = new ArrayList<>();
    private Paint p;
    /**
     * 负责绘制的工作线程
     */
    private DrawThread drawThread;
    private Timer timer;
    private ScheduledExecutorService scheduledExecutorService;

    public ZanView(Context context) {
        this(context, null);
        init(context);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    public ZanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    private void init(Context context) {
        this.context = context;
        setDefaultDrawableList();
    }

    public ZanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //setZOrderMediaOverlay(true);
        this.setZOrderOnTop(true);
        /**设置画布  背景透明*/
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        p = new Paint();
        //抗锯齿
        p.setAntiAlias(true);
        drawThread = new DrawThread();
    }

    /**
     * 点赞动作  添加心的函数 控制画面最大心的个数
     */
    public void addZanXin() {
        ZanBean zanBean = new ZanBean(((BitmapDrawable) mDrawbleList.get(new Random().nextInt(mDrawbleList.size() - 1))).getBitmap(), getWidth(), getHeight());
        zanBeen.add(zanBean);
        if (zanBeen.size() > 20) {
            zanBeen.remove(0);
        }
        startDraw();
    }


    public void setDefaultDrawableList() {
        mDrawbleList.add(context.getResources().getDrawable(R.drawable.z0));
        mDrawbleList.add(context.getResources().getDrawable(R.drawable.z1));
        mDrawbleList.add(context.getResources().getDrawable(R.drawable.z2));
        mDrawbleList.add(context.getResources().getDrawable(R.drawable.z3));
        mDrawbleList.add(context.getResources().getDrawable(R.drawable.z4));
        mDrawbleList.add(context.getResources().getDrawable(R.drawable.z5));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (drawThread != null) {
            drawThread.interrupt();
            drawThread = null;
        }
        drawThread = new DrawThread();
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (drawThread != null) {
            drawThread.isRun = false;
            drawThread = null;
            new ClearThread().start();
        }
    }

    class ClearThread extends Thread {
        boolean isNeedClear = true;

        @Override
        public void run() {
            super.run();
            if (isNeedClear) {
                Canvas canvas = null;
                try {
                    synchronized (surfaceHolder) {
                        if (surfaceHolder != null) {
                            canvas = surfaceHolder.lockCanvas();
                            if (canvas != null) {
                                /**清除画面*/
                                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                isNeedClear = false;
            }
        }
    }

    class DrawThread extends Thread {
        boolean isRun = true;

        @Override
        public void run() {
            super.run();
            /**绘制的线程 死循环 不断的跑动*/
            while (isRun) {
                long l = System.currentTimeMillis();
                Canvas canvas = null;
                try {
                    synchronized (surfaceHolder) {
                        if (surfaceHolder != null) {
                            canvas = surfaceHolder.lockCanvas(null);
                            if (canvas != null) {
                                /**清除画面*/
                                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                                boolean isEnd = true;

                                /**对所有心进行遍历绘制*/
                                for (int i = 0; i < zanBeen.size(); i++) {
                                    isEnd = zanBeen.get(i).isEnd;
                                    zanBeen.get(i).draw(canvas, p);
                                }
                                /**这里做一个性能优化的动作，由于线程是死循环的 在没有心需要的绘制的时候会结束线程*/
                                if (isEnd) {
                                    isRun = false;
                                    drawThread = null;
                                }
                            }

                        }
                        Log.i("耗时：", "run: " + (System.currentTimeMillis() - l));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null && isAlive()) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
                    /**用于控制绘制帧率*/
                    long b = 16 - (System.currentTimeMillis() - l);
                    Thread.sleep(Math.max(0, b));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void stopAddHeart() {
        try {
            if (drawThread != null) {
                drawThread.isRun = false;
                drawThread.interrupt();
                for (int i = 0; i < zanBeen.size(); i++) {
                    zanBeen.get(i).stop();
                }
                drawThread = null;
                if (scheduledExecutorService != null) {
                    scheduledExecutorService.shutdownNow();
                    scheduledExecutorService = null;
                }
                zanBeen.clear();
                new ClearThread().start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startDraw() {
        if (drawThread == null) {
            drawThread = new DrawThread();
            drawThread.start();
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mZanHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            addZanXin();
        }
    };

    public void autoAddZan() {
        stopAddHeart();
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