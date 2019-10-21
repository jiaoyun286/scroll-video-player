package com.nd.android.bk.video.videomanager.controller;

import android.view.View;

import com.nd.android.bk.video.tracker.IViewTracker;

public class DefaultControllerView implements IControllerView {

    @Override
    public View videoControllerBar(IViewTracker tracker) {
        return new FullScreenControllerView(tracker.getContext());
    }

    @Override
    public View loadingController(IViewTracker tracker) {
        return new LoadingControllerView(tracker.getContext());
    }



    @Override
    public boolean muteVideo() {
        return false;
    }

    @Override
    public boolean enableAutoRotation() {
        return true;
    }
}
