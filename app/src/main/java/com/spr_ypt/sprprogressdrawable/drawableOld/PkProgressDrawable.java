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
 * Created by Spr_ypt on 2017/9/29.
 * 属性动画实现进度条增减
 */

public class PkProgressDrawable extends Drawable {
    private static final String TAG = PkProgressDrawable.class.getSimpleName();
    private Paint leftPaint ;
    private Paint rightPaint ;
    private float mlastRate;//上次动画最终位置
    private float rate;//需要到达的位置
    private float progress;//绘制进度
    private ValueAnimator mAnim;//动画触发器
    private boolean isAnimCancel;
    private int mDuration = 500;
    private Path path;

    public PkProgressDrawable(@FloatRange(from = 0.0f, to = 1.0f) float rate) {
        this.rate = rate;
        this.progress = rate;
        this.mlastRate = rate;
        init();
    }

    /**
     * 初始化画笔监听等
     */
    private void init() {
        initPath();
        initAnim();
    }

    private void initAnim() {
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

    private void initPath() {
        path=new Path();
    }

    private void initPaints(Canvas canvas){
        leftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient leftLg = new LinearGradient(0 , canvas.getHeight() / 2,
                canvas.getWidth() / 2 , canvas.getHeight() / 2,
                Color.parseColor("#ff2d55"), Color.parseColor("#ff7c3c"), Shader.TileMode.MIRROR);
        LinearGradient rightLg = new LinearGradient(canvas.getWidth() , canvas.getHeight() / 2,
                canvas.getWidth() / 2 , canvas.getHeight() / 2,
                Color.parseColor("#408aed"), Color.parseColor("#00c4ff"), Shader.TileMode.MIRROR);
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

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (null==leftPaint||null==rightPaint){
            initPaints(canvas);
        }
        int index = (int) (canvas.getWidth() * progress);
        float radi=canvas.getHeight()/2;
        float[] leftRadii={radi,radi,0f,0f,0f,0f,radi,radi};
        float[] rightRadii={0f,0f,radi,radi,radi,radi,0f,0f};
        RectF leftRectF = new RectF(0, 0, index, canvas.getHeight());
        RectF rightRectF = new RectF(index, 0, canvas.getWidth(), canvas.getHeight());
        path.reset();
        path.addRoundRect(leftRectF,leftRadii, Path.Direction.CW);
        canvas.drawPath(path, leftPaint);
        path.reset();
        path.addRoundRect(rightRectF,rightRadii,Path.Direction.CW);
        canvas.drawPath(path, rightPaint);
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

    public void recycle() {
        if (null!=mAnim&&mAnim.isRunning()){
            mAnim.cancel();
        }
    }


}
