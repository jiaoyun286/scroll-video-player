package com.nd.android.bk.video.videomanager.interfaces;

import com.nd.android.bk.video.tracker.IViewTracker;
import com.nd.android.bk.video.videomanager.PlayerMessageState;
import com.nd.android.bk.video.videomanager.player.VideoPlayerView;

/**
 * @author JiaoYun
 * @date 2019/10/14 20:49
 */
public interface VideoPlayerManagerCallback {
    void setCurrentItem(IViewTracker viewTracker, VideoPlayerView newPlayerView);

    void updateVideoPlayerState(VideoPlayerView videoPlayerView, PlayerMessageState playerMessageState);

    PlayerMessageState getCurrentPlayerState();
}
