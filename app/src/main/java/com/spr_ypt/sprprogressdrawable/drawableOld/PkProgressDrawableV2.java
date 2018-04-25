package com.spr_ypt.sprprogressdrawable.drawableOld;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.OvershootInterpolator;

/**
 * Created by Spr_ypt on 2017/10/1.
 * 属性动画实现进度条增减+异性进度条绘制
 */

public class PkProgressDrawableV2 extends Drawable {

    private static final String TAG = PkProgressDrawable.class.getSimpleName();
    private Paint leftPaint;
    private Paint rightPaint;
    private float mlastRate;//上次动画最终位置
    private float rate;//需要到达的位置
    private float progress;//绘制进度
    private ValueAnimator mAnim;//动画触发器
    private boolean isAnimCancel;
    private int mDuration = 500;

    //以下为绘制相关变量
    private float cot = 0.5f;//中间斜线斜度（宽高比），可以尝试负数未验证

    private Path leftQPath;
    private Path rightQPath;

    float leftQHight;//左边图形高
    float leftQWidth;//左边图形宽
    float rightQWidth;//右边图形宽
    float rightQHight;//右边图形高

    float[] leftQ1;//左边图形左上坐标
    float[] leftQ2;//左边图形右上坐标
    float[] leftQ3;//左边图形右下坐标
    float[] leftQ4;//左边图形左下坐标

    RectF arcLeftRectF;//左边图形绘制圆角基准

    float[] rightQ1;//右边图形左上坐标
    float[] rightQ2;//右边图形右上坐标
    float[] rightQ3;//右边图形右下坐标
    float[] rightQ4;//右边图形左下坐标

    RectF arcRightRectF;//右边图形绘制圆角基准
    //以上为绘制相关变量

    public PkProgressDrawableV2(@FloatRange(from = 0.0f, to = 1.0f) float rate) {
        this.rate = rate;
        this.progress = rate;
        this.mlastRate = rate;
        init();
    }

    public PkProgressDrawableV2(Paint leftPaint, Paint rightPaint) {
        this.leftPaint = leftPaint;
        this.rightPaint = rightPaint;
        init();
    }

    public PkProgressDrawableV2(float rate, Paint leftPaint, Paint rightPaint) {
        this.rate = rate;
        this.progress = rate;
        this.mlastRate = rate;
        this.leftPaint = leftPaint;
        this.rightPaint = rightPaint;
        init();
    }

    /**
     * 初始化画笔监听等
     */
    private void init() {
        initPath();
        initAnims();
    }

    private void initPath() {
        leftQPath = new Path();
        rightQPath = new Path();
    }

    private void initAnims() {
        mAnim = ObjectAnimator.ofFloat(0, 1f);
        mAnim.setDuration(mDuration);
        mAnim.setInterpolator(new OvershootInterpolator());
        mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue() * (rate - mlastRate) + mlastRate;
                Log.d(TAG, "onAnimationUpdate: progress=" + progress);
                invalidateSelf();
            }
        });
        mAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                //动画中途停止记录进度
                isAnimCancel = true;
                mlastRate = progress;
                Log.d(TAG, "onAnimationCancel: mlastRate=" + mlastRate);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //正常停止动画记录进度，如果中断过则只记录中断的进度
                if (!isAnimCancel) {
                    mlastRate = rate;
                }
                isAnimCancel = false;
                Log.d(TAG, "onAnimationEnd: mlastRate=" + mlastRate);
            }


        });
    }

    private void initPaints(Canvas canvas) {
        leftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient leftLg = new LinearGradient(0, canvas.getHeight() / 2, canvas.getWidth() / 2, canvas.getHeight() / 2, Color.parseColor("#ff2d55"), Color.parseColor("#ff7c3c"), Shader.TileMode.MIRROR);
        LinearGradient rightLg = new LinearGradient(canvas.getWidth(), canvas.getHeight() / 2, canvas.getWidth() / 2, canvas.getHeight() / 2, Color.parseColor("#408aed"), Color.parseColor("#00c4ff"), Shader.TileMode.MIRROR);
        leftPaint.setShader(leftLg);
        rightPaint.setShader(rightLg);
    }

    /**
     * 设置进度比例
     *
     * @param rate
     */
    public void setRate(@FloatRange(from = 0.0f, to = 1.0f) float rate) {
        this.rate = rate;
        if (mAnim.isRunning()) {
            mAnim.cancel();
        }
        mAnim.start();

    }

    public void recycle() {
        mAnim.cancel();
        mAnim.removeAllUpdateListeners();

    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        if (null == leftPaint || null == rightPaint) {
            initPaints(canvas);
        }

        if (progress > 0.5f) {

            //计算2个4边型的基本宽高
            leftQHight = canvas.getHeight();
            leftQWidth = canvas.getWidth() * progress;

            rightQWidth = canvas.getWidth() * (1 - progress);
            rightQHight = canvas.getHeight() * (1.5f - progress);

            //计算4边型的4点坐标
            leftQ1 = new float[]{leftQHight / 2, 0f};//left-top
            leftQ2 = new float[]{leftQWidth - leftQHight / 2 * cot, 0f};//right-top
            leftQ3 = new float[]{leftQWidth + leftQHight / 2 * cot, leftQHight};//right-bottom
            leftQ4 = new float[]{leftQHight / 2, leftQHight};//left-bottom
            arcLeftRectF = new RectF(0, 0, leftQHight, leftQHight);

            rightQ1 = new float[]{leftQWidth - (rightQHight - canvas.getHeight() / 2) * cot, canvas.getHeight() - rightQHight};//left-top
            rightQ2 = new float[]{canvas.getWidth() - rightQHight / 2, canvas.getHeight() - rightQHight};//right-top
            rightQ3 = new float[]{canvas.getWidth() - rightQHight / 2, leftQHight};//right-bottom
            rightQ4 = leftQ3;//left-bottom
            arcRightRectF = new RectF(canvas.getWidth() - rightQHight, canvas.getHeight() - rightQHight, canvas.getWidth(), canvas.getHeight());

        } else {
            //计算2个四边形长宽
            leftQHight = canvas.getHeight() * (0.5f + progress);
            leftQWidth = canvas.getWidth() * progress;

            rightQWidth = canvas.getWidth() * (1 - progress);
            rightQHight = canvas.getHeight();

            //计算2个四边形4点坐标
            leftQ1 = new float[]{leftQHight / 2, canvas.getHeight() - leftQHight};
            leftQ2 = new float[]{leftQWidth - (leftQHight - canvas.getHeight() / 2) * cot, canvas.getHeight() - leftQHight};
            leftQ3 = new float[]{leftQWidth + canvas.getHeight() / 2 * cot, canvas.getHeight()};
            leftQ4 = new float[]{leftQHight / 2, canvas.getHeight()};
            arcLeftRectF = new RectF(0, canvas.getHeight() - leftQHight, leftQHight, canvas.getHeight());

            rightQ1 = new float[]{leftQWidth - rightQHight / 2 * cot, 0f};
            rightQ2 = new float[]{canvas.getWidth() - rightQHight / 2, 0f};
            rightQ3 = new float[]{rightQ2[0], rightQHight};
            rightQ4 = new float[]{leftQWidth + rightQHight / 2 * cot, rightQHight};
            arcRightRectF = new RectF(canvas.getWidth() - rightQHight, 0f, canvas.getWidth(), rightQHight);

        }

        //连接&绘制path
        leftQPath.reset();
        leftQPath.moveTo(leftQ1[0], leftQ1[1]);
        leftQPath.lineTo(leftQ2[0], leftQ2[1]);
        leftQPath.lineTo(leftQ3[0], leftQ3[1]);
        leftQPath.lineTo(leftQ4[0], leftQ4[1]);
        leftQPath.arcTo(arcLeftRectF, 90, 180);
        leftQPath.close();

        rightQPath.reset();
        rightQPath.moveTo(rightQ1[0], rightQ1[1]);
        rightQPath.lineTo(rightQ2[0], rightQ2[1]);
        rightQPath.arcTo(arcRightRectF, 270, 180);
        rightQPath.lineTo(rightQ4[0], rightQ4[1]);
        rightQPath.lineTo(rightQ1[0], rightQ1[1]);
        rightQPath.close();

        canvas.drawPath(leftQPath, leftPaint);
        canvas.drawPath(rightQPath, rightPaint);


//        int index = (int) (canvas.getWidth() * progress);
//        RectF leftRectF = new RectF(0, 0, index, canvas.getHeight());
//        RectF rightRectF = new RectF(index, 0, canvas.getWidth(), canvas.getHeight());
//        canvas.drawRect(leftRectF, leftPaint);
//        canvas.save();
//        canvas.drawRect(rightRectF, rightPaint);
//        canvas.restore();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        this.leftPaint.setAlpha(alpha);
        this.rightPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        this.leftPaint.setColorFilter(colorFilter);
        this.rightPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
        mAnim.setDuration(mDuration);
    }
}
