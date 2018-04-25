package com.spr_ypt.sprprogressdrawable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.spr_ypt.sprprogressdrawable.drawable.SprProgressDrawable;
import com.spr_ypt.sprprogressdrawable.drawable.paramImp.DemoParams;

public class DemoActivty extends AppCompatActivity {

    private ImageView mIvTest1;
    private Button mBtnTest;
    private SprProgressDrawable mDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        initView();
        initDrawable();
        initEvent();
    }

    private void initView() {
        mIvTest1 = (ImageView) findViewById(R.id.iv_test1);
        mBtnTest = (Button) findViewById(R.id.btn_test);
    }

    private void initDrawable() {
        mDrawable = new SprProgressDrawable(new DemoParams());
        mIvTest1.setImageDrawable(mDrawable);
    }

    private void initEvent() {
        mBtnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double random = Math.random();
                mDrawable.setRate((float) random);
            }
        });
    }
}
