package com.nd.android.bk.video.utils;

import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.nd.android.bk.video.tracker.IViewTracker;
import com.nd.android.bk.video.videomanager.interfaces.IFindNextTrackView;

/**
 * @author JiaoYun
 * @date 2019/10/14 21:37
 */
public class DefaultFindNextTrackView implements IFindNextTrackView {
    private  final String TAG = "DefaultFindNextTrackView";


    @Override
    public  View getNextTrackerView(ViewGroup listView, IViewTracker tracker) {

        if (listView == null) {
            return null;
        }
        int childCount = listView.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View itemView = listView.getChildAt(i);
            if (itemView == null) {
                return null;
            }
            View container = itemView.findViewById(tracker.getTrackerViewId());
            if (container == null) {
                continue;
            }
            Rect rect = new Rect();
            container.getLocalVisibleRect(rect);
            Logger.d(TAG, "getNextTrackerView Bottom: item = " + i
                    + " rectLocal : " + rect);
            if (rect.bottom >= 0 && rect.left == 0 && rect.top == 0) {
                if (rect.bottom - rect.top == container.getHeight()) {
                    return container;
                }
            }

//        int edge = tracker.getEdge();
//        Log.d(TAG,"edge = " + edge);
//        switch (edge) {
//            case IViewTracker.TOP_EDGE:
//            case IViewTracker.RIGHT_EDGE:
//            case IViewTracker.LEFT_EDGE:
//                for (int i = 0; i < childCount; i++) {
//                    View itemView = listView.getChildAt(i);
//                    if (itemView == null) {
//                        return null;
//                    }
//                    View container = itemView.findViewById(tracker.getTrackerViewId());
//                    if (container == null) {
//                        continue;
//                    }
//                    Rect rect = new Rect();
//                    container.getLocalVisibleRect(rect);
//                    Log.e(TAG, "getNextTrackerView Bottom: item = " + i
//                            + " rectLocal : " + rect);
//                    if (rect.bottom >= 0 && rect.left == 0 && rect.top == 0) {
//                        if (rect.bottom - rect.top == container.getHeight()) {
//                            return container;
//                        }
//                    }
//                }
//                break;
//            case IViewTracker.BOTTOM_EDGE:
//                for (int i = childCount - 1; i >= 0; i--) {
//                    View itemView = listView.getChildAt(i);
//                    if (itemView == null) {
//                        return null;
//                    }
//                    View container = itemView.findViewById(tracker.getTrackerViewId());
//                    if (container == null) {
//                        continue;
//                    }
//                    Rect rect = new Rect();
//                    container.getLocalVisibleRect(rect);
//                    Log.e(TAG, "getNextTrackerView Bottom: item = " + i
//                            + " rectLocal : " + rect);
//                    if (rect.left == 0 && rect.top == 0) {
//                        if (rect.bottom - rect.top == container.getHeight()) {
//                            return container;
//                        }
//                    }
//                }
//                break;
        }
        return null;
    }

//    /**
//     * 获取ListView中完全可见的item的高度
//     *
//     * @param listView
//     * @param coverId
//     * @return item view
//     */
//    public static View getRelativeMostVisibleItemView(ViewGroup listView, @IdRes int coverId) {
//        if (listView == null) {
//            return null;
//        }
//        int childCount = listView.getChildCount();
//        int mostVisibleItemIndex = -1;
//        int maxVisibleHeight = 0;
//        for (int i = 0; i < childCount; i++) {
//            View itemView = listView.getChildAt(i);
//            if (itemView == null) {
//                return null;
//            }
//            View container = itemView.findViewById(coverId);
//            if (container == null) {
//                continue;
//            }
//            Rect rect = new Rect();
//            container.getLocalVisibleRect(rect);
//            if (rect.bottom >= 0 && rect.left == 0 && rect.top == 0) {
//                int visibleHeight = rect.bottom - rect.top;
//                if (maxVisibleHeight < visibleHeight) {
//                    maxVisibleHeight = visibleHeight;
//                    mostVisibleItemIndex = i;
//                }
//            }
//        }
//        return listView.getChildAt(mostVisibleItemIndex);
//    }
}
