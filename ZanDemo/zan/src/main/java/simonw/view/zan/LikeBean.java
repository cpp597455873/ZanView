package simonw.view.zan;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.animation.DecelerateInterpolator;

import java.util.Random;


public class LikeBean {

    /**
     * 心的当前坐标
     */
    public Point point;
    /**
     * 移动动画
     */
    private ValueAnimator moveAnim;
    /**
     * 放大动画
     */
    private ValueAnimator zoomAnim;
    /**
     * 透明度
     */
    public int alpha = 255;
    /**
     * 心图
     */
    private Bitmap bitmap;
    /**
     * 绘制bitmap的矩阵  用来做缩放和移动的
     */
    private Matrix matrix = new Matrix();
    /**
     * 缩放系数
     */
    private float scale = 0;
    /**
     * 产生随机数
     */
    private Random random = new Random();
    /**
     * 是否结束
     */
    public boolean isEnd = false;

    private int mHeight;
    private int mWidth;
    private int mBitmapWidth;


    public LikeBean(Bitmap bitmap, int width, int height) {
        random = new Random();
        this.bitmap = bitmap;
        mBitmapWidth = bitmap.getWidth();
        mHeight = height;
        mWidth = width;
        init();
    }

    private void init() {
        int boundX = mWidth - mBitmapWidth;
        int boundY = mHeight / 4;
        //这里边界可能小于0,在random的时候可能会崩溃掉
        if (boundX <= 0 || boundY <= 0) {
            return;
        }
        int pointX1 = random.nextInt(boundX);
        int pointX2 = random.nextInt(boundX);
        int pointY1 = random.nextInt(boundY) + boundY;
        int pointY2 = random.nextInt(boundY) + mHeight / 2;

        final Point controlPoint1 = new Point(pointX1, pointY1);
        final Point controlPoint2 = new Point(pointX2, pointY2);
        LikeBezierEvaluator evaluator = new LikeBezierEvaluator(controlPoint1, controlPoint2);
        final Point point1 = new Point(mWidth / 2, mHeight - mBitmapWidth);
        final Point point2 = new Point(random.nextInt(boundX), 0);

        moveAnim = ValueAnimator.ofObject(evaluator, point1, point2);
        moveAnim.setDuration(2000);
        moveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LikeBean.this.point = (Point) animation.getAnimatedValue();
                alpha = (int) ((float) LikeBean.this.point.y / (float) point1.y * 255);
            }
        });
        moveAnim.start();
        zoomAnim = ValueAnimator.ofFloat(0, 1f).setDuration(700);
        zoomAnim.setInterpolator(new DecelerateInterpolator());
        zoomAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scale = (Float) animation.getAnimatedValue();
            }
        });
        zoomAnim.start();
    }


    public void stop() {
        if (moveAnim != null) {
            moveAnim.cancel();
            moveAnim = null;
        }
        if (zoomAnim != null) {
            zoomAnim.cancel();
            zoomAnim = null;
        }
    }

    /**
     * 主要绘制函数
     */
    public void draw(Canvas canvas, Paint p) {
        if (bitmap != null && alpha > 0) {
            p.setAlpha(alpha);
            matrix.setScale(scale, scale, mBitmapWidth / 2, mBitmapWidth / 2);
            matrix.postTranslate(point.x, point.y);
            canvas.drawBitmap(bitmap, matrix, p);
        } else {
            isEnd = true;
        }
    }
}