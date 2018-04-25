package com.spr_ypt.sprprogressdrawable.drawable.paramImp;

import com.spr_ypt.sprprogressdrawable.drawable.BaseStyleParams;

public class DemoParams extends BaseStyleParams {

    @Override
    public float getLeftHeightInterpolate(float progress) {
        return 0.5f + 0.5f * progress;
    }

    @Override
    public float getRightHeightInterpolate(float progress) {
        return 0.5f + 0.5f * (1 - progress);
    }

    @Override
    public boolean isWipe() {
        return false;
    }
}
