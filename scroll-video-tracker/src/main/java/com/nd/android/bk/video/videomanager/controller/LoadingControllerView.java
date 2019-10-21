package com.nd.android.bk.video.videomanager.controller;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nd.android.bk.video.R;

/**
 * @author JiaoYun
 * @date 2019/10/14 22:30
 */
public class LoadingControllerView extends BaseControllerView {
    public LoadingControllerView(Context context) {
        super(context);
    }

    public LoadingControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private ImageView loading;
    private AnimationDrawable drawable;

    @Override
    protected void initView() {

        loading = new ImageView(getContext());
        loading.setBackgroundResource(R.drawable.common_video_loading);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT);
        addView(loading, params);
        drawable = (AnimationDrawable) loading.getBackground();
    }

    @Override
    protected void attachWindow(boolean attach) {
        if(attach){
            drawable.start();
        }else {
            drawable.stop();
        }
    }
}
