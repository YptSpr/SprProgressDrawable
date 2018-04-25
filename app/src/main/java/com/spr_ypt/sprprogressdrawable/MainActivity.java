package com.spr_ypt.sprprogressdrawable;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.spr_ypt.sprprogressdrawable.drawableOld.PkProgressDrawable;
import com.spr_ypt.sprprogressdrawable.drawableOld.PkProgressDrawableV2;
import com.spr_ypt.sprprogressdrawable.drawableOld.PkProgressDrawableV3;

public class MainActivity extends AppCompatActivity {

    private ImageView mIvVersion1;
    private ImageView mIvVersion2;
    private ImageView mIvVersion3;

    private PkProgressDrawable drawable1;
    private PkProgressDrawableV2 drawable2;
    private PkProgressDrawableV3 drawable3;

    private Button mBtnRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initDrawable();
        initEvent();
    }

    private void initView() {
        mIvVersion1 = (ImageView) findViewById(R.id.iv_version1);
        mIvVersion2 = (ImageView) findViewById(R.id.iv_version2);
        mIvVersion3 = (ImageView) findViewById(R.id.iv_version3);
        mBtnRandom = (Button) findViewById(R.id.btn_random);
    }

    private void initDrawable() {
        drawable1 = new PkProgressDrawable(0.5f);
        mIvVersion1.setImageDrawable(drawable1);
        drawable2 = new PkProgressDrawableV2(0.5f);
        mIvVersion2.setImageDrawable(drawable2);
        drawable3 = new PkProgressDrawableV3(0.5f);
        mIvVersion3.setImageDrawable(drawable3);
    }

    private void initEvent() {
        mBtnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float f = (float) Math.random();
                drawable1.setRate(f);
                drawable2.setRate(f);
                drawable3.setRate(f);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //finalize 回收的话会导致finalizerReference对象累积，导致内存泄漏
        drawable1.recycle();
        drawable2.recycle();
        drawable3.recycle();
    }
}
