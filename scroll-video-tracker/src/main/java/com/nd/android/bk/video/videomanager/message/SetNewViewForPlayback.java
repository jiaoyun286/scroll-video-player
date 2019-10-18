package com.nd.android.bk.video.videomanager.message;

import com.nd.android.bk.video.tracker.IViewTracker;
import com.nd.android.bk.video.videomanager.PlayerMessageState;
import com.nd.android.bk.video.videomanager.interfaces.VideoPlayerManagerCallback;
import com.nd.android.bk.video.videomanager.player.VideoPlayerView;

/**
 * @author JiaoYun
 * @date 2019/10/14 22:13
 */
public class SetNewViewForPlayback extends PlayerMessage {
    private final IViewTracker mViewTracker;
    private final VideoPlayerView mCurrentPlayer;
    private final VideoPlayerManagerCallback mCallback;

    public SetNewViewForPlayback(IViewTracker viewTracker, VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
        mViewTracker = viewTracker;
        mCurrentPlayer = videoPlayerView;
        mCallback = callback;
    }

    @Override
    public String toString() {
        return SetNewViewForPlayback.class.getSimpleName() + ", mCurrentPlayer " + mCurrentPlayer;
    }

    @Override
    protected void performAction(VideoPlayerView currentPlayer) {
        mCallback.setCurrentItem(mViewTracker, mCurrentPlayer);
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.SETTING_NEW_PLAYER;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.IDLE;
    }
}
