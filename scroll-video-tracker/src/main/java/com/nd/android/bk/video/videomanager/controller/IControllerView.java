package com.nd.android.bk.video.videomanager.controller;

import android.view.View;

import com.nd.android.bk.video.tracker.IViewTracker;

/**
 * @author JiaoYun
 * @date 2019/10/14 21:08
 */
public interface IControllerView {


    View videoControllerBar(IViewTracker tracker);


    View loadingController(IViewTracker tracker);




    boolean muteVideo();

    boolean enableAutoRotation();
}
