package com.nd.android.bk.video.videomanager.interfaces;

import android.view.View;
import android.view.ViewGroup;

import com.nd.android.bk.video.tracker.IViewTracker;

public interface IFindNextTrackView {
    /**
     * 获取下一个完全可见的TrackView
     * 获取策略根据followView位移时触边方向确定，顶部触边，则从上向下第一个完全可见item为下个TrackView，
     * 底部触边，则从下向上第一个完全可见item为下个TrackView
     * @param listView
     */
    View getNextTrackerView(ViewGroup listView, IViewTracker tracker);
}
