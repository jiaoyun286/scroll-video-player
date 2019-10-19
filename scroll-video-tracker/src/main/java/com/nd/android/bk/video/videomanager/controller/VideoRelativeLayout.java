package com.nd.android.bk.video.videomanager.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.nd.android.bk.video.R;
import com.nd.android.bk.video.tracker.IViewTracker;
import com.nd.android.bk.video.tracker.Tracker;
import com.nd.android.bk.video.utils.Logger;
import com.nd.android.bk.video.videomanager.interfaces.PlayerItemChangeListener;
import com.nd.android.bk.video.videomanager.interfaces.SimpleVideoPlayerListener;
import com.nd.android.bk.video.videomanager.interfaces.VideoPlayerListener;


/**
 * @author JiaoYun
 * @date 2019/10/15 11:08
 */
public class VideoRelativeLayout extends RelativeLayout implements PlayerItemChangeListener {
    private final String TAG = "VideoRelativeLayout";
    private View mPlayView;
    private View mTrackerView;

    public VideoRelativeLayout(Context context) {
        super(context);
    }

    public VideoRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPlayView = findViewById(R.id.view_play_video);
        mTrackerView = findViewById(R.id.view_tracker);
    }

    /**
     *
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Logger.d(TAG,"onAttachedToWindow");
        Tracker.addVideoPlayerListener(mVideoPlayerListener);
        Tracker.addPlayerItemChangeListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Logger.d(TAG,"onDetachedFromWindow");
        Tracker.removeVideoPlayerListener(mVideoPlayerListener);
        Tracker.removePlayerItemChangeListener(this);
    }

    /**
     * 用于监听播放器状态，改变占位图上的播放按钮的状态
     */
    private VideoPlayerListener mVideoPlayerListener = new SimpleVideoPlayerListener(){

        @Override
        public void onVideoStarted(IViewTracker viewTracker) {
            if(mPlayView != null && viewTracker.getTrackerView().equals(mTrackerView)){
                mPlayView.setVisibility(GONE);
            }
        }

        @Override
        public void onVideoStopped(IViewTracker viewTracker) {
            if(mPlayView != null){
                mPlayView.setVisibility(VISIBLE);
            }
        }

        @Override
        public void onVideoCompletion(IViewTracker viewTracker) {
            if(mPlayView != null){
                mPlayView.setVisibility(VISIBLE);
            }
        }

        @Override
        public void onVideoPaused(IViewTracker viewTracker) {
            if(mPlayView != null){
                mPlayView.setVisibility(VISIBLE);
            }
        }

        @Override
        public void onError(IViewTracker viewTracker, int what, int extra) {
            if(mPlayView != null){
                mPlayView.setVisibility(VISIBLE);
            }
        }
    };

    @Override
    public void onPlayerItemChanged(IViewTracker viewTracker) {
        if(mPlayView != null){
            if(viewTracker.getTrackerView().equals(mTrackerView)) {
                mPlayView.setVisibility(GONE);
            }else {
                mPlayView.setVisibility(VISIBLE);
            }
        }
    }
}
