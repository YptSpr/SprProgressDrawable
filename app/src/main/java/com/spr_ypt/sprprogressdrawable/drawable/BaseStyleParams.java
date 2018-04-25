package com.spr_ypt.sprprogressdrawable.drawable;


import android.graphics.Color;

/**
 * 用于自定义可控的样式
 */
public class BaseStyleParams {


    /**
     * 进度条动画执行时间
     *
     * @return
     */
    public int getDurationForRate() {
        return 500;
    }

    /**
     * 扫光效果动画执行周期时间
     *
     * @return
     */
    public int getDurationForColor() {
        return 5000;
    }

    /**
     * 中间斜线斜度（宽高比
     * 正数\，负数/
     * 数值绝对值越大越斜
     *
     * @return 角度的cot值
     */
    public float getMidLineCot() {
        return 0.5f;
    }

    /**
     * 左侧扫过渐变色，目前只支持两种颜色
     *
     * @return
     */
    public int[] getLeftGradientColors() {
        return new int[]{Color.parseColor("#ff2d55"), Color.parseColor("#ff7c3c")};
    }

    /**
     * 右侧扫过渐变色，目前只支持两种颜色
     *
     * @return
     */
    public int[] getRightGradienColors() {
        return new int[]{Color.parseColor("#408aed"), Color.parseColor("#00c4ff")};
    }

    /**
     * 获取左边比分条高度比例（实际高度=高度比例*画布高度）
     * @param progress
     * @return 返回一个高度比例与进度之间的函数关系
     */
    public float getLeftHeightInterpolate(float progress) {
        return progress > 0.5 ? 1f : (0.5f + progress);
    }

    /**
     * 获取右边比分条高度比例
     * @param progress
     * @return 返回一个高度比例与进度之间的函数关系
     */
    public float getRightHeightInterpolate(float progress) {
        return progress > 0.5 ? (1.5f - progress) : 1f;
    }

    public boolean isWipe(){
        return true;
    }


}
