package com.spr_ypt.sprprogressdrawable.drawable;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
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

import com.spr_ypt.sprprogressdrawable.drawableOld.PkProgressDrawableV3;

public class SprProgressDrawable extends Drawable {
    private static final String TAG = PkProgressDrawableV3.class.getSimpleName();
    BaseStyleParams mParams = new BaseStyleParams();

    private Paint leftPaint;
    private Paint rightPaint;
    private float mlastRate;//上次动画最终位置
    private float rate;//需要到达的位置
    private float progress;//绘制进度
    private float mLastProgress=-1f;//用于记录上次的progress
    private ValueAnimator mAnimForRate;//对比度变化动画触发器
    private ValueAnimator mAnimForColor;//画笔动画触发器
    private boolean isAnimCancel;
    private float colorRate;
    private float mLastColorRate=-1f;//用于记录上次的colorRate

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

    public SprProgressDrawable(@FloatRange(from = 0.0f, to = 1.0f) float rate) {
        this.rate = rate;
        this.progress = rate;
        this.mlastRate = rate;
        init();
    }

    public SprProgressDrawable() {
        init();
    }

    public SprProgressDrawable(BaseStyleParams params) {
        if (null != params) {
            this.mParams = params;
        }
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
        mAnimForRate = ObjectAnimator.ofFloat(0, 1f);
        mAnimForRate.setDuration(mParams.getDurationForRate());
        mAnimForRate.setInterpolator(new OvershootInterpolator());
        mAnimForRate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue() * (rate - mlastRate) + mlastRate;
                Log.d(TAG, "onAnimationUpdate: progress=" + progress);
                if (!mParams.isWipe()) invalidateSelf();

            }
        });
        mAnimForRate.addListener(new AnimatorListenerAdapter() {
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

        if (mParams.isWipe()) {
            mAnimForColor = ObjectAnimator.ofFloat(0, 1f);
            mAnimForColor.setDuration(mParams.getDurationForColor());
            mAnimForColor.setRepeatCount(ValueAnimator.INFINITE);
            mAnimForColor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    colorRate = (float) animation.getAnimatedValue();
                    invalidateSelf();
                }
            });
            mAnimForColor.start();
        }
    }



    private void initPaints(Canvas canvas) {
        if (null == leftPaint) leftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (null == rightPaint) rightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (mLastColorRate != colorRate) {//如果colorRate没有改变则不需要重置画笔的shader
            LinearGradient leftLg = new LinearGradient(0 + canvas.getWidth() * colorRate, canvas.getHeight() / 2,
                    canvas.getWidth() / 2 * (1 + 2 * colorRate), canvas.getHeight() / 2,
                    mParams.getLeftGradientColors()[0], mParams.getLeftGradientColors()[1], Shader.TileMode.MIRROR);
            LinearGradient rightLg = new LinearGradient(canvas.getWidth() + canvas.getWidth() * colorRate, canvas.getHeight() / 2,
                    canvas.getWidth() / 2 * (1 + 2 * colorRate), canvas.getHeight() / 2,
                    mParams.getRightGradienColors()[0], mParams.getRightGradienColors()[1], Shader.TileMode.MIRROR);
            leftPaint.setShader(leftLg);
            rightPaint.setShader(rightLg);
            mLastColorRate = colorRate;
        }
    }

    /**
     * 设置进度比例
     *
     * @param rate
     */
    public void setRate(@FloatRange(from = 0.0f, to = 1.0f) float rate) {
        this.rate = rate;
        if (mAnimForRate.isRunning()) {
            mAnimForRate.cancel();
        }
        mAnimForRate.start();

    }

    public void recycle() {
        if (null != mAnimForRate) {
            mAnimForRate.cancel();
            mAnimForRate.removeAllUpdateListeners();
        }
        if (null != mAnimForColor) {
            mAnimForColor.cancel();
            mAnimForColor.removeAllUpdateListeners();
        }

    }



    @Override
    public void draw(@NonNull Canvas canvas) {

        Log.e(TAG, "draw: ");

        initPaints(canvas);

        if (mLastProgress != progress) {//左右的path只有在progress变化时才变更

            float cot = mParams.getMidLineCot();

            //计算2个4边型的基本宽高
            leftQHight = mParams.getLeftHeightInterpolate(progress) * canvas.getHeight();
            leftQWidth = canvas.getWidth() * progress;

            rightQWidth = canvas.getWidth() * (1 - progress);
            rightQHight = mParams.getRightHeightInterpolate(progress) * canvas.getHeight();

            //计算4边型的4点坐标
            leftQ1 = new float[]{leftQHight / 2, canvas.getHeight() - leftQHight};//left-top
            leftQ2 = new float[]{leftQWidth - (leftQHight - canvas.getHeight() / 2) * cot, canvas.getHeight() - leftQHight};//right-top
            leftQ3 = new float[]{leftQWidth + canvas.getHeight() / 2 * cot, canvas.getHeight()};//right-bottom
            leftQ4 = new float[]{leftQHight / 2, canvas.getHeight()};//left-bottom
            arcLeftRectF = new RectF(0, canvas.getHeight() - leftQHight, leftQHight, canvas.getHeight());

            rightQ1 = new float[]{leftQWidth - (rightQHight - canvas.getHeight() / 2) * cot, canvas.getHeight() - rightQHight};//left-top
            rightQ2 = new float[]{canvas.getWidth() - rightQHight / 2, canvas.getHeight() - rightQHight};//right-top
            rightQ3 = new float[]{canvas.getWidth() - rightQHight / 2, leftQHight};//right-bottom
            rightQ4 = leftQ3;//left-bottom
            arcRightRectF = new RectF(canvas.getWidth() - rightQHight, canvas.getHeight() - rightQHight, canvas.getWidth(), canvas.getHeight());


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

            mLastProgress = progress;
        }

        canvas.drawPath(leftQPath, leftPaint);
        canvas.drawPath(rightQPath, rightPaint);
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

}
