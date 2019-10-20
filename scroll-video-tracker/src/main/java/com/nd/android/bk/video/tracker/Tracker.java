package com.nd.android.bk.video.tracker;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.view.View;

import com.nd.android.bk.video.lifecyce.VideoPageLifecycleTracker;
import com.nd.android.bk.video.utils.Logger;
import com.nd.android.bk.video.videomanager.SingleVideoPlayerManager;
import com.nd.android.bk.video.videomanager.interfaces.PlayerItemChangeListener;
import com.nd.android.bk.video.videomanager.interfaces.VideoPlayerListener;
import com.nd.android.bk.video.videomanager.player.VideoPlayerView;
import com.nd.sdp.android.lifecycle.manager.LifecycleBinder;
import com.nd.sdp.android.lifecycle.manager.LifecycleManager;

import java.util.WeakHashMap;

/**
 * @author JiaoYun
 * @date 2019/10/14 22:07
 */
public class Tracker {
    private static final String TAG = "Tracker";

    private static WeakHashMap<Activity, IViewTracker> mViewTrackers = new WeakHashMap<>();

    /**
     * 关联一个Activity和一个 {@link IViewTracker},并且绑定
     * follower view 到 DecorView
     * @param context activity
     * @return IViewTracker
     */
    public static IViewTracker attach(Activity context) {
        LifecycleManager lifecycleManager = LifecycleBinder.with(context);
        if(lifecycleManager.getTracker() == null){
            lifecycleManager.setTracker(new VideoPageLifecycleTracker());
        }

        IViewTracker iViewTracker = mViewTrackers.get(context);
        if (iViewTracker != null) {
            return iViewTracker.attach();
        }
        IViewTracker tracker = new VideoTracker(context).attach();
        mViewTrackers.put(context, tracker);
        return tracker;
    }


    public static IViewTracker attach(Fragment fragment){
        LifecycleBinder.with(fragment).setTracker(new VideoPageLifecycleTracker());
        return attach(fragment.getActivity());
    }

    public static IViewTracker attach(android.support.v4.app.Fragment fragment){
        LifecycleBinder.with(fragment).setTracker(new VideoPageLifecycleTracker());
        return attach(fragment.getActivity());
    }


    /**
     * 从DecorView中移除follower视图,但是不移除{@link IViewTracker}
     * 从 {@link #mViewTrackers} 集合, 以备后面需要重新 attach tracker view
     * @param context activity
     * @return IViewTracker
     */
    public static IViewTracker detach(Activity context) {
        IViewTracker iViewTracker = mViewTrackers.get(context);
        if (iViewTracker != null) {
            return iViewTracker.detach();
        }
        return null;
    }

    /**
     * 移除 follower view 和 {@link IViewTracker}
     * 并且释放它所持有的所有实例, 意味不在需要{@link IViewTracker}.通常在Activity销毁时调用
     * @param context activity
     * @return IViewTracker
     */
    public static IViewTracker destroy(Activity context){
        IViewTracker iViewTracker = mViewTrackers.remove(context);
        if (iViewTracker != null) {
            return  iViewTracker.destroy();
        }
        return null;
    }

    public static void destroy(){
        for(Activity key : mViewTrackers.keySet()){
            IViewTracker iViewTracker = mViewTrackers.get(key);
            if (iViewTracker != null) {
                iViewTracker.destroy();
            }
        }
        mViewTrackers.clear();
    }

    /**
     * 检查follower view 是否已经 attach 到 DecorView
     * @param context activity
     * @return IViewTracker
     */
    public static boolean isAttach(Activity context){
        IViewTracker iViewTracker = mViewTrackers.get(context);
        if (iViewTracker != null) {
            return iViewTracker.isAttach();
        }
        return false;
    }


    /**
     * 检查tracher view是否同一个
     * @param context
     * @param newTracker
     * @return
     */
    public static boolean isSameTrackerView(Activity context, View newTracker){
        IViewTracker iViewTracker = mViewTrackers.get(context);
        if(iViewTracker != null && iViewTracker.getTrackerView() != null){
            return iViewTracker.getTrackerView().equals(newTracker) && iViewTracker.isAttach();
        }
        return false;
    }

    /**
     * 变更track view，重新绑定{@link FloatLayerView}
     * @param context
     * @param trackView
     */
    public static void changeTrackView(Activity context,View trackView){
        if(getViewTracker(context) != null){
            getViewTracker(context).changeTrackView(trackView);
        }
    }

    /**
     * 屏幕方向配置发生改变时调用，来自动切换播放器的横竖屏
     * @param context 配置发生改变Activity
     * @param newConfig 配置相关参数
     */
    public static void onConfigurationChanged(Activity context, Configuration newConfig){
        if(getViewTracker(context) != null){
            getViewTracker(context).onConfigurationChanged(newConfig);
        }
    }

    public static IViewTracker getViewTracker(Activity context){
        return mViewTrackers.get(context);
    }

    public static void stopAnyPlayback() {
        Logger.d(TAG,"stopAnyPlayback");
        SingleVideoPlayerManager.getInstance().stopAnyPlayback();
    }

    public static void resetMediaPlayer(){
        Logger.d(TAG,"resetMediaPlayer");
        SingleVideoPlayerManager.getInstance().resetMediaPlayer();
    }

    public static void startVideo() {
        SingleVideoPlayerManager.getInstance().startVideo();
    }

    public static void pauseVideo() {
        SingleVideoPlayerManager.getInstance().pauseVideo();
    }

    public static void addVideoPlayerListener(VideoPlayerListener videoPlayerListener) {
        SingleVideoPlayerManager.getInstance().addVideoPlayerListener(videoPlayerListener);
    }
    public static void addPlayerItemChangeListener(PlayerItemChangeListener playerItemChangeListener) {
        SingleVideoPlayerManager.getInstance().addPlayerItemChangeListener(playerItemChangeListener);
    }

    public static void removePlayerItemChangeListener(PlayerItemChangeListener playerItemChangeListener) {
        SingleVideoPlayerManager.getInstance().removePlayerItemChangeListener(playerItemChangeListener);
    }

    public static void removeVideoPlayerListener(VideoPlayerListener videoPlayerListener) {
        SingleVideoPlayerManager.getInstance().removeVideoPlayerListener(videoPlayerListener);
    }

    public static void removeAllVideoPlayerListeners() {
        SingleVideoPlayerManager.getInstance().removeAllVideoPlayerListeners();
    }

    public static void removePlayerItemChangeListeners() {
        SingleVideoPlayerManager.getInstance().removeAllPlayerItemChangeListeners();
    }


    public static void playNewVideo(IViewTracker viewTracker, VideoPlayerView videoPlayerView) {
        SingleVideoPlayerManager.getInstance().playNewVideo(viewTracker, videoPlayerView);
    }
}
