package com.gnepux.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.gnepux.demo.slideview.R;
import com.gnepux.slideview.SlideView;

public class MainActivity extends AppCompatActivity {

    private SlideView mSlideView;

    private CheckBox mResetNotFullCb;

    private CheckBox mEnableWhenFullCb;

    private Button mResetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlideView = (SlideView) findViewById(R.id.slideview);
        mSlideView.addSlideListener(new SlideView.OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                Toast.makeText(MainActivity.this, "确认成功", Toast.LENGTH_SHORT).show();
            }
        });

        mResetNotFullCb = (CheckBox) findViewById(R.id.cb_reset_when_not_full);
        mResetNotFullCb.setChecked(mSlideView.isResetWhenNotFull());
        mResetNotFullCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSlideView.resetWhenNotFull(isChecked);
            }
        });

        mEnableWhenFullCb = (CheckBox) findViewById(R.id.cb_enable_when_full);
        mEnableWhenFullCb.setChecked(mSlideView.isEnableWhenFull());
        mEnableWhenFullCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSlideView.enableWhenFull(isChecked);
            }
        });

        mResetBtn = (Button) findViewById(R.id.btn_reset);
        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideView.reset();
            }
        });
    }
}
