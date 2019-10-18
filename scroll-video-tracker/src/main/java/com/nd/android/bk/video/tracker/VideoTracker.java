package com.nd.android.bk.video.tracker;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.SimpleArrayMap;
import android.view.View;
import android.view.ViewGroup;

import com.nd.android.bk.video.meta.MetaData;
import com.nd.android.bk.video.utils.DrawableTask;
import com.nd.android.bk.video.utils.Logger;
import com.nd.android.bk.video.utils.OrientationDetector;
import com.nd.android.bk.video.utils.Utils;
import com.nd.android.bk.video.videomanager.controller.BaseControllerView;
import com.nd.android.bk.video.videomanager.controller.IControllerView;
import com.nd.android.bk.video.videomanager.interfaces.PlayerItemChangeListener;
import com.nd.android.bk.video.videomanager.player.VideoPlayerView;

public class VideoTracker extends ViewTracker implements PlayerItemChangeListener,
        DrawableTask.Callback,
        OrientationDetector.OnOrientationChangedListener {

    private static final String TAG = VideoTracker.class.getSimpleName();
    private DrawableTask mDrawableTask = new DrawableTask(this);
    private SimpleArrayMap<Object, BitmapDrawable> mCachedDrawables = new SimpleArrayMap<>();
    private OrientationDetector mOrientationDetector;
    private VideoPlayerView mVideoPlayView;
    private View mLoadingControllerView;
    private BaseControllerView mNormalScreenControllerView;
    private BaseControllerView mFullScreenControllerView;
    public VideoTracker(Activity context) {
        super(context);
    }

    @Override
    public IViewTracker attach() {
        super.attach();
        keepScreenOn(true);
        mVideoPlayView = mFloatLayerView.getVideoPlayerView();
        mVideoPlayView.refreshSurfaceTexture(0, 0);
        mVideoPlayView.setAlpha(0f);
        return super.attach();
    }

    @Override
    public IViewTracker detach() {
        IViewTracker tracker = super.detach();
        keepScreenOn(false);
        Tracker.stopAnyPlayback();
        return tracker;
    }

    @Override
    public IViewTracker hide() {
        Logger.d(TAG,"hide");
        pauseVideo();
        return super.hide();
    }

    @Override
    public IViewTracker show() {
        Logger.d(TAG,"show");
        startVideo();
        return super.show();
    }

    @Override
    public MetaData getMetaData() {
        return mMetaData;
    }


    @Override
    public void onPlayerItemChanged(IViewTracker viewTracker) {
        Logger.d(TAG,"onPlayerItemChanged");
    }

    @Override
    public void done(Object key, BitmapDrawable drawable) {
        mCachedDrawables.put(key, drawable);
        mVideoBottomView.setBackgroundDrawable(drawable);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (Utils.isSystemRotationEnabled(mContext) && isAttach) {
            switch (orientation) {
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    toNormalScreen();
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    toFullScreen();
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    break;
            }
        }

    }

    @Override
    public IViewTracker controller(IControllerView controllerView) {
        super.controller(controllerView);
        if (mControllerView.enableAutoRotation()) {// 自动转换屏幕方向
            if (mOrientationDetector == null) {
                mOrientationDetector = new OrientationDetector(mContext, this);
                mOrientationDetector.enable(true);
            }
        } else {
            if (mOrientationDetector != null) {
                mOrientationDetector.enable(false);
            }
            mOrientationDetector = null;
        }
        handleControllerView(controllerView);
        return this;
    }

    private void handleControllerView(IControllerView controllerView) {
        if(mLoadingControllerView != null && mLoadingControllerView.getParent() != null){
            ((ViewGroup) mLoadingControllerView.getParent()).removeView(mLoadingControllerView);
        }

        if(mNormalScreenControllerView != null && mNormalScreenControllerView.getParent() != null){
            ((ViewGroup) mNormalScreenControllerView.getParent()).removeView(mNormalScreenControllerView);
        }

        if(mFullScreenControllerView != null && mFullScreenControllerView.getParent() != null){
            ((ViewGroup) mFullScreenControllerView.getParent()).removeView(mFullScreenControllerView);
        }
        if(mLoadingControllerView == null){
            mLoadingControllerView = controllerView.loadingController(this);
        }

        if(mNormalScreenControllerView == null){
            mNormalScreenControllerView = (BaseControllerView) controllerView.normalScreenController(this);
        }

        if(mFullScreenControllerView == null){
            mFullScreenControllerView = (BaseControllerView) controllerView.fullScreenController(this);
        }
    }

    @Override
    public void muteVideo(boolean mute) {
        Logger.d(TAG,"muteVideo mute = " + mute);
        mVideoPlayView.muteVideo(mute);

    }

    @Override
    public void startVideo() {
        Logger.d(TAG,"startVideo");
        Tracker.startVideo();

    }

    @Override
    public void pauseVideo() {
        Logger.d(TAG,"pauseVideo");
        Tracker.pauseVideo();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isFullScreen()) {
            addFullScreenView();
        } else {
            addNormalScreenView();
        }
    }

    public void addNormalScreenView() {
        mVideoTopView.removeView(mFullScreenControllerView);
        if(mNormalScreenControllerView.getParent() == null) {
            mVideoTopView.addView(mNormalScreenControllerView);
        }
        mNormalScreenControllerView.setViewTracker(this);
    }

    private void addFullScreenView() {
    }
}
