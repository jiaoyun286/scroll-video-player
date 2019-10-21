package com.nd.android.bk.video.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * @author JiaoYun
 * @date 2019/10/14 22:01
 */
public class Utils {
    /**
     * 判断是否是全面屏
     */
    private volatile static boolean mHasCheckAllScreen;
    private volatile static boolean mIsAllScreenDevice;

    public static int dp2px(Context context, double dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    public static int getDeviceWidth(Activity context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(mDisplayMetrics);
        return mDisplayMetrics.widthPixels;
    }

    public static int getDeviceHeight(Activity context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }

    public static boolean isSystemRotationEnabled(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static int getFullScreenHeight(Activity activity){
        int statuBarHeight = getStatusBarHeight(activity);
        if(!isAllScreenDevice(activity)){
            int[] screenDimen = getScreenDimention(activity);
            int screenHeight = screenDimen[1];
            return screenHeight - statuBarHeight;
        }

        int screenRealHeight = getScreenHeight(activity);
        int activityHeight = getActivityHeight(activity);
        int remainHeight = screenRealHeight - statuBarHeight;
        if(activityHeight == remainHeight){
            return  remainHeight;
        }else {
            return remainHeight - getNavigationBarHeight(activity);
        }

    }

    public static boolean isAllScreenDevice(Context context) {
        if (mHasCheckAllScreen) {
            return mIsAllScreenDevice;
        }
        mHasCheckAllScreen = true;
        mIsAllScreenDevice = false;
        // 低于 API 21的，都不会是全面屏。。。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
            float width, height;
            if (point.x < point.y) {
                width = point.x;
                height = point.y;
            } else {
                width = point.y;
                height = point.x;
            }
            if (height / width >= 1.97f) {
                mIsAllScreenDevice = true;
            }
        }
        return mIsAllScreenDevice;
    }
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;

        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return statusBarHeight;
    }

    public static int[] getScreenDimention(Context context) {
        WindowManager wm = (WindowManager)context.getSystemService("window");
        Display display = wm.getDefaultDisplay();
        int[] cordinary = new int[2];
        if (Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);
            cordinary[0] = size.x;
            cordinary[1] = size.y;
        } else {
            cordinary[0] = display.getWidth();
            cordinary[1] = display.getHeight();
        }

        return cordinary;
    }

    public static int getScreenHeight(Context context){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1){
            wm.getDefaultDisplay().getRealMetrics(metrics);
        }
        return metrics.heightPixels;
    }

    public static int getActivityHeight(Activity activity){
        Rect outRect1 = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        int activityHeight = outRect1.height();
        return activityHeight;

    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

}
