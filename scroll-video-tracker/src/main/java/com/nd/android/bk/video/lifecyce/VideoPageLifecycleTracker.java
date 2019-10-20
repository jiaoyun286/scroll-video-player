package com.nd.android.bk.video.lifecyce;

import com.nd.android.bk.video.tracker.Tracker;
import com.nd.android.bk.video.utils.Logger;
import com.nd.android.bk.video.videomanager.SingleVideoPlayerManager;
import com.nd.sdp.android.lifecycle.manager.DefaultLifecycleTracker;

/**
 * @author JiaoYun
 * @date 2019/10/20 15:54
 */
public class VideoPageLifecycleTracker extends DefaultLifecycleTracker {
    private final String TAG = "VideoPageLifecycleTracker";
    @Override
    public void onDestroy() {
        Logger.d(TAG,"onDestroy");
        Tracker.destroy();
        SingleVideoPlayerManager.getInstance().release();
        super.onDestroy();
    }
}
