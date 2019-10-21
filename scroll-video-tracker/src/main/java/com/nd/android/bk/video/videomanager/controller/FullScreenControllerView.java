package com.nd.android.bk.video.videomanager.controller;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import com.nd.android.bk.video.R;
import com.nd.android.bk.video.tracker.IViewTracker;
import com.nd.android.bk.video.tracker.Tracker;

/**
 * @author JiaoYun
 * @date 2019/10/14 22:17
 */
public class FullScreenControllerView extends BaseControllerView {
    private VideoControllerView mControllerView;

    public FullScreenControllerView(Context context) {
        super(context);
    }

    public FullScreenControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {

    }

    @Override
    public void setViewTracker(IViewTracker viewTracker) {
        super.setViewTracker(viewTracker);
        if(mControllerView == null){
            mControllerView = new VideoControllerView.Builder((Activity) viewTracker.getContext(), mPlayerControlListener)
                    .withVideoView(viewTracker.getFollowerView())
                    .canControlBrightness(true)
                    .canControlVolume(true)
                    .canSeekVideo(false)
                    .exitIcon(R.drawable.video_top_back)
                    .pauseIcon(R.drawable.ic_media_pause_small)
                    .playIcon(R.drawable.ic_media_play_small)
                    .shrinkIcon(R.drawable.ic_media_fullscreen_shrink)
                    .stretchIcon(R.drawable.ic_media_fullscreen_stretch)
                    .build(this);

        }
    }

    @Override
    protected void attachWindow(boolean attach) {

    }

    private VideoControllerView.MediaPlayerControlListener mPlayerControlListener = new VideoControllerView.MediaPlayerControlListener() {
        @Override
        public void start() {
            Tracker.startVideo();
        }

        @Override
        public void pause() {
            Tracker.pauseVideo();
        }

        @Override
        public int getDuration() {
            return mVideoPlayerView.getDuration();
        }

        @Override
        public int getCurrentPosition() {
            return mVideoPlayerView.getCurrentPosition();
        }

        @Override
        public void seekTo(int position) {
            mVideoPlayerView.seekTo(position);
        }

        @Override
        public boolean isPlaying() {
            return mVideoPlayerView.isPlaying();
        }

        @Override
        public boolean isComplete() {
            return mVideoPlayerView.isComplete();
        }

        @Override
        public int getBufferPercentage() {
            return mVideoPlayerView.getCurrentBuffer();
        }

        @Override
        public boolean isFullScreen() {
            return mViewTracker.isFullScreen();
        }

        @Override
        public void toggleFullScreen() {
            if (isFullScreen()) {
                mViewTracker.toNormalScreen();
            } else {
                mViewTracker.toFullScreen();
            }
        }

        @Override
        public void exit() {
            if (isFullScreen()) {
                mViewTracker.toNormalScreen();
            }
        }

        @Override
        public void setSpeed(float speed) {
            mVideoPlayerView.setSpeed(speed);
        }


        @Override
        public boolean isPaused() {
            return mVideoPlayerView.isPaused();
        }
    };
}
