package com.nd.android.bk.video.videomanager;

import android.os.Handler;
import android.os.Looper;

import com.nd.android.bk.video.tracker.IViewTracker;
import com.nd.android.bk.video.utils.Logger;
import com.nd.android.bk.video.videomanager.interfaces.PlayerItemChangeListener;
import com.nd.android.bk.video.videomanager.interfaces.VideoPlayerListener;
import com.nd.android.bk.video.videomanager.interfaces.VideoPlayerManager;
import com.nd.android.bk.video.videomanager.interfaces.VideoPlayerManagerCallback;
import com.nd.android.bk.video.videomanager.message.ClearPlayerInstance;
import com.nd.android.bk.video.videomanager.message.CreateNewPlayerInstance;
import com.nd.android.bk.video.videomanager.message.Pause;
import com.nd.android.bk.video.videomanager.message.Prepare;
import com.nd.android.bk.video.videomanager.message.Release;
import com.nd.android.bk.video.videomanager.message.Reset;
import com.nd.android.bk.video.videomanager.message.SetNewViewForPlayback;
import com.nd.android.bk.video.videomanager.message.SetUrlDataSourceMessage;
import com.nd.android.bk.video.videomanager.message.Start;
import com.nd.android.bk.video.videomanager.message.Stop;
import com.nd.android.bk.video.videomanager.player.VideoPlayerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author JiaoYun
 * @date 2019/10/14 22:10
 */
public class SingleVideoPlayerManager implements VideoPlayerManager<IViewTracker>, VideoPlayerManagerCallback, VideoPlayerListener {
    private static final String TAG = SingleVideoPlayerManager.class.getSimpleName();

    /**
     * 用来处理播放器消息的线程
     */
    private final MessagesHandlerThread mPlayerHandler = new MessagesHandlerThread();
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    private VideoPlayerView mCurrentPlayer = null;
    private PlayerMessageState mCurrentPlayerState = PlayerMessageState.IDLE;

    private List<PlayerItemChangeListener> mPlayerItemChangeListeners = new ArrayList<>();
    private List<VideoPlayerListener> mPendingAddListeners = new ArrayList<>();

    private static SingleVideoPlayerManager mInstance;

    private SingleVideoPlayerManager() {
    }

    public static SingleVideoPlayerManager getInstance() {
        if (mInstance == null) {
            synchronized (SingleVideoPlayerManager.class) {
                mInstance = new SingleVideoPlayerManager();
                return mInstance;
            }
        }
        return mInstance;
    }

    /**
     * 开始播放一个新的video在新的{@link VideoPlayerView}中
     * 1. 停止队列处理，以便在post新消息时保持队列的一致状态
     * 2. 删除队列中的所有监听器和消息(销毁当前播放器)
     * 3. 创建一个新的{@link com.nd.android.bk.video.videomanager.interfaces.IMediaPlayer} 为新的 {@link VideoPlayerView},添加Prepare消息，开始准备播放
     * 5. 重新开始处理队列中的消息
     *
     * @param viewTracker     当前item的track view
     * @param videoPlayerView - 新的视频播放器视图
     */
    @Override
    public void playNewVideo(IViewTracker viewTracker, VideoPlayerView videoPlayerView) {
        Logger.v(TAG, ">> playNewVideo, videoPlayer " + videoPlayerView + ", mCurrentPlayer " + mCurrentPlayer + ", videoPlayerView " + videoPlayerView);

        mPlayerHandler.pauseQueueProcessing(TAG);

        startNewPlayback(viewTracker, videoPlayerView);

        mPlayerHandler.resumeQueueProcessing(TAG);

        Logger.v(TAG, "<< playNewVideo, videoPlayer " + videoPlayerView);
    }

    private void startNewPlayback(IViewTracker viewTracker, VideoPlayerView videoPlayerView) {

        // 添加新的播放器监听
        videoPlayerView.addMediaPlayerListener(this);

        //清除所有被挂起的消息
        mPlayerHandler.clearAllPendingMessages(TAG);

        //重置并释放当前播放器
        stopResetReleaseClearCurrentPlayer();

        //开始播放新的
        setNewViewForPlaybackAndPlay(viewTracker, videoPlayerView);
    }

    /**
     * 停止一个存在的播放任务
     */
    @Override
    public void stopAnyPlayback() {
        Logger.v(TAG, ">> stopAnyPlayback, mCurrentPlayerState " + mCurrentPlayerState);
        mPlayerHandler.pauseQueueProcessing(TAG);
        Logger.v(TAG, "stopAnyPlayback, mCurrentPlayerState " + mCurrentPlayerState);

        mPlayerHandler.clearAllPendingMessages(TAG);

        stopResetReleaseClearCurrentPlayer();

        mPlayerHandler.resumeQueueProcessing(TAG);

        Logger.v(TAG, "<< stopAnyPlayback, mCurrentPlayerState " + mCurrentPlayerState);
    }

    /**
     * 停止当前播放任务并重置播放器，当我们不需要他的时候调用
     */
    @Override
    public void resetMediaPlayer() {
        Logger.v(TAG, ">> resetMediaPlayer, mCurrentPlayerState " + mCurrentPlayerState);

        mPlayerHandler.pauseQueueProcessing(TAG);
        Logger.v(TAG, "resetMediaPlayer, mCurrentPlayerState " + mCurrentPlayerState);
        mPlayerHandler.clearAllPendingMessages(TAG);
        resetReleaseClearCurrentPlayer();

        mPlayerHandler.resumeQueueProcessing(TAG);

        Logger.v(TAG, "<< resetMediaPlayer, mCurrentPlayerState " + mCurrentPlayerState);
    }

    @Override
    public void startVideo() {
        Logger.v(TAG, ">> startVideo, mCurrentPlayerState " + mCurrentPlayerState);
        mPlayerHandler.pauseQueueProcessing(TAG);
        Logger.v(TAG, "startVideo, mCurrentPlayerState " + mCurrentPlayerState);

        switch (mCurrentPlayerState) {
            case STARTING:
            case STARTED:
            case PAUSING:
            case PAUSED:
            case STOPPING:
            case STOPPED:
                mPlayerHandler.addMessage(new Start(mCurrentPlayer, this));
                break;
        }

        mPlayerHandler.resumeQueueProcessing(TAG);

        Logger.v(TAG, "<< startVideo, mCurrentPlayerState " + mCurrentPlayerState);
    }

    @Override
    public void pauseVideo() {
        Logger.v(TAG, ">> pauseVideo, mCurrentPlayerState " + mCurrentPlayerState);

        mPlayerHandler.pauseQueueProcessing(TAG);
        Logger.v(TAG, "pauseVideo, mCurrentPlayerState " + mCurrentPlayerState);

        switch (mCurrentPlayerState) {
            case STARTING:
            case STARTED:
            case PREPARED:
            case PAUSING:
            case PAUSED:
                mPlayerHandler.addMessage(new Pause(mCurrentPlayer, this));
                break;
        }

        mPlayerHandler.resumeQueueProcessing(TAG);

        Logger.v(TAG, "<< pauseVideo, mCurrentPlayerState " + mCurrentPlayerState);
    }

    /**
     * 当当前播放器被停止，并且新的播放器被集合时，这个方法发送的消息用于设置新的播放器，而且最终会调用{@link PlayerItemChangeListener#onPlayerItemChanged(IViewTracker)}
     */
    private void setNewViewForPlaybackAndPlay(IViewTracker viewTracker, VideoPlayerView videoPlayerView) {
        Logger.v(TAG, "setNewViewForPlaybackAndPlay, viewTracker " + viewTracker + ", videoPlayer " + videoPlayerView);
        mPlayerHandler.addMessages(Arrays.asList(
                new SetNewViewForPlayback(viewTracker, videoPlayerView, this),
                new CreateNewPlayerInstance(videoPlayerView, this),
                new SetUrlDataSourceMessage(videoPlayerView, viewTracker.getMetaData(), this),
                new Prepare(videoPlayerView, this)
        ));
    }

    /**
     * post一组消息到 {@link MessagesHandlerThread}，停止当前的播放
     */
    private void stopResetReleaseClearCurrentPlayer() {
        Logger.v(TAG, "stopResetReleaseClearCurrentPlayer, mCurrentPlayerState " + mCurrentPlayerState + ", mCurrentPlayer " + mCurrentPlayer);

        if(mCurrentPlayerState == null){
            return;
        }
        switch (mCurrentPlayerState) {
            case SETTING_NEW_PLAYER:
            case IDLE:

            case CREATING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CREATED:

            case CLEARING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CLEARED:
                // 这些状态下，播放器是stopped
                break;
            case INITIALIZED:
            case PREPARING:
            case PREPARED:
            case STARTING:
            case STARTED:
            case PAUSING:
            case PAUSED:
                mPlayerHandler.addMessage(new Stop(mCurrentPlayer, this));
            case SETTING_DATA_SOURCE:
            case DATA_SOURCE_SET:
                /** 处于这个状态时必须reset player, 否则 {@link android.media.MediaPlayer.OnVideoSizeChangedListener}接口方法中传递参数永远是0.*/
            case STOPPING:
            case STOPPED:
            case ERROR:
            case PLAYBACK_COMPLETED:
                mPlayerHandler.addMessage(new Reset(mCurrentPlayer, this));
            case RESETTING:
            case RESET:
                mPlayerHandler.addMessage(new Release(mCurrentPlayer, this));
            case RELEASING:
            case RELEASED:
                mPlayerHandler.addMessage(new ClearPlayerInstance(mCurrentPlayer, this));

                break;
            case END:
                throw new RuntimeException("unhandled " + mCurrentPlayerState);
        }
    }

    private void resetReleaseClearCurrentPlayer() {
        Logger.v(TAG, "resetReleaseClearCurrentPlayer, mCurrentPlayerState " + mCurrentPlayerState + ", mCurrentPlayer " + mCurrentPlayer);

        switch (mCurrentPlayerState) {
            case SETTING_NEW_PLAYER:
            case IDLE:

            case CREATING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CREATED:

            case SETTING_DATA_SOURCE:
            case DATA_SOURCE_SET:

            case CLEARING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CLEARED:
                break;
            case INITIALIZED:
            case PREPARING:
            case PREPARED:
            case STARTING:
            case STARTED:
            case PAUSING:
            case PAUSED:
            case STOPPING:
            case STOPPED:
            case ERROR:
            case PLAYBACK_COMPLETED:
                mPlayerHandler.addMessage(new Reset(mCurrentPlayer, this));
            case RESETTING:
            case RESET:
                mPlayerHandler.addMessage(new Release(mCurrentPlayer, this));
            case RELEASING:
            case RELEASED:
                mPlayerHandler.addMessage(new ClearPlayerInstance(mCurrentPlayer, this));

                break;
            case END:
                throw new RuntimeException("unhandled " + mCurrentPlayerState);
        }
    }

    /**
     * 当新的播放器被激活是，被 {@link SetNewViewForPlayback}调用.
     * 并将参数{@link com.nd.android.bk.video.tracker.ViewTracker} 传递给 {@link #mPlayerItemChangeListeners}
     */
    @Override
    public void setCurrentItem(final IViewTracker viewTracker, VideoPlayerView videoPlayerView) {
        Logger.v(TAG, ">> onPlayerItemChanged");

        mCurrentPlayer = videoPlayerView;
        mCurrentPlayer.setViewTracker(viewTracker);

        for (VideoPlayerListener listener : mPendingAddListeners) {
            mCurrentPlayer.addMediaPlayerListener(listener);
        }

        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (PlayerItemChangeListener listener : mPlayerItemChangeListeners) {
                    listener.onPlayerItemChanged(viewTracker);
                }
            }
        });

        Logger.v(TAG, "<< onPlayerItemChanged");
    }

    @Override
    public void updateVideoPlayerState(VideoPlayerView videoPlayerView, PlayerMessageState playerMessageState) {
        Logger.v(TAG, ">> updateVideoPlayerState, playerMessageState " + playerMessageState + ", videoPlayer " + videoPlayerView);

        mCurrentPlayerState = playerMessageState;

        //clear listener when player instance cleared
        if (playerMessageState == PlayerMessageState.PLAYER_INSTANCE_CLEARED && mCurrentPlayer != null) {
            mCurrentPlayer.removeAllPlayerListener();
        }

        Logger.v(TAG, "<< updateVideoPlayerState, playerMessageState " + playerMessageState + ", videoPlayer " + videoPlayerView);
    }

    @Override
    public PlayerMessageState getCurrentPlayerState() {
        Logger.v(TAG, "getCurrentPlayerState, mCurrentPlayerState " + mCurrentPlayerState);
        return mCurrentPlayerState;
    }

    @Override
    public void onVideoSizeChanged(IViewTracker viewTracker, int width, int height) {
    }

    @Override
    public void onVideoPrepared(IViewTracker viewTracker) {
        Logger.v(TAG, "onVideoPrepared tracker" + viewTracker);
    }

    @Override
    public void onVideoCompletion(IViewTracker viewTracker) {
        mCurrentPlayerState = PlayerMessageState.PLAYBACK_COMPLETED;
    }

    @Override
    public void onError(IViewTracker viewTracker, int what, int extra) {
        Logger.v(TAG, "onError, what " + what + ", extra " + extra);
        mCurrentPlayerState = PlayerMessageState.ERROR;
    }

    @Override
    public void onBufferingUpdate(IViewTracker viewTracker, int percent) {
    }

    @Override
    public void onVideoStopped(IViewTracker viewTracker) {
        Logger.v(TAG, "onVideoStopped tracker " + viewTracker);
    }

    @Override
    public void onVideoReset(IViewTracker viewTracker) {
        Logger.v(TAG, "onVideoReset tracker " + viewTracker);
    }

    @Override
    public void onVideoReleased(IViewTracker viewTracker) {
        Logger.v(TAG, "onVideoReleased tracker " + viewTracker);
    }

    @Override
    public void onInfo(IViewTracker viewTracker, int what) {
        Logger.v(TAG, "onInfo tracker " + viewTracker);
    }

    @Override
    public void onVideoStarted(IViewTracker viewTracker) {
        Logger.v(TAG, "onVideoStarted tracker " + viewTracker);
    }

    @Override
    public void onVideoPaused(IViewTracker viewTracker) {
        Logger.v(TAG, "onVideoPaused tracker " + viewTracker);
    }

    public void addPlayerItemChangeListener(PlayerItemChangeListener playerItemChangeListener) {
        mPlayerItemChangeListeners.add(playerItemChangeListener);
    }

    public void removePlayerItemChangeListener(PlayerItemChangeListener playerItemChangeListener) {
        mPlayerItemChangeListeners.remove(playerItemChangeListener);
    }

    public void removeAllPlayerItemChangeListeners() {
        mPlayerItemChangeListeners.clear();
    }

    public void addVideoPlayerListener(VideoPlayerListener videoPlayerListener) {
        mPendingAddListeners.add(videoPlayerListener);
    }

    public void removeVideoPlayerListener(VideoPlayerListener videoPlayerListener) {
        mPendingAddListeners.remove(videoPlayerListener);
    }

    public void removeAllVideoPlayerListeners() {
        mPendingAddListeners.clear();
    }

    public void release(){
        mPendingAddListeners.clear();
        mPlayerItemChangeListeners.clear();
        if(mCurrentPlayer != null){
            mCurrentPlayer.removeAllPlayerListener();
            mCurrentPlayer = null;
        }
        mCurrentPlayerState = null;
    }


}
