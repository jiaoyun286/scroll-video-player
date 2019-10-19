package com.nd.android.bk.video.tracker;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nd.android.bk.video.R;
import com.nd.android.bk.video.videomanager.player.VideoPlayerView;

/**
 * 浮在列表视图上层的容器视图，视频播放时显示，列表视图滚动时，会跟随列表滚动，完全遮盖住视频播放前，列表中视频封面显示所在的位置
 * @author JiaoYun
 * @date 2019/10/14 20:38
 */
public class FloatLayerView extends FrameLayout {

    /**
     * 底部容器视图，trackView在视频播放时会添加到里面
     */
    private FrameLayout mVideoBottomView;
    /**
     * 视频播放视图，视频会在这个视图里面播放
     */
    private VideoPlayerView mVideoPlayerView;
    /**
     * 顶层容器视图，controllerbar和状态视图会add到里面
     */
    private FrameLayout mVideoTopView;

    public FloatLayerView(@NonNull Context context) {
        super(context);
        init();
    }

    public FloatLayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatLayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mVideoBottomView = new FrameLayout(getContext());
        mVideoTopView = new FrameLayout(getContext());
        mVideoPlayerView = new VideoPlayerView(getContext());

        FrameLayout videoRoot = new FrameLayout(getContext());
        videoRoot.addView(mVideoBottomView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        videoRoot.addView(mVideoPlayerView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        videoRoot.addView(mVideoTopView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        FrameLayout rootLayout = new FrameLayout(getContext());
        rootLayout.addView(videoRoot,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(rootLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * 获取播放器视图的父视图
     * @return
     */
    public View getVideoRootView() {
        return (View) mVideoPlayerView.getParent();
    }

    /**
     * 获取底层容器视图
     * @return
     */
    public FrameLayout getVideoBottomView(){
        return mVideoBottomView;
    }

    /**
     * 获取顶层容器视图
     * @return
     */
    public FrameLayout getVideoTopView(){
        return mVideoTopView;
    }

    /**
     * 获取播放器视图
     * @return
     */
    public VideoPlayerView getVideoPlayerView(){
        return mVideoPlayerView;
    }
}
