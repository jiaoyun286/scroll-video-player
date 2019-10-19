package com.nd.android.bk.video.videomanager.interfaces;

import com.nd.android.bk.video.tracker.IViewTracker;

/**
 * 播放器监听的默认实现类，方法都是空实现，需要监听播放器状态的类从这个类派生就只需要复写自己关系的方法
 * @author JiaoYun
 * @date 2019/10/14 21:42
 */
public class SimpleVideoPlayerListener implements VideoPlayerListener {
    @Override
    public void onVideoSizeChanged(IViewTracker viewTracker, int width, int height) {

    }

    @Override
    public void onVideoPrepared(IViewTracker viewTracker) {

    }

    @Override
    public void onVideoCompletion(IViewTracker viewTracker) {

    }

    @Override
    public void onError(IViewTracker viewTracker, int what, int extra) {

    }

    @Override
    public void onBufferingUpdate(IViewTracker viewTracker, int percent) {

    }

    @Override
    public void onVideoStopped(IViewTracker viewTracker) {

    }

    @Override
    public void onVideoReset(IViewTracker viewTracker) {

    }

    @Override
    public void onVideoReleased(IViewTracker viewTracker) {

    }

    @Override
    public void onInfo(IViewTracker viewTracker, int what) {

    }

    @Override
    public void onVideoStarted(IViewTracker viewTracker) {

    }

    @Override
    public void onVideoPaused(IViewTracker viewTracker) {

    }
}
