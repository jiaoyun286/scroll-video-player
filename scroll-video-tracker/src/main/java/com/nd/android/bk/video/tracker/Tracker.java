package com.nd.android.bk.video.tracker;

import android.app.Activity;
import android.support.v4.util.ArrayMap;

import com.nd.android.bk.video.utils.Logger;

import java.util.Map;
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
        IViewTracker iViewTracker = mViewTrackers.get(context);
        if (iViewTracker != null) {
            return iViewTracker.attach();
        }
        IViewTracker tracker = new VideoTracker(context).attach();
        mViewTrackers.put(context, tracker);
        return tracker;
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
            return iViewTracker.destroy();
        }
        return null;
    }

    public static void stopAnyPlayback() {
        Logger.d(TAG,"stopAnyPlayback");
    }

    public static void startVideo() {
    }

    public static void pauseVideo() {
    }
}
