package com.nd.android.bk.video.tracker;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.widget.FrameLayout;

import com.nd.android.bk.video.detector.IScrollDetector;
import com.nd.android.bk.video.meta.MetaData;
import com.nd.android.bk.video.videomanager.controller.IControllerView;

public interface IViewTracker {
    //FollowView滚动后的触的边界
    int NONE_EDGE = 0;
    int TOP_EDGE = 1;
    int BOTTOM_EDGE = 2;
    int LEFT_EDGE = 3;
    int RIGHT_EDGE = 4;

    /**
     * 添加一个FollowView到DecorView
     * @return
     */
    IViewTracker attach();

    /**
     * 将FollowView从DecorView移除
     * @return
     */
    IViewTracker detach();
    /**
     * 隐藏当前 {@link #getFloatLayerView()},不是删除,并且暂停视频
     * 如果存在
     */
    IViewTracker hide();

    /**
     * 现实当前 {@link #getFloatLayerView()},并且开始播放，如果有正在播放的视频
     */
    IViewTracker show();

    /**
     * 释放不需要资源，通常在Actvity被销毁是调用
     */
    IViewTracker destroy();

    /**
     * 提供一个追踪视图给follower view来追踪（需要把旧的移除，再添加新的）
     * @param trackView 被追踪的滚动的视图，也就是视频播放时完全覆盖的视图，通常是一个item中显示占位图的视图
     */
    IViewTracker trackView(View trackView);

    /**
     * 绑定当前{@link #getFollowerView()} 到新的追踪视图
     * @param trackView 新的trackView
     */
    IViewTracker changeTrackView(View trackView);

    /**
     * 绑定一个trackView的滚动监听{@link IScrollDetector}，以便观察到滚动状态改变时可以做些事情
     * @param scrollDetector
     */
    IViewTracker into(IScrollDetector scrollDetector);

    /**
     * 添加一个tracker view 的矩形区域可见性发生改变时的回调监听
     * @param listener rect change listener
     */
    IViewTracker visibleListener(VisibleChangeListener listener);

    /**
     * 添加一个获取视频控制视图的接口
     * @param controllerView
     * @return
     */
    IViewTracker controller(IControllerView controllerView);

    /**
     * 检查follower view 是否已经添加到DecorView
     */
    boolean isAttach();

    /**
     * 获取Tracker的当前滚出屏幕边界
     */
    int getEdge();

    /**
     * 格式化化edge，用于打印可读的log
     */
    String getEdgeString();

    /**
     * 获取TrackView所在的可滚动视图
     * 比如 {@link android.widget.ListView},{@link android.support.v7.widget.RecyclerView}
     */
    View getVerticalScrollView();

    /**
     * 获取被追踪的视图，视频显示区需要跟随他位置变化而滚动
     */
    View getTrackerView();

    /**
     * 获取绑定的视频数据
     */
    MetaData getMetaData();


    int getTrackerViewId();

    /**
     * 获取需要绑定到 {@link #getTrackerView()},并且跟着滚动.
     */
    View getFollowerView();

    /**
     * 获取根视图 {@link #getFollowerView()}, 需要添加到
     * DecorView {@link android.view.Window#ID_ANDROID_CONTENT}的视图
     */
    FloatLayerView getFloatLayerView();

    FrameLayout getVideoTopView();

    FrameLayout getVideoBottomView();


    Context getContext();

    /**
     * 屏幕方向改变是会回到
     * @param newConfig 新的配置参数
     */
    void onConfigurationChanged(Configuration newConfig);

    /**
     * 判断是否是横屏
     */
    boolean isFullScreen();

    /**
     * 切换Activity到横屏
     */
    void toFullScreen();

    /**
     * 切换Activity到竖屏
     */
    void toNormalScreen();

    void muteVideo(boolean mute);

    void startVideo();

    void pauseVideo();

    IControllerView getControllerView();

}
